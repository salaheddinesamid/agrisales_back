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
    public void initializeManager(){
        String email = "Oussama.elmir@medjoolstar.com";

        if(!userRepository.existsByEmail(email)){
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setFirstName("Oussama");
            user.setLastName("Elmir");
             Role role = roleRepository.findByRoleName(RoleName.GENERAL_MANAGER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
            user.setAccountNonLocked(true);
            userRepository.save(user);
        }
    }

}
