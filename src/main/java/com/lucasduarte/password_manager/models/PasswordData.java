package com.lucasduarte.password_manager.models;

import com.lucasduarte.password_manager.enums.Category;
import com.lucasduarte.password_manager.enums.LoginType;
import lombok.Data;

@Data
public class PasswordData {

    private String alias;
    private Category category;
    private LoginType loginType;
    private String loginValue;
    private String plainPassword;
}
