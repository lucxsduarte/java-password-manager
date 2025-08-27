package com.lucasduarte.password_manager.security.impl;

import com.lucasduarte.password_manager.security.EncryptedPayload;
import com.lucasduarte.password_manager.security.EncryptionService;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private static final String ALGORITHM_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    @Override
    public EncryptedPayload encrypt(final String plainText, final SecretKey key) {
        try {
            final var iv = new byte[IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);

            final var cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);

            final var gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

            byte[] encryptedText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return new EncryptedPayload(encryptedText, iv);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar os dados", e);
        }
    }

    @Override
    public String decrypt(final EncryptedPayload payload, final SecretKey key) {
        try {
            final var cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);

            final var gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, payload.initializationVector());

            cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

            byte[] decryptedText = cipher.doFinal(payload.encryptedData());

            return new String(decryptedText, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar os dados. A chave pode estar incorreta ou os dados corrompidos.", e);
        }
    }
}
