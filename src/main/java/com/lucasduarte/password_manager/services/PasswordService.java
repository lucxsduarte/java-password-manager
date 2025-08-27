package com.lucasduarte.password_manager.services;

import com.lucasduarte.password_manager.models.PasswordData;

import javax.crypto.SecretKey;
import java.util.List;

public interface PasswordService {

    PasswordData createPassword(PasswordData data, SecretKey sessionKey);

    List<PasswordData> getAllPasswords(SecretKey sessionKey);
}
