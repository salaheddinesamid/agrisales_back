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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = MedjoolApplication.class)
@AutoConfigureMockMvc
public class AuthorizationServiceTesting {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtilities jwtUtilities;

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


    @Test
    void testAccessProtectedRestEndpoint_withValidJwt_andInvalidRole() throws Exception{
        String token = jwtUtilities.generateToken("salaheddine.samid@medjoolstar.com","SALES");

        mockMvc.perform(get("/api/stock/get_all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());  // 401 if the user does not have the right role
    }


}
