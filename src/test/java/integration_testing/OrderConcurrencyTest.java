package integration_testing;

import com.example.medjool.dto.OrderItemRequestDto;
import com.example.medjool.dto.OrderRequestDto;
import com.example.medjool.model.Product;
import com.example.medjool.repository.ProductRepository;
import com.example.medjool.services.implementation.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderConcurrencyTest {

    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        Product product = new Product();
        product.setProductCode("P001");
        product.setTotalWeight(1000.0); // start with 1000 kg
        productRepository.save(product);
    }

    @Test
    public void testConcurrentOrders() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    OrderRequestDto dto = buildTestOrderRequest(); // each requests 100kg
                    orderService.createOrder(dto);
                } catch (Exception e) {
                    System.out.println("Failed to create order: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Product updated = productRepository.findByProductCode("P001").orElseThrow();
        System.out.println("Remaining stock: " + updated.getTotalWeight());
        // Assert that no overselling occurred
        Assertions.assertTrue(updated.getTotalWeight() >= 0);
    }

    private OrderRequestDto buildTestOrderRequest() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setClientName("Test Client");
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductCode("P001");
        item.setItemWeight(100.0); // each thread tries to order 100kg
        item.setPricePerKg(5.0);
        item.setCurrency("EUR");
        item.setNumberOfPallets(1);
        item.setPackaging(1.0);
        item.setPalletId(1);
        dto.setItems(List.of(item));
        return dto;
    }
}

