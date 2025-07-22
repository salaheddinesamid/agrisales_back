package unit_testing;

import com.example.medjool.component.StockInitializer;
import com.example.medjool.dto.NewProductDto;
import com.example.medjool.model.Product;

import com.example.medjool.repository.ProductRepository;
import com.example.medjool.services.implementation.StockServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StockServiceTesting {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockInitializer sockInitializer;

    @InjectMocks
    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sockInitializer.initialize();
    }

    @Test
    void testCreateNewProduct_ProductAlreadyExists() {

        NewProductDto newProductDto = new NewProductDto();
        newProductDto.setCallibre("BBS");
        newProductDto.setQuality("Export A");
        newProductDto.setFarm("Medjool");

        Product existingProduct = new Product();
        when(productRepository.findByCallibreAndQualityAndFarm(
                "BBS",  "Export A", "Medjool")).thenReturn(existingProduct);

        // Call the service method
        ResponseEntity<Object> response = stockService.createNewProduct(newProductDto);

        // Verification
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Product already exists", response.getBody());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testCreateNewProduct_Success() {

        NewProductDto newProductDto = new NewProductDto();
        newProductDto.setCallibre("A");
        newProductDto.setQuality("Export A");
        newProductDto.setFarm("Farm1");
        newProductDto.setTotalWeight(100.0);

        when(productRepository.findByCallibreAndQualityAndFarm(
                "A",  "Export A", "Farm1")).thenReturn(null);


        ResponseEntity<Object> response = stockService.createNewProduct(newProductDto);

        // Vérifications
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New product created successfully", response.getBody());
        verify(productRepository, times(1)).save(any(Product.class));
    }

        @Test
        void testUpdateStock_Success() throws Exception {

            File csvFile = new File("C:\\Users\\s.samid\\Downloads\\medjool_demo_back\\src\\main\\resources\\stock_update.csv");
            FileInputStream inputStream = new FileInputStream(csvFile);
            MultipartFile file = new MockMultipartFile("file", csvFile.getName(), "text/csv", inputStream);


            Product product1 = new Product();
            product1.setProductId(1L);
            product1.setProductCode("S00_EA0_D_MS");
            product1.setTotalWeight(100.0);


            when(productRepository.findById(1L)).thenReturn(Optional.of(product1));


            ResponseEntity<Object> response = stockService.updateStock(file);

            // Vérifications
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("Stock updated successfully", response.getBody());
            assertEquals(50.0, product1.getTotalWeight());
            verify(productRepository, times(2)).save(any(Product.class));
        }

        @Test
        void testUpdateStock_ProductNotFound() throws Exception {

            String csvContent = "product_id,quantity\n789,20";
            MockMultipartFile file = new MockMultipartFile("file", "stock.csv", "text/csv", csvContent.getBytes());

            // Mock pour un produit non trouvé
            when(productRepository.findByProductCode("789")).thenReturn(Optional.empty());

            // Appel de la méthode à tester
            ResponseEntity<Object> response = stockService.updateStock(file);

            // Vérifications
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("Stock updated successfully", response.getBody());
            verify(productRepository, never()).save(any(Product.class));
        }
    }