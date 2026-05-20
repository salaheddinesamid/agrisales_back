package unit_testing;

import com.example.medjool.modules.order.model.Order;
import com.example.medjool.modules.order.model.OrderCurrency;
import com.example.medjool.modules.order.model.OrderStatus;
import com.example.medjool.modules.order.repository.OrderRepository;
import com.example.medjool.modules.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class ShipmentServiceTesting {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private OrderRepository orderRepository;


    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void testCreateShipment() {
        // Implement the test logic here

        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(null);
        order.setStatus(OrderStatus.PRELIMINARY);
        order.setTotalWeight(200);
        order.setDeliveryDate(null);
        order.setClient(null);
        order.setCurrency(OrderCurrency.MAD);
        order.setTotalPrice(2000);

        // Mock the behavior of the order repository
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    }
}
