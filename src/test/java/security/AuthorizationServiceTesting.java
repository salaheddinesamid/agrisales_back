package unit_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.jwt.JwtUtilities;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = MedjoolApplication.class)
@AutoConfigureMockMvc
public class AuthorizationServiceTesting {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtilities jwtUtilities;

    @Test
    void testAccessProtectedRestEndpoint_withValidJwt() throws Exception {
        // Generate a valid JWT for testing
        String token = jwtUtilities.generateToken("oussama.elmir@gmail.com", "GENERAL_MANAGER");

        mockMvc.perform(get("/api/stock/get_all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 200 if JWT is valid
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
