package integration_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.dto.OrderItemRequestDto;
import com.example.medjool.model.*;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.repository.PalletRepository;
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
    private PalletRepository palletRepository;

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


        // Mock pallet :
        Pallet pallet = new Pallet(
                null,
                1f,
                11,
                10,
                8,
                null,
                null,
                3f,
                0.8f,
                2f,
                4f,
                0.33f,
                0.99f,
                2f,
                1.88f,
                2f,
                3f,
                1f,
                1f,
                180,
                200,
                100,
                900.0f,
                "",
                "",
                10
        );
        palletRepository.save(pallet);


        // Mock a product:
        Product product = new Product(
                null,
                "Test Product",
                "Small",
                4000.0,
                "Dark",
                "Farm A",
                "Export A",
                null
        );

        Integer palletId = pallet.getPalletId();

        // Prepare an order item:
        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setPalletId(palletId);
        orderItemRequestDto.setCurrency(OrderCurrency.EUR);

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
