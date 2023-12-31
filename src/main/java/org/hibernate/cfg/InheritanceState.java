package org.hibernate.cfg;

import org.hibernate.AnnotationException;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.mapping.PersistentClass;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/*
 * 建立此類別的目的是因為希望JPA在建立表格時生成的欄位可以依照Entity父類別的field順序，故額外從Hibernate的部分內容取出，
 * 以JAVA的機制會優先讀取這個類以取代Hibernate包的InheritanceState
 * 1.更好的做法是自定義一個JPA依賴，但也有礙於時間成本以及維護性的問題
 */
public class InheritanceState {
    private XClass clazz;
    private boolean hasSiblings = false;
    private boolean hasParents = false;
    private InheritanceType type;
    private boolean isEmbeddableSuperclass = false;
    private Map<XClass, InheritanceState> inheritanceStatePerClass;
    private List<XClass> classesToProcessForMappedSuperclass = new ArrayList();
    private MetadataBuildingContext buildingContext;
    private AccessType accessType;
    private ElementsToProcess elementsToProcess;
    private Boolean hasIdClassOrEmbeddedId;

    public InheritanceState(XClass clazz, Map<XClass, InheritanceState> inheritanceStatePerClass, MetadataBuildingContext buildingContext) {
        this.setClazz(clazz);
        this.buildingContext = buildingContext;
        this.inheritanceStatePerClass = inheritanceStatePerClass;
        this.extractInheritanceType();
    }

    private void extractInheritanceType() {
        XAnnotatedElement element = this.getClazz();
        Inheritance inhAnn = (Inheritance)element.getAnnotation(Inheritance.class);
        MappedSuperclass mappedSuperClass = (MappedSuperclass)element.getAnnotation(MappedSuperclass.class);
        if (mappedSuperClass != null) {
            this.setEmbeddableSuperclass(true);
            this.setType(inhAnn == null ? null : inhAnn.strategy());
        } else {
            this.setType(inhAnn == null ? InheritanceType.SINGLE_TABLE : inhAnn.strategy());
        }

    }

    boolean hasTable() {
        return !this.hasParents() || !InheritanceType.SINGLE_TABLE.equals(this.getType());
    }

    boolean hasDenormalizedTable() {
        return this.hasParents() && InheritanceType.TABLE_PER_CLASS.equals(this.getType());
    }

    public static InheritanceState getInheritanceStateOfSuperEntity(XClass clazz, Map<XClass, InheritanceState> states) {
        XClass superclass = clazz;

        do {
            superclass = superclass.getSuperclass();
            InheritanceState currentState = (InheritanceState)states.get(superclass);
            if (currentState != null && !currentState.isEmbeddableSuperclass()) {
                return currentState;
            }
        } while(superclass != null && !Object.class.getName().equals(superclass.getName()));

        return null;
    }

    public static InheritanceState getSuperclassInheritanceState(XClass clazz, Map<XClass, InheritanceState> states) {
        XClass superclass = clazz;

        do {
            superclass = superclass.getSuperclass();
            InheritanceState currentState = (InheritanceState)states.get(superclass);
            if (currentState != null) {
                return currentState;
            }
        } while(superclass != null && !Object.class.getName().equals(superclass.getName()));

        return null;
    }

    public XClass getClazz() {
        return this.clazz;
    }

    public void setClazz(XClass clazz) {
        this.clazz = clazz;
    }

    public boolean hasSiblings() {
        return this.hasSiblings;
    }

    public void setHasSiblings(boolean hasSiblings) {
        this.hasSiblings = hasSiblings;
    }

    public boolean hasParents() {
        return this.hasParents;
    }

    public void setHasParents(boolean hasParents) {
        this.hasParents = hasParents;
    }

    public InheritanceType getType() {
        return this.type;
    }

    public void setType(InheritanceType type) {
        this.type = type;
    }

