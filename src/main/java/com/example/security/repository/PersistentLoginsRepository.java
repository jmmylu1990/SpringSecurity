package com.example.security.repository;

import com.example.security.entity.PersistentLogins;
import com.example.security.entity.User;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.util.Date;

public interface PersistentLoginsRepository extends JpaRepository<PersistentLogins,String> {

    @Modifying
    @Query("UPDATE PersistentLogins p SET p.token = :token , p.lastUsed = :lastUsed WHERE p.series = :series")
    void updateToken(@Param("token")String token, @Param("lastUsed") Date lastUsed, @Param("series")String series);

    PersistentLogins findBySeries(String series);

    void deleteByUsername(String username);
}
