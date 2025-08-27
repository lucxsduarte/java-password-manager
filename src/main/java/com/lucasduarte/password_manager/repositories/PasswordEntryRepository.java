package com.lucasduarte.password_manager.repositories;

import com.lucasduarte.password_manager.entities.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {


}