    public boolean isEmbeddableSuperclass() {
        return this.isEmbeddableSuperclass;
    }

    public void setEmbeddableSuperclass(boolean embeddableSuperclass) {
        this.isEmbeddableSuperclass = embeddableSuperclass;
    }

    void postProcess(PersistentClass persistenceClass, EntityBinder entityBinder) {
        this.getElementsToProcess();
        this.addMappedSuperClassInMetadata(persistenceClass);
        entityBinder.setPropertyAccessType(this.accessType);
    }

    public XClass getClassWithIdClass(boolean evenIfSubclass) {
        if (!evenIfSubclass && this.hasParents()) {
            return null;
        } else if (this.clazz.isAnnotationPresent(IdClass.class)) {
            return this.clazz;
        } else {
            InheritanceState state = getSuperclassInheritanceState(this.clazz, this.inheritanceStatePerClass);
            return state != null ? state.getClassWithIdClass(true) : null;
        }
    }

    public Boolean hasIdClassOrEmbeddedId() {
        if (this.hasIdClassOrEmbeddedId == null) {
            this.hasIdClassOrEmbeddedId = false;
            if (this.getClassWithIdClass(true) != null) {
                this.hasIdClassOrEmbeddedId = true;
            } else {
                ElementsToProcess process = this.getElementsToProcess();
                Iterator var2 = process.getElements().iterator();

                while(var2.hasNext()) {
                    PropertyData property = (PropertyData)var2.next();
                    if (property.getProperty().isAnnotationPresent(EmbeddedId.class)) {
                        this.hasIdClassOrEmbeddedId = true;
                        break;
                    }
                }
            }
        }

        return this.hasIdClassOrEmbeddedId;
    }

    public ElementsToProcess getElementsToProcess() {
        if (this.elementsToProcess == null) {
            InheritanceState inheritanceState = (InheritanceState)this.inheritanceStatePerClass.get(this.clazz);

            assert !inheritanceState.isEmbeddableSuperclass();

            this.getMappedSuperclassesTillNextEntityOrdered();
            this.accessType = this.determineDefaultAccessType();
            ArrayList<PropertyData> elements = new ArrayList();
            int idPropertyCount = 0;

            int currentIdPropertyCount;
            for(Iterator var4 = this.classesToProcessForMappedSuperclass.iterator(); var4.hasNext(); idPropertyCount += currentIdPropertyCount) {
                XClass classToProcessForMappedSuperclass = (XClass)var4.next();
                PropertyContainer propertyContainer = new PropertyContainer(classToProcessForMappedSuperclass, this.clazz, this.accessType);
                currentIdPropertyCount = AnnotationBinder.addElementsOfClass(elements, propertyContainer, this.buildingContext);
            }

            if (idPropertyCount == 0 && !inheritanceState.hasParents()) {
                throw new AnnotationException("No identifier specified for entity: " + this.clazz.getName());
            }

            elements.trimToSize();
            this.elementsToProcess = new ElementsToProcess(elements, idPropertyCount);
        }

        return this.elementsToProcess;
    }

