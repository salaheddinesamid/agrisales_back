package integration_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.dto.*;
import com.example.medjool.model.*;
import com.example.medjool.repository.*;
import com.example.medjool.services.implementation.OrderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = MedjoolApplication.class)
@ActiveProfiles("test")
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PalletRepository palletRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ForexRepository forexRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderServiceImpl orderService;


    @BeforeEach
    void setUp(){
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
                "Farm A",
                "Export A",
                null
        );

        productRepository.save(product);


        /*
        Forex forex = new Forex();
        forex.setCurrency(ForexCurrency.EUR);
        forex.setBuyingRate(10.0);

        forexRepository.save(forex);

         */
    }


    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        Pallet pallet = palletRepository.findByPackaging(1);
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductCode("Test Product");
        //item.setItemWeight(200.0);
        item.setPricePerKg(2.5);
        item.setPackaging(1);
        item.setNumberOfPallets(2);
        item.setPalletId(pallet.getPalletId());
        item.setItemBrand("BrandA");

        OrderRequestDto request = new OrderRequestDto();
        request.setClientName("Test client");
        request.setCurrency("EUR");
        request.setItems(List.of(item));
        request.setShippingAddress("123 Shipping Lane");

        // No mixed order
        request.setMixedOrderDto(new MixedOrderDto());

        ResponseEntity<?> response = orderService.createOrder(request);
        assertEquals(200, response.getStatusCodeValue(), "Expected status code 200 for successful order creation");

        // Additional assertions
        Product updatedProduct = productRepository.findByProductCode("Test Product").orElseThrow();
        assertEquals(2200, updatedProduct.getTotalWeight(), 0.1);

        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
    }

    @Test
    public void testGetAllOrders(){

        List<OrderResponseDto> response = orderService.getAllOrders();
        // Assertions can be added here to verify the response
        assertEquals(0, response.size(), "Expected no orders in the database for a fresh test run.");
    }

    @Test
    public void updateOrderSuccess(){

        Pallet pallet = palletRepository.findByPackaging(1);
        OrderUpdateRequestDto request = new OrderUpdateRequestDto();
        OrderItemRequestDto updatedItem = new OrderItemRequestDto();
        updatedItem.setProductCode("Test Product");
        updatedItem.setPricePerKg(2.5);
        updatedItem.setPalletId(pallet.getPalletId());
        updatedItem.setPackaging(1);
        updatedItem.setNumberOfPallets(3);
        updatedItem.setItemBrand("BrandA");


    }

    @Test
    public void testUpdateOrderStatus(){}

    @Test
    public void testCancelOrder(){}

    @Test
    public void testGetAllOrdersHistory(){}
}
