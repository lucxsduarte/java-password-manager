package com.lucasduarte.password_manager.security.impl;

import com.lucasduarte.password_manager.security.KeyDerivationService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

@Service
public class KeyDerivationServiceImpl implements KeyDerivationService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 250000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;

    @Override
    public byte[] generateSalt() {
        final var salt = new byte[SALT_LENGTH_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    @Override
    public SecretKey deriveKeyFromPassword(final char[] password, final byte[] salt) {
        try {
            final var factory = SecretKeyFactory.getInstance(ALGORITHM);

            final var spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH_BITS);

            final var secret = factory.generateSecret(spec);

            return new SecretKeySpec(secret.getEncoded(), "AES");

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erro grave no serviço de derivação de chave", e);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
