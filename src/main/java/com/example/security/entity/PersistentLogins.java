package com.example.security.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Table(name = "persistent_logins")
@Entity
@ToString
public @Data class PersistentLogins {

    @Column(nullable = false, columnDefinition = "varchar(64)")
    private String username;

    @Id
    @Column(columnDefinition = "varchar(64)")
    private String series;

    @Column(columnDefinition = "varchar(64)")
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_used", nullable = false)
    private Date lastUsed;

    public PersistentLogins() {
    }

    public PersistentLogins(String username, String series, String token, Date lastUsed) {
        this.username = username;
        this.series = series;
        this.token = token;
        this.lastUsed = lastUsed;
    }
}
