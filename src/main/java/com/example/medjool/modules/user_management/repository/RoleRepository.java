package com.example.medjool.modules.user_management.repository;

import com.example.medjool.modules.user_management.model.Role;
import com.example.medjool.modules.user_management.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/** * Repository interface for managing Role entities.
 * Provides methods to find roles by their name and check existence.
 */


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
    boolean existsByRoleName(RoleName roleName);
}
