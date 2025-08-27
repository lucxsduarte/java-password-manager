package com.lucasduarte.password_manager.services;

import javax.crypto.SecretKey;
import java.io.IOException;

public interface SetupService {

    boolean isSetupComplete();

    void performInitialSetup(char[] masterPassword) throws IOException;

    SecretKey login(char[] masterPassword) throws IOException;
}
