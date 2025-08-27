package com.lucasduarte.password_manager.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasduarte.password_manager.exceptions.InvalidMasterPasswordException;
import com.lucasduarte.password_manager.security.EncryptedPayload;
import com.lucasduarte.password_manager.security.EncryptionService;
import com.lucasduarte.password_manager.security.KeyDerivationService;
import com.lucasduarte.password_manager.services.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

@Service
public class SetupServiceImpl implements SetupService {

    private record VaultData(String salt, String iv, String validator) {
    }

    private static final String VALIDATION_STRING = "SUCCESS";
    private final Path vaultFilePath;

    private final KeyDerivationService keyDerivationService;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SetupServiceImpl(KeyDerivationService keyDerivationService, EncryptionService encryptionService, ObjectMapper objectMapper) {
        this.keyDerivationService = keyDerivationService;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
        // Define o caminho do arquivo do cofre no diretório home do usuário
        this.vaultFilePath = Paths.get(System.getProperty("user.home"), ".password-manager", "vault.json");
    }

    @Override
    public boolean isSetupComplete() {
        return Files.exists(vaultFilePath);
    }

    @Override
    public void performInitialSetup(final char[] masterPassword) throws IOException {
        if (isSetupComplete()) {
            throw new IllegalStateException("O setup já foi realizado.");
        }

        byte[] salt = keyDerivationService.generateSalt();
        final var key = keyDerivationService.deriveKeyFromPassword(masterPassword, salt);

        final var validatorPayload = encryptionService.encrypt(VALIDATION_STRING, key);

        final var vaultData = new VaultData(
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(validatorPayload.initializationVector()),
                Base64.getEncoder().encodeToString(validatorPayload.encryptedData())
        );

        Files.createDirectories(vaultFilePath.getParent());
        objectMapper.writeValue(vaultFilePath.toFile(), vaultData);
    }

    @Override
    public SecretKey login(final char[] masterPassword) throws IOException {
        if (!isSetupComplete()) {
            throw new IllegalStateException("O setup precisa ser realizado primeiro.");
        }

        final var vaultData = objectMapper.readValue(vaultFilePath.toFile(), VaultData.class);

        final var salt = Base64.getDecoder().decode(vaultData.salt());
        final var iv = Base64.getDecoder().decode(vaultData.iv());
        final var encryptedValidator = Base64.getDecoder().decode(vaultData.validator());

        final var key = keyDerivationService.deriveKeyFromPassword(masterPassword, salt);

        try {
            final var validatorPayload = new EncryptedPayload(encryptedValidator, iv);
            final var decryptedString = encryptionService.decrypt(validatorPayload, key);

            if (VALIDATION_STRING.equals(decryptedString)) {
                return key;
            } else {
                throw new InvalidMasterPasswordException("Falha na validação do cofre.");
            }
        } catch (Exception e) {
            throw new InvalidMasterPasswordException("Senha mestra incorreta.");
        } finally {
            Arrays.fill(masterPassword, '\0');
        }
    }
}
