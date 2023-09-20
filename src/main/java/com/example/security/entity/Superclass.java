package com.example.security.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.List;

@MappedSuperclass
public @Data class Superclass {
    @Column(name = "Z_TEXT_FIELD", columnDefinition = "varchar(40)")
    private String z_textField;
    @Column(name = "A_TEXT_FIELD", columnDefinition = "varchar(40)")
    private String a_textField;
}
