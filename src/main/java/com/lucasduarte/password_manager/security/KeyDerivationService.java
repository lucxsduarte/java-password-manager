package com.lucasduarte.password_manager.security;

import javax.crypto.SecretKey;

public interface KeyDerivationService {

    byte[] generateSalt();

    SecretKey deriveKeyFromPassword(char[] password, byte[] salt);
}
