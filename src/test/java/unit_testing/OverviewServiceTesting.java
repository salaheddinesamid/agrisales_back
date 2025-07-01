package unit_testing;


import com.example.medjool.model.Client;
import com.example.medjool.model.Order;
import com.example.medjool.model.Pallet;
import com.example.medjool.repository.OrderRepository;
import com.example.medjool.repository.ProductRepository;
import com.example.medjool.repository.SystemSettingRepository;
import com.example.medjool.services.implementation.AlertServiceImpl;
import com.example.medjool.services.implementation.OverviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OverviewServiceTesting {

    @Mock
    private ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    SystemSettingRepository systemSettingRepository;

    @Mock
    private AlertServiceImpl alertService;

    @InjectMocks
    private OverviewServiceImpl overviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void getAnOverview(){

    }


    @Test
    void testMarginPerClientSuccess(){
        // Mock the client:
        Client client = new Client();
        client.setClientId(1);
        client.setCompanyName("Test Client");
        // Mock the pallets:
        Pallet p1 = new Pallet();
        p1.setPalletId(1);
        p1.setProductionCost(2f);
        p1.setFuelCost(1.5f);
        p1.setLaborTransportCost(3f);
        p1.setVat(4f);
        Pallet p2 = new Pallet();

        // Mock the orders:
        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();


    }
}
