package com.example.medjool.repository;

import com.example.medjool.model.Role;
import com.example.medjool.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
    boolean existsByRoleName(RoleName roleName);
}
