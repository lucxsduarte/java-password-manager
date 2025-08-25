package com.lucasduarte.password_manager.entities;

import com.lucasduarte.password_manager.enums.Category;
import com.lucasduarte.password_manager.enums.LoginType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "password_entries")
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Column(name = "login_value")
    private String loginValue;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPassword;

}