    private AccessType determineDefaultAccessType() {
        XClass xclass;
        for(xclass = this.clazz; xclass != null; xclass = xclass.getSuperclass()) {
            if ((xclass.getSuperclass() == null || Object.class.getName().equals(xclass.getSuperclass().getName())) && (xclass.isAnnotationPresent(Entity.class) || xclass.isAnnotationPresent(MappedSuperclass.class)) && xclass.isAnnotationPresent(Access.class)) {
                return AccessType.getAccessStrategy(((Access)xclass.getAnnotation(Access.class)).value());
            }
        }

        label62:
        for(xclass = this.clazz; xclass != null && !Object.class.getName().equals(xclass.getName()); xclass = xclass.getSuperclass()) {
            if (xclass.isAnnotationPresent(Entity.class) || xclass.isAnnotationPresent(MappedSuperclass.class)) {
                Iterator var2 = xclass.getDeclaredProperties(AccessType.PROPERTY.getType()).iterator();

                XProperty prop;
                boolean isEmbeddedId;
                do {
                    if (!var2.hasNext()) {
                        var2 = xclass.getDeclaredProperties(AccessType.FIELD.getType()).iterator();

                        do {
                            if (!var2.hasNext()) {
                                continue label62;
                            }

                            prop = (XProperty)var2.next();
                            isEmbeddedId = prop.isAnnotationPresent(EmbeddedId.class);
                        } while(!prop.isAnnotationPresent(Id.class) && !isEmbeddedId);

                        return AccessType.FIELD;
                    }

                    prop = (XProperty)var2.next();
                    isEmbeddedId = prop.isAnnotationPresent(EmbeddedId.class);
                } while(!prop.isAnnotationPresent(Id.class) && !isEmbeddedId);

                return AccessType.PROPERTY;
            }
        }

        throw new AnnotationException("No identifier specified for entity: " + this.clazz);
    }

    private void getMappedSuperclassesTillNextEntityOrdered() {
        XClass currentClassInHierarchy = this.clazz;

        InheritanceState superclassState;
        do {
           // this.classesToProcessForMappedSuperclass.add(0, currentClassInHierarchy);
            // fixed the sorting, add this ↓↓↓↓↓
            classesToProcessForMappedSuperclass.add( currentClassInHierarchy );
            XClass superClass = currentClassInHierarchy;

            do {
                superClass = superClass.getSuperclass();
                superclassState = (InheritanceState)this.inheritanceStatePerClass.get(superClass);
            } while(superClass != null && !this.buildingContext.getBootstrapContext().getReflectionManager().equals(superClass, Object.class) && superclassState == null);

            currentClassInHierarchy = superClass;
        } while(superclassState != null && superclassState.isEmbeddableSuperclass());

    }

    private void addMappedSuperClassInMetadata(PersistentClass persistentClass) {
        org.hibernate.mapping.MappedSuperclass mappedSuperclass = null;
        InheritanceState superEntityState = getInheritanceStateOfSuperEntity(this.clazz, this.inheritanceStatePerClass);
        PersistentClass superEntity = superEntityState != null ? this.buildingContext.getMetadataCollector().getEntityBinding(superEntityState.getClazz().getName()) : null;
        int lastMappedSuperclass = this.classesToProcessForMappedSuperclass.size() - 1;

        //for(int index = 0; index < lastMappedSuperclass; ++index) {
        // fixed the sorting, add this ↓↓↓↓↓
        for ( int index = lastMappedSuperclass; index > 0; index-- ) {
            org.hibernate.mapping.MappedSuperclass parentSuperclass = mappedSuperclass;
            Class<?> type = this.buildingContext.getBootstrapContext().getReflectionManager().toClass((XClass)this.classesToProcessForMappedSuperclass.get(index));
            mappedSuperclass = this.buildingContext.getMetadataCollector().getMappedSuperclass(type);
            if (mappedSuperclass == null) {
                mappedSuperclass = new org.hibernate.mapping.MappedSuperclass(parentSuperclass, superEntity);
                mappedSuperclass.setMappedClass(type);
                this.buildingContext.getMetadataCollector().addMappedSuperclass(type, mappedSuperclass);
            }
        }

        if (mappedSuperclass != null) {
            persistentClass.setSuperMappedSuperclass(mappedSuperclass);
        }

    }

    static final class ElementsToProcess {
        private final List<PropertyData> properties;
        private final int idPropertyCount;

        public List<PropertyData> getElements() {
            return this.properties;
        }

        public int getIdPropertyCount() {
            return this.idPropertyCount;
        }

        private ElementsToProcess(List<PropertyData> properties, int idPropertyCount) {
            this.properties = properties;
            this.idPropertyCount = idPropertyCount;
        }
    }
}
