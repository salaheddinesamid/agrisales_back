package com.example.medjool.services.implementation;

import com.example.medjool.dto.NewPasswordDto;
import com.example.medjool.dto.NewUserDto;
import com.example.medjool.dto.UserDetailsDto;
import com.example.medjool.exception.UserAccountCannotBeDeletedException;
import com.example.medjool.model.Role;
import com.example.medjool.model.RoleName;
import com.example.medjool.model.User;
import com.example.medjool.repository.RoleRepository;
import com.example.medjool.repository.UserRepository;
import com.example.medjool.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserManagementServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public ResponseEntity<List<UserDetailsDto>> getAllUsers() {
        List<UserDetailsDto> userDetailsDtos = userRepository.findAll().stream()
                .map(user -> new UserDetailsDto(
                                user.getUserId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getRole().getRoleName().toString(),
                                user.isAccountNonLocked(),
                                user.getLastLogin()
                        )
                )
                .toList();
        return new ResponseEntity<>(userDetailsDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDetailsDto> getUserById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<Object> activateUserAccount(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            u.setAccountNonLocked(true);
            userRepository.save(u);
        });
        return new ResponseEntity<>("User account activated", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> updateUserDetails(Long id, UserDetailsDto userDetailsDto) {
        Optional<User> user = userRepository.findById(id);

        user.ifPresent(u -> {
            u.setFirstName(userDetailsDto.getFirstName());
            u.setLastName(userDetailsDto.getLastName());
            u.setEmail(userDetailsDto.getEmail());
            u.setPassword(passwordEncoder.encode(u.getPassword()));
            userRepository.save(u);
        });

        return new ResponseEntity<>("User details updated", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> holdUserAccount(Long id) {

        // Fetch the user from the database;
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            u.setAccountNonLocked(false);
        });

        return new ResponseEntity<>("User account hold", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteUserAccount(Long id) {
        try {
            userRepository.deleteById(id);
            return new ResponseEntity<>("User account deleted", HttpStatus.OK);
        } catch (UserAccountCannotBeDeletedException e) {
            return new ResponseEntity<>("User account cannot be deleted", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> createUserAccount(NewUserDto userDetailsDto) {
        boolean userExists = userRepository.existsByEmail(userDetailsDto.getEmail());

        if (userExists) {
            return new ResponseEntity<>("User with this email already exists", HttpStatus.BAD_REQUEST);
        } else {
            User user = new User();
            user.setFirstName(userDetailsDto.getFirstName());
            user.setLastName(userDetailsDto.getLastName());
            user.setEmail(userDetailsDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
            user.setAccountNonLocked(true);

            Role role = roleRepository.findByRoleName(RoleName.valueOf(userDetailsDto.getRoleName())).get();
            user.setRole(role);

            userRepository.save(user);
            return new ResponseEntity<>("User account created", HttpStatus.CREATED);
        }
    }

    @Override
    public ResponseEntity<Object> resetUserPassword(Long id, NewPasswordDto newPassword) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            User u = user.get();

            if (passwordEncoder.matches(newPassword.getOldPassword(), u.getPassword())) {
                if (passwordEncoder.matches(newPassword.getNewPassword(), u.getPassword())) {
                    return new ResponseEntity<>("New password cannot be the same as the old password", HttpStatus.BAD_REQUEST);
                } else {
                    u.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
                    userRepository.save(u);
                    return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("Old password is incorrect", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
}
