package integration_testing;

import com.example.medjool.MedjoolApplication;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MedjoolApplication.class)
@ActiveProfiles("test")
@Transactional
public class OrderServiceIntegrationTest {


    @Test
    public void testCreateOrderSuccess(){}

    @Test
    public void testGetAllOrders(){}

    @Test
    public void updateOrderSuccess(){}

    @Test
    public void testUpdateOrderStatus(){}

    @Test
    public void testCancelOrder(){}

    @Test
    public void testGetAllOrdersHistory(){}
}
