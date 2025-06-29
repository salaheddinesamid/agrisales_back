package integration_testing;

import com.example.medjool.dto.ClientDto;
import com.example.medjool.model.Address;
import com.example.medjool.repository.AddressRepository;
import com.example.medjool.services.implementation.ConfigurationServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ConfigurationServiceIntegrationTesting.class)
@Transactional
public class ConfigurationServiceIntegrationTesting {

    @Autowired
    private ConfigurationServiceImpl configurationService;

    @Mock
    private AddressRepository addressRepository;

    @Test
    public void addClient_ShouldRollbackAllChangesWhenAddressSaveFails() {
        // Setup
        ClientDto clientDto = new ClientDto(
                "Client1",
                "GM",
                "Export and Import",
                "www.client1.com",
                "GMS",
                10,
                null,
                null,
                "ACTIVE"

        );

        doThrow(new RuntimeException("DB error"))
                .when(addressRepository).save(any(Address.class));

        // Execute & Verify
        assertThrows(RuntimeException.class, () ->
                configurationService.addClient(clientDto));
    }
}