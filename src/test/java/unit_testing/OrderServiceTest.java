package unit_testing;

import com.example.medjool.dto.*;

import com.example.medjool.exception.ClientNotActiveException;
import com.example.medjool.exception.OrderCannotBeCanceledException;
import com.example.medjool.exception.ProductLowStock;
import com.example.medjool.exception.ProductNotFoundException;

import com.example.medjool.model.*;
import com.example.medjool.repository.*;

import com.example.medjool.services.implementation.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class  OrderServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PalletRepository palletRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderHistoryRepository orderHistoryRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private MixedOrderItemRepo mixedOrderItemRepo;

    @Mock
    private ForexRepository forexRepository;

    @Mock
    private MixeOrderItemDetailsRepo mixeOrderItemDetailsRepo;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Should create order successfully with valid request")
    @Test
    void testCreateOrderSuccess() {
        // --- GIVEN ---
        String clientName = "Fresh Fruits Inc";

        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName(clientName);

        Product product = new Product();
        product.setProductId(1L);
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(5000.0);

        Pallet pallet = new Pallet();
        pallet.setPalletId(1);
        pallet.setTotalNet(200f);
        pallet.setPreparationTime(5.0);

        Forex forex = new Forex();
        forex.setId(1L);
        forex.setCurrency(ForexCurrency.USD);
        forex.setBuyingRate(10);

        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setProductCode("M_EA_B_M");
        //itemDto.setItemWeight(700.0);
        itemDto.setPalletId(1);
        itemDto.setPricePerKg(2.5);
        itemDto.setPackaging(1.0);
        itemDto.setNumberOfPallets(1);

        MixedOrderDto mixedOrderDto = new MixedOrderDto();
        mixedOrderDto.setItems(null); // No mixed items

        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setClientName(clientName);
        orderRequest.setItems(List.of(itemDto));
        orderRequest.setMixedOrderDto(mixedOrderDto);
        orderRequest.setCurrency("USD");
        orderRequest.setShippingAddress("Test Address");

        // --- MOCKS ---
        when(clientRepository.findByCompanyName(clientName)).thenReturn(client);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(palletRepository.findAll()).thenReturn(List.of(pallet));
        when(palletRepository.findById(1)).thenReturn(Optional.of(pallet));
        when(forexRepository.findByCurrency(ForexCurrency.USD)).thenReturn(Optional.of(forex));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        // --- WHEN ---
        ResponseEntity<?> response = orderService.createOrder(orderRequest);

        // --- THEN ---
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // --- VERIFY ORDER WAS SAVED ---
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertNotNull(savedOrder);
        assertEquals(client, savedOrder.getClient());
        assertEquals("Test Address", savedOrder.getShippingAddress());
        assertEquals(OrderCurrency.USD, savedOrder.getCurrency());
        assertEquals(1, savedOrder.getOrderItems().size());

        OrderItem item = savedOrder.getOrderItems().get(0);
        assertEquals("M_EA_B_M", item.getProduct().getProductCode());
        assertEquals(4800.0, item.getProduct().getTotalWeight());
        assertEquals(200.0, item.getItemWeight());
        assertEquals(2.5, item.getPricePerKg());
        assertEquals(1, item.getNumberOfPallets());
        assertEquals(1.0, item.getPackaging());
        assertEquals(pallet, item.getPallet());
    }




    @Test
    void testCreateOrderWithMixedItems() {
        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setClientName("Fresh Fruits Inc");

        // Mock order item
        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setProductCode("M_EB_B_M");
        //itemDto.setItemWeight(500.0);
        itemDto.setPalletId(1);
        itemDto.setPricePerKg(2.5);
        itemDto.setPackaging(1);
        itemDto.setNumberOfPallets(1);

        orderRequest.setItems(List.of(itemDto));
        orderRequest.setCurrency("EUR");
        orderRequest.setProductionDate(LocalDate.now());

        // Mock the mixed order DTO
        MixedOrderDto mixedOrderDto = new MixedOrderDto();
        ArrayList<MixedOrderItemRequestDto> mixedItems = new ArrayList<>();

        MixedOrderItemRequestDto item_1 = new MixedOrderItemRequestDto();
        MixedOrderItemRequestDto item_2 = new MixedOrderItemRequestDto();

        // Mixed item 1:
        item_1.setProductCode("M_EC_B_M");
        item_1.setPercentage(10);

        // Mixed item 2:
        item_2.setProductCode("M_EA_B_M");
        item_2.setPercentage(90);

        mixedItems.add(item_1);
        mixedItems.add(item_2);

        mixedOrderDto.setItems(mixedItems);
        mixedOrderDto.setPalletId(1);

        orderRequest.setMixedOrderDto(mixedOrderDto);

        // Mock the client
        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        Forex forex = new Forex();
        forex.setId(1L);
        forex.setCurrency(ForexCurrency.EUR);
        forex.setBuyingRate(10);

        // Mock the products with sufficient stock
        Product product1 = new Product();
        Product product2 = new Product();
        Product product3 = new Product();

        product1.setProductId(1L);
        product1.setProductCode("M_EA_B_M");
        product1.setTotalWeight(1000.0); // Sufficient stock

        product2.setProductId(2L);
        product2.setProductCode("M_EB_B_M");
        product2.setTotalWeight(2000.0); // Sufficient stock

        product3.setProductId(3L);
        product3.setProductCode("M_EC_B_M");
        product3.setTotalWeight(2000.0); // Sufficient stock


        // Mock the pallet
        Pallet pallet = new Pallet();
        pallet.setPalletId(1);
        pallet.setPreparationTime(5.0);
        pallet.setTotalNet(1000.0f);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(forexRepository.findByCurrency(ForexCurrency.EUR)).thenReturn(Optional.of(forex));
        when(productRepository.findAll()).thenReturn(List.of(product1, product2, product3));
        when(palletRepository.findAll()).thenReturn(List.of(pallet));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<?> response = orderService.createOrder(orderRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify the order was saved

    }

    @Test
    void testCreateOrder_withInActiveClient() {
        // Arrange
        LocalDate now = LocalDate.now();
        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setClientName("Fresh Fruits Inc");


        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setProductCode("M_EA_B_M");
        //itemDto.setItemWeight(500.0);
        itemDto.setPalletId(1);
        itemDto.setPricePerKg(2.5);
        itemDto.setPackaging(1);
        itemDto.setNumberOfPallets(1);


        orderRequest.setItems(List.of(itemDto));
        orderRequest.setCurrency(OrderCurrency.MAD.toString());
        orderRequest.setProductionDate(now);


        Client client = new Client();
        client.setClientStatus(ClientStatus.INACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        Product product = new Product();
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(1000.0);

        Pallet pallet = new Pallet();

        pallet.setPreparationTime(5.0);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(productRepository.findByProductCode("M_EA_B_M")).thenReturn(Optional.of(product));
        when(palletRepository.findById(1)).thenReturn(Optional.of(pallet));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));


        ClientNotActiveException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        ClientNotActiveException.class,
                        () -> orderService.createOrder(orderRequest)
                );
    }


    @Test
    void testCreateOrder_withProductNotFound() {
        // Arrange
        LocalDate now = LocalDate.now();
        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setClientName("Fresh Fruits Inc");

        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setProductCode("M_EA_B_M");
        //itemDto.setItemWeight(500.0);
        itemDto.setPalletId(1);
        itemDto.setPricePerKg(2.5);
        itemDto.setPackaging(1);
        itemDto.setNumberOfPallets(1);


        orderRequest.setItems(List.of(itemDto));
        orderRequest.setCurrency(OrderCurrency.EUR.toString());
        orderRequest.setProductionDate(now);

        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        Product product = new Product();
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(1000.0);

        Pallet pallet = new Pallet();
        pallet.setPackaging(1);
        pallet.setPreparationTime(5.0);

        Forex forex = new Forex();
        forex.setCurrency(ForexCurrency.EUR);
        forex.setBuyingRate(12);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(palletRepository.findByPackaging(1)).thenReturn(pallet);
        when(forexRepository.findByCurrency(ForexCurrency.EUR)).thenReturn(Optional.of(forex));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));


        ProductNotFoundException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        ProductNotFoundException.class,
                        () -> orderService.createOrder(orderRequest)
                );
    }

    @Test
    void testCreateOrder_withProductLowStock() {

        LocalDateTime localDateTime = LocalDateTime.now();

        // Mock a client
        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        // Mock a product
        Product product = new Product();
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(0.0); // Simulating low stock

        // Mock a pallet
        Pallet pallet = new Pallet();
        pallet.setPreparationTime(5.0);
        pallet.setPalletId(1);

        // Mock a forex:
        Forex forex = new Forex();
        forex.setCurrency(ForexCurrency.EUR);
        forex.setBuyingRate(10.0);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(palletRepository.findById(1)).thenReturn(Optional.of(pallet));
        when(forexRepository.findByCurrency(ForexCurrency.EUR)).thenReturn(Optional.of(forex));

        // Create an order request
        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setClientName("Fresh Fruits Inc");
        orderRequest.setCurrency("EUR");

        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setProductCode("M_EA_B_M");
        //itemDto.setItemWeight(500.0);
        itemDto.setPalletId(1);
        itemDto.setPricePerKg(2.5);
        itemDto.setPackaging(1);
        itemDto.setNumberOfPallets(1);


        orderRequest.setItems(List.of(itemDto));
        orderRequest.setCurrency(OrderCurrency.EUR.toString());
        orderRequest.setProductionDate(localDateTime.toLocalDate());

        // Act & Assert

        Assertions.assertThrows(
                ProductLowStock.class,
                () -> orderService.createOrder(orderRequest)
        );
    }

    @Test
    void testCancelOrderSuccess(){
        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        Product product = new Product();
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(1000.0);

        Order order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setStatus(OrderStatus.PRELIMINARY);

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setProduct(product);
        orderItem1.setOrderCurrency(OrderCurrency.MAD);
        orderItem1.setItemWeight(300.0);
        orderItem1.setPricePerKg(3);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setProduct(product);
        orderItem2.setOrderCurrency(OrderCurrency.MAD);
        orderItem2.setItemWeight(300.0);
        orderItem2.setPricePerKg(3);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        order.setOrderItems(orderItems);


        Pallet pallet = new Pallet();

        pallet.setPreparationTime(5.0);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(productRepository.findByProductCode("M_EA_B_M")).thenReturn(Optional.of(product));
        when(palletRepository.findById(1)).thenReturn(Optional.of(pallet));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<Object> response = orderService.cancelOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order has been cancelled.", response.getBody());
        assertEquals(0,orderItemRepository.findAll().size());
    }

    @Test
    void testCancelOrderInProduction() {

        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        Product product = new Product();
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(1000.0);

        Order order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setStatus(OrderStatus.IN_PRODUCTION);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrderCurrency(OrderCurrency.MAD);
        orderItem.setItemWeight(300.0);
        orderItem.setPricePerKg(3);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);

        Pallet pallet = new Pallet();

        pallet.setPreparationTime(5.0);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(productRepository.findByProductCode("M_EA_B_M")).thenReturn(Optional.of(product));
        when(palletRepository.findById(1)).thenReturn(Optional.of(pallet));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<Object> response = orderService.cancelOrder(1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Order is already in production and cannot be canceled at the moment...",response.getBody());
    }

    @Test
    void testCancelOrderShipped_failed() throws Exception {
        Client client = new Client();
        client.setClientStatus(ClientStatus.ACTIVE);
        client.setCompanyName("Fresh Fruits Inc");

        Product product = new Product();
        product.setProductCode("M_EA_B_M");
        product.setTotalWeight(1000.0);

        Order order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setStatus(OrderStatus.SHIPPED);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrderCurrency(OrderCurrency.MAD);
        orderItem.setItemWeight(300.0);
        orderItem.setPricePerKg(3);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);

        Pallet pallet = new Pallet();

        pallet.setPreparationTime(5.0);

        when(clientRepository.findByCompanyName("Fresh Fruits Inc")).thenReturn(client);
        when(productRepository.findByProductCode("M_EA_B_M")).thenReturn(Optional.of(product));
        when(palletRepository.findById(1)).thenReturn(Optional.of(pallet));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderStatusDto orderStatusDto = new OrderStatusDto();
        orderStatusDto.setNewStatus("CANCELED");

        OrderCannotBeCanceledException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        OrderCannotBeCanceledException.class,
                        () -> orderService.updateOrderStatus(1L, orderStatusDto)
                );
    }

    @Test
    void updateOrderTest() {
        // === Setup Entities ===
        Client c = new Client();
        c.setCompanyName("Samid Cor");
        c.setClientId(1);
        c.setClientStatus(ClientStatus.ACTIVE);

        Product p1 = new Product(); // will be deleted
        p1.setProductId(1L);
        p1.setProductCode("M_EA_B_M");
        p1.setTotalWeight(2000.0);

        Product p2 = new Product(); // will be updated
        p2.setProductId(2L);
        p2.setProductCode("M_EC_B_M");
        p2.setTotalWeight(2000.0);

        Pallet pallet1 = new Pallet();
        pallet1.setPalletId(1);
        pallet1.setPreparationTime(5.0);
        pallet1.setTotalNet(300.0f);

        OrderItem oi1 = new OrderItem(); // to be deleted
        oi1.setId(1L);
        oi1.setProduct(p1);
        oi1.setOrderCurrency(OrderCurrency.USD);
        oi1.setPallet(pallet1);
        oi1.setItemWeight(500.0);
        oi1.setPricePerKg(2.0);

        OrderItem oi2 = new OrderItem(); // to be updated
        oi2.setId(2L);
        oi2.setProduct(p2);
        oi2.setItemWeight(800.0);
        oi2.setPricePerKg(2.0);
        oi2.setPallet(pallet1);
        oi2.setOrderCurrency(OrderCurrency.USD);
        oi2.setBrand("Medjool Star");

        Order o = new Order();
        o.setId(1L);
        o.setClient(c);
        o.setCurrency(OrderCurrency.valueOf("USD"));
        o.setStatus(OrderStatus.PRELIMINARY);
        o.setOrderItems(new ArrayList<>(List.of(oi1, oi2)));
        o.setProductionDate(LocalDateTime.now());

        OrderItemRequestDto addedItem = new OrderItemRequestDto();
        addedItem.setProductCode("M_EC_B_M");
        addedItem.setItemBrand("Medjool Star");
        addedItem.setPalletId(1);
        addedItem.setNumberOfPallets(3);
        //addedItem.setItemWeight(900.0);
        addedItem.setPricePerKg(2.0);
        addedItem.setPackaging(1);

        OrderItemUpdateRequestDto updatedItem = new OrderItemUpdateRequestDto();
        updatedItem.setItemId(2L);
        updatedItem.setProductCode("M_EC_B_M");
        updatedItem.setNewBrand("Oum Toumour");
        updatedItem.setNewWeight(1000.0);
        updatedItem.setNewPricePerKg(2.5);
        updatedItem.setNewNumberOfPallets(4);
        updatedItem.setNewPackaging(0.5);

        OrderUpdateRequestDto dto = new OrderUpdateRequestDto();
        dto.setClientName("Samid Cor");
        dto.setItemsDeleted(List.of(1L));
        dto.setItemsAdded(List.of(addedItem));
        dto.setUpdatedItems(List.of(updatedItem));

        // === Mock Repositories ===
        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(o));
        when(orderItemRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(oi1));
        when(orderItemRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(oi2));
        when(productRepository.findByProductCodeForUpdate("M_EA_B_M")).thenReturn(Optional.of(p1));
        when(productRepository.findByProductCodeForUpdate("M_EC_B_M")).thenReturn(Optional.of(p2));
        when(palletRepository.findAll()).thenReturn(List.of(pallet1));

        // === Call Service Method ===
        ResponseEntity<?> responseDto = orderService.updateOrder(1L, dto);

        // === Assertions ===
        assertNotNull(responseDto);
        assertEquals(1L, o.getId());

        // Total price: 2.5 * 1000 (updated item) + 2.0 * 900 (new item)
        double expectedTotalPrice = 2.5 * 1000 + 2.0 * 900;
        double expectedTotalWeight = 1000.0 + 900.0;

        assertEquals(expectedTotalPrice, o.getTotalPrice(), 0.001);
        assertEquals(expectedTotalWeight, o.getTotalWeight(), 0.001);

        assertEquals(2, o.getOrderItems().size());

        assertFalse(o.getOrderItems().contains(oi1)); // Deleted item gone

        assertTrue(o.getOrderItems().stream()
                .anyMatch(item -> item.getBrand().equals("Oum Toumour"))); // Updated brand
    }

}