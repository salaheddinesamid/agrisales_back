package unit_testing;

import com.example.medjool.component.StockInitializer;
import com.example.medjool.dto.NewProductDto;
import com.example.medjool.model.Product;

import com.example.medjool.repository.ProductRepository;
import com.example.medjool.services.implementation.StockServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

    private static final String CSV_CONTENT = "product_code,total_weight\nP001,10.5\nP002,20.0\n";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //sockInitializer.initialize();
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
    void testUpdateStock_SuccessfulUpdate() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "stock.csv", "text/csv", CSV_CONTENT.getBytes());

        Product product1 = new Product();
        product1.setProductCode("P001");
        product1.setTotalWeight(50.0);

        Product product2 = new Product();
        product2.setProductCode("P002");
        product2.setTotalWeight(30.0);

        when(productRepository.findByProductCode("P001")).thenReturn(Optional.of(product1));
        when(productRepository.findByProductCode("P002")).thenReturn(Optional.of(product2));

        // We mock the repository save() method
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Spy to verify private method indirectly (or move updateAnalytics to separate service for mocking)
        StockServiceImpl spyService = Mockito.spy(stockService);
        doNothing().when(spyService).updateAnalytics(anyList(), eq(34)); // Example weekNumber

        // When
        ResponseEntity<Object> response = spyService.updateStock(mockFile, 34);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stock updated successfully", response.getBody());

        verify(productRepository, times(2)).save(any());
        verify(spyService).updateAnalytics(anyList(), eq(34));
    }

        @Test
        void testUpdateStock_ProductNotFound() throws Exception {

            String csvContent = "product_id,quantity\n789,20";
            MockMultipartFile file = new MockMultipartFile("file", "stock.csv", "text/csv", csvContent.getBytes());

            // Mock pour un produit non trouvé
            when(productRepository.findByProductCode("789")).thenReturn(Optional.empty());

            // Appel de la méthode à tester
            ResponseEntity<Object> response = stockService.updateStock(file,2);

            // Vérifications
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("Stock updated successfully", response.getBody());
            verify(productRepository, never()).save(any(Product.class));
        }
    }