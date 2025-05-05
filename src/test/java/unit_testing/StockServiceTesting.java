package unit_testing;
/**
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StockServiceTesting {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewProduct_ProductAlreadyExists() {

        NewProductDto newProductDto = new NewProductDto();
        newProductDto.setCallibre("BBS");
        newProductDto.setColor("Dark");
        newProductDto.setQuality("Export A");
        newProductDto.setFarm("Medjool");

        Product existingProduct = new Product();
        when(productRepository.findByCallibreAndColorAndQualityAndFarm(
                "BBS", "Dark", "Export A", "Medjool")).thenReturn(existingProduct);

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
        newProductDto.setColor("Red");
        newProductDto.setQuality("High");
        newProductDto.setFarm("Farm1");
        newProductDto.setTotalWeight(100.0);

        when(productRepository.findByCallibreAndColorAndQualityAndFarm(
                "A", "Red", "High", "Farm1")).thenReturn(null);


        ResponseEntity<Object> response = stockService.createNewProduct(newProductDto);

        // Vérifications
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New product created successfully", response.getBody());
        verify(productRepository, times(1)).save(any(Product.class));
    }

        @Test
        void testUpdateStock_Success() throws Exception {

            // Create e CSV content:
            String csvContent = "product_id,quantity\n123,50\n456,30";
            MockMultipartFile file = new MockMultipartFile("file", "stock.csv", "text/csv", csvContent.getBytes());


            Product product1 = new Product();
            product1.setProductId("123");
            product1.setTotalWeight(100.0);

            Product product2 = new Product();
            product2.setProductId("456");
            product2.setTotalWeight(200.0);

            when(productRepository.findById("123")).thenReturn(Optional.of(product1));
            when(productRepository.findById("456")).thenReturn(Optional.of(product2));


            ResponseEntity<Object> response = stockService.updateStock(file);

            // Vérifications
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("Stock updated successfully", response.getBody());
            assertEquals(50.0, product1.getTotalWeight());
            assertEquals(30.0, product2.getTotalWeight());
            verify(productRepository, times(2)).save(any(Product.class));
        }

        @Test
        void testUpdateStock_ProductNotFound() throws Exception {

            String csvContent = "product_id,quantity\n789,20";
            MockMultipartFile file = new MockMultipartFile("file", "stock.csv", "text/csv", csvContent.getBytes());

            // Mock pour un produit non trouvé
            when(productRepository.findById("789")).thenReturn(Optional.empty());

            // Appel de la méthode à tester
            ResponseEntity<Object> response = stockService.updateStock(file);

            // Vérifications
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("Stock updated successfully", response.getBody());
            verify(productRepository, never()).save(any(Product.class));
        }
    }**/