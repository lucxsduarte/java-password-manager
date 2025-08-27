package com.lucasduarte.password_manager.security;

public record EncryptedPayload(byte[] encryptedData, byte[] initializationVector) {
}
