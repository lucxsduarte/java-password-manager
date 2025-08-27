package com.lucasduarte.password_manager.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasduarte.password_manager.entities.PasswordEntry;
import com.lucasduarte.password_manager.models.PasswordData;
import com.lucasduarte.password_manager.repositories.PasswordEntryRepository;
import com.lucasduarte.password_manager.security.EncryptedPayload;
import com.lucasduarte.password_manager.security.EncryptionService;
import com.lucasduarte.password_manager.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEntryRepository passwordEntryRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PasswordServiceImpl(
            PasswordEntryRepository passwordEntryRepository,
            EncryptionService encryptionService,
            ObjectMapper objectMapper) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.encryptionService = encryptionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public PasswordData createPassword(final PasswordData data, final SecretKey sessionKey) {
        try {
            final var plainJson = objectMapper.writeValueAsString(data);

            final var payload = encryptionService.encrypt(plainJson, sessionKey);

            final var newEntry = new PasswordEntry();
            newEntry.setEncryptedData(payload.encryptedData());
            newEntry.setInitializationVector(payload.initializationVector());

            passwordEntryRepository.save(newEntry);

            return data;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar e criptografar a nova entrada de senha.", e);
        }
    }

    @Override
    public List<PasswordData> getAllPasswords(SecretKey sessionKey) {
        final var allEntries = passwordEntryRepository.findAll();

        return allEntries.stream()
                .map(entry -> {
                    try {
                        final var payload = new EncryptedPayload(
                                entry.getEncryptedData(),
                                entry.getInitializationVector()
                        );

                        final var decryptedJson = encryptionService.decrypt(payload, sessionKey);

                        return objectMapper.readValue(decryptedJson, PasswordData.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Falha ao descriptografar a entrada com id: " + entry.getId(), e);
                    }
                })
                .toList();
    }
}
