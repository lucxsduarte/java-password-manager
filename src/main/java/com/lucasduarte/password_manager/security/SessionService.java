package com.lucasduarte.password_manager.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.crypto.SecretKey;

@Component
@SessionScope
public class SessionService {

    private SecretKey sessionKey;

    public void setSessionKey(SecretKey key) {
        this.sessionKey = key;
    }

    public SecretKey getSessionKey() {
        if (this.sessionKey == null) {
            throw new IllegalStateException("Usuário não autenticado. A chave da sessão não está disponível.");
        }
        return this.sessionKey;
    }

    public boolean isUserAuthenticated() {
        return this.sessionKey != null;
    }

    public void clearSession() {
        this.sessionKey = null;
    }
}
