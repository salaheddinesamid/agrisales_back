package integration_testing;

import com.example.medjool.MedjoolApplication;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MedjoolApplication.class)
@ActiveProfiles("test")
@Transactional
public class OverviewServiceIntegrationTest{

    @Test
    public void testGetOverview() {}

    @Test
    public void testGetMarginPerClient(){}
}
