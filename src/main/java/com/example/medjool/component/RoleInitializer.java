package com.example.medjool.component;

import com.example.medjool.model.Role;
import com.example.medjool.model.RoleName;
import com.example.medjool.model.User;
import com.example.medjool.repository.RoleRepository;
import com.example.medjool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final RoleName[] roles = {
            RoleName.GENERAL_MANAGER,
            RoleName.SALES,
            RoleName.LOGISTICS,
            RoleName.FINANCE,
            RoleName.FACTORY
    };
    @Autowired
    public RoleInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        for (RoleName roleName : roles) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = new Role();
                // NO manual ID setting here
                role.setRoleName(roleName);
                roleRepository.save(role);
            }
        }

    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeUsers(){

        // Create a default user for the General Manager role:
        String GM_EMAIL = "Oussama.elmir@medjoolstar.com";

        if(!userRepository.existsByEmail(GM_EMAIL)){
            User GM = new User();
            GM.setEmail(GM_EMAIL);
            GM.setPassword(passwordEncoder.encode("admin"));
            GM.setFirstName("Oussama");
            GM.setLastName("Elmir");
             Role role = roleRepository.findByRoleName(RoleName.GENERAL_MANAGER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            GM.setRole(role);
            GM.setAccountNonLocked(true);
            userRepository.save(GM);
        }

    }
}
