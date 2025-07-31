package integration_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.model.Product;
import com.example.medjool.repository.ProductRepository;
import com.example.medjool.services.implementation.StockServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MedjoolApplication.class)
@ActiveProfiles("test")
@Transactional
public class StockServiceIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockServiceImpl stockService;

    @Test
    public void testGetAllStock(){}


    @Test
    void testUpdateStock_Success() throws Exception {

        File csvFile = new File("C:\\Users\\s.samid\\Downloads\\medjool_demo_back\\src\\main\\resources\\stock_update.csv");
        FileInputStream inputStream = new FileInputStream(csvFile);
        MultipartFile file = new MockMultipartFile("file", csvFile.getName(), "text/csv", inputStream);

        ResponseEntity<Object> response = stockServic.updateStock(file);

        // Vérifications
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Stock updated successfully", response.getBody());

        // Pick a product to verify if the weight has been updated:
        Product p = productRepository.findByProductCode("S00_EA0_D_MS").get();
        assertEquals(p.getTotalWeight(),398837.20);
    }
}
