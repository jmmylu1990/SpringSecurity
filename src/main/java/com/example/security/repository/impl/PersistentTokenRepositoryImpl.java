package com.example.security.repository.impl;

import com.example.security.entity.PersistentLogins;
import com.example.security.repository.PersistentLoginsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public class PersistentTokenRepositoryImpl implements PersistentTokenRepository{

    @Autowired
    private PersistentLoginsRepository persistentLoginsRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogins persistentLogins = new PersistentLogins(token.getUsername(),
                token.getSeries(),
                token.getTokenValue(),
                token.getDate());
        System.out.println("PersistentLogins:"+persistentLogins);
        persistentLoginsRepository.save(persistentLogins);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        System.out.println("updateToken");
        persistentLoginsRepository.updateToken(tokenValue, lastUsed, series);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentLogins persistentLogins = persistentLoginsRepository.findBySeries(seriesId);
        PersistentRememberMeToken persistentRememberMeToken = new PersistentRememberMeToken(persistentLogins.getUsername(),
                persistentLogins.getSeries(),
                persistentLogins.getToken(),
                persistentLogins.getLastUsed());
        System.out.println("persistentLogins:"+persistentLogins);
        System.out.println("persistentRememberMeToken:"+persistentRememberMeToken);
        return persistentRememberMeToken;
    }

    @Transactional
    @Override
    public void removeUserTokens(String username) {
        System.out.println("username:"+username);
        persistentLoginsRepository.deleteByUsername(username);
    }
}
