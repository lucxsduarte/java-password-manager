package com.lucasduarte.password_manager.repositories;

import com.lucasduarte.password_manager.entities.PasswordEntry;
import com.lucasduarte.password_manager.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    List<PasswordEntry> findByCategory(Category category);

//    List<PasswordEntry> findByAliasContainingIgnoreCase(String text);

}
