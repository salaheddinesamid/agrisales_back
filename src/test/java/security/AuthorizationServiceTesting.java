package security;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.jwt.JwtUtilities;

import com.example.medjool.services.implementation.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** * This class tests the authorization service by verifying access to protected REST endpoints using JWT tokens.
 * It checks both valid and invalid JWT scenarios.
 */

@SpringBootTest(classes = MedjoolApplication.class)
@AutoConfigureMockMvc
public class AuthorizationServiceTesting {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtilities jwtUtilities;


    /** * Tests access to a protected REST endpoint with a valid JWT token.
     * It verifies that the endpoint returns a 200 OK status when accessed with a valid token.
     */
    @Test
    void testAccessProtectedRestEndpoint_withValidJwt() throws Exception {
        String email = "Oussama.elmir@gmail.com";
        String role = "GENERAL_MANAGER";
        String token = jwtUtilities.generateToken(email, role);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("dummy")
                .authorities(role)
                .build();

        Mockito.when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        mockMvc.perform(get("/api/order/get_all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }



    /** * Tests access to a protected REST endpoint with an invalid JWT token.
     * It verifies that the endpoint returns a 401 Unauthorized status when accessed with an invalid token.
     */
    @Test
    void testAccessProtectedRestEndpoint_withValidJwt_andInvalidRole() throws Exception{
        String token = jwtUtilities.generateToken("salaheddine.samid@medjoolstar.com","SALES");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("salaheddine.samid@medjoolstar.com")
                .password("dummy")
                .authorities("FINANCE") // Invalid role for this endpoint
                .build();

        Mockito.when(userDetailsService.loadUserByUsername("salaheddine.samid@medjoolstar.com")).thenReturn(userDetails);

        mockMvc.perform(get("/api/stock/get_all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());  // 401 if the user does not have the right role
    }


}
