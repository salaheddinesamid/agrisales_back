package integration_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.dto.OrderItemRequestDto;
import com.example.medjool.model.Client;
import com.example.medjool.model.ClientStatus;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.services.implementation.OrderServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest(classes = MedjoolApplication.class)
@ActiveProfiles("test")
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderServiceImpl orderService;


    @Test
    public void testCreateOrderSuccess(){
        // Mock a client:
        Client client = new Client(
                null,
                "Test client",
                "Manager",
                "Trading",
                "--",
                "www.website.com",
                "Export A",
                10f,
                null,
                null,
                ClientStatus.ACTIVE,
                null
        );
        clientRepository.save(client);

        // Prepare an order item:
        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();

    }

    @Test
    public void testGetAllOrders(){


    }

    @Test
    public void updateOrderSuccess(){}

    @Test
    public void testUpdateOrderStatus(){}

    @Test
    public void testCancelOrder(){}

    @Test
    public void testGetAllOrdersHistory(){}
}
