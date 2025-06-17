package unit_testing;

import com.example.medjool.dto.NewUserDto;
import com.example.medjool.model.Role;
import com.example.medjool.model.RoleName;
import com.example.medjool.model.User;
import com.example.medjool.repository.RoleRepository;
import com.example.medjool.repository.UserRepository;
import com.example.medjool.services.implementation.UserManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserManagementTesting {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;


    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPasswordEncoderMock() {
        // Simuler le comportement de l'encodage
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Vérifier le comportement simulé
        String result = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, result));

    }


    @Test
    void createUserCredentialsSuccess(){

        NewUserDto newUserDto = new NewUserDto("John","Doe","john.doe@gmail.com","GENERAL_MANAGER","password123",false);

        Role role = new Role();
        role.setRoleName(RoleName.valueOf(newUserDto.getRoleName()));
        role.setId(1L); // Assuming the role ID is set to 1 for testing purposes

        // Mock the behavior of the role repository
        when(roleRepository.findByRoleName(RoleName.valueOf(newUserDto.getRoleName()))).thenReturn(java.util.Optional.of(role));
        assertEquals(new ResponseEntity<>("User account created", HttpStatus.CREATED), userManagementService.createUserAccount(newUserDto));

    }

    @Test
    void testGetAllUsersSuccess() {
        // Implement the test logic here
        // This method should test the getAllUsers functionality of UserManagementServiceImpl
    }

    @Test
    void unlockUserAccountSuccess() {
        // Implement the test logic here
        // This method should test the unlockUserAccount functionality of UserManagementServiceImpl

        Role role = new Role(1L, RoleName.GENERAL_MANAGER);

        when(roleRepository.findByRoleName(RoleName.GENERAL_MANAGER)).thenReturn(java.util.Optional.of(role));
        User user = new User(1L,"John","Doe","john.doe@gmail.com",passwordEncoder.encode("password123"),LocalDateTime.now(),role,true,true,true,true);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<Object> response = userManagementService.activateUserAccount(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User account activated", response.getBody());

        assertTrue(user.isAccountNonLocked());
    }

}
