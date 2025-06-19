package com.example.medjool.services.implementation;

import com.example.medjool.dto.*;
import com.example.medjool.exception.InvalidCredentialsException;
import com.example.medjool.exception.UserAccountLockedException;
import com.example.medjool.exception.UserAlreadyExistsException;
import com.example.medjool.jwt.JwtUtilities;
import com.example.medjool.model.Role;
import com.example.medjool.model.RoleName;
import com.example.medjool.model.User;
import com.example.medjool.repository.RoleRepository;
import com.example.medjool.repository.UserRepository;
import com.example.medjool.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtilities jwtUtilities;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, JwtUtilities jwtUtilities, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtUtilities = jwtUtilities;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param loginRequestDto the login request containing email and password
     * @return ResponseEntity containing authentication response with token and user details
     * @throws UsernameNotFoundException if the user is not found
     * @throws UserAccountLockedException if the user account is locked
     * @throws InvalidCredentialsException if the credentials are invalid
     */
    @Override
    public ResponseEntity<?> authenticate(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isAccountNonLocked()) {
            throw new UserAccountLockedException("User account is locked");
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials,please try again");
        }

        LocalDateTime now = LocalDateTime.now();
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();

        String token = jwtUtilities.generateToken(user.getEmail(), user.getRole().getRoleName().toString());

        user.setLastLogin(now);
        userRepository.save(user);

        UserDetailsDto userDetailsDto = new UserDetailsDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getRoleName().toString(),
                user.isAccountNonLocked(),
                user.getLastLogin()
        );

        authenticationResponseDto.setToken(token);
        authenticationResponseDto.setUser(userDetailsDto);

        return new ResponseEntity<>(authenticationResponseDto, HttpStatus.OK);
    }

    /**     * Creates a new user with the provided details.
     *
     * @param newUserDto the DTO containing new user details
     * @return ResponseEntity indicating the result of the user creation
     * @throws UserAlreadyExistsException if a user with the same email already exists
     */
    @Override
    public ResponseEntity<?> createCredentials(NewUserDto newUserDto){

        // Check if the user already exists:
        Optional<User> user = userRepository.findByEmail(newUserDto.getEmail());

        user.ifPresent(u->{
            throw new UserAlreadyExistsException("User with this email already exists");
        });

        // Create new user:
        User newUser = new User();

        RoleName roleName = RoleName.valueOf(newUserDto.getRoleName());
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(() -> new RuntimeException("Role not found"));

        newUser.setFirstName(newUserDto.getFirstName());
        newUser.setLastName(newUserDto.getLastName());
        newUser.setEmail(newUserDto.getEmail());
        newUser.setRole(role);
        newUser.setPassword(passwordEncoder.encode(newUserDto.getPassword()));
        newUser.setAccountNonLocked(newUserDto.isAccountLocked());
        newUser.setEnabled(true);
        userRepository.save(newUser);

        return new ResponseEntity<>("NEW USER CREATED, WAITING FOR UNLOCKING THE ACCOUNT BY AN ADMIN...", HttpStatus.CREATED);
    }

    /**
     * Logs out a user by invalidating their token.
     *
     * @param token the JWT token to be invalidated
     * @return ResponseEntity indicating the result of the logout operation
     */
    @Override
    public ResponseEntity<Object> logout(String token) {
        return null;
    }
}
