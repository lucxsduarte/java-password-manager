package com.lucasduarte.password_manager.controllers;

import com.lucasduarte.password_manager.dtos.LoginRequest;
import com.lucasduarte.password_manager.models.PasswordData;
import com.lucasduarte.password_manager.security.KeyDerivationService;
import com.lucasduarte.password_manager.security.SessionService;
import com.lucasduarte.password_manager.services.PasswordService;
import com.lucasduarte.password_manager.services.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordController {

    private final PasswordService passwordService;
    private final SessionService sessionService;
    private final KeyDerivationService keyDerivationService;
    private final SetupService setupService;

    @Autowired
    public PasswordController(PasswordService passwordService, SessionService sessionService, KeyDerivationService keyDerivationService, SetupService setupService) {
        this.passwordService = passwordService;
        this.sessionService = sessionService;
        this.keyDerivationService = keyDerivationService;
        this.setupService = setupService;
    }

    @PostMapping("/passwords")
    public ResponseEntity<PasswordData> createPassword(@RequestBody final PasswordData passwordData) {
        final var sessionKey = sessionService.getSessionKey();
        final var createdPassword = passwordService.createPassword(passwordData, sessionKey);
        return new ResponseEntity<>(createdPassword, HttpStatus.CREATED);
    }

    @GetMapping("/passwords")
    public ResponseEntity<List<PasswordData>> getAllPasswords() {
        final var sessionKey = sessionService.getSessionKey();
        final var allPasswords = passwordService.getAllPasswords(sessionKey);
        return ResponseEntity.ok(allPasswords);
    }

    @GetMapping("/setup/status")
    public ResponseEntity<Map<String, Boolean>> getSetupStatus() {
        return ResponseEntity.ok(Map.of("isSetupComplete", setupService.isSetupComplete()));
    }

    @PostMapping("/setup")
    public ResponseEntity<Void> initialSetup(@RequestBody LoginRequest request) throws IOException {
        setupService.performInitialSetup(request.getMasterPassword().toCharArray());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) throws IOException {
        final var key = setupService.login(request.getMasterPassword().toCharArray());
        sessionService.setSessionKey(key);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        sessionService.clearSession();
        return ResponseEntity.ok().build();
    }
}
