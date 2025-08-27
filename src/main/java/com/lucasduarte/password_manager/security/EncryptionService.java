package com.lucasduarte.password_manager.security;

import javax.crypto.SecretKey;

public interface EncryptionService {

    EncryptedPayload encrypt(String plainText, SecretKey key);

    String decrypt(EncryptedPayload payload, SecretKey key);
}
