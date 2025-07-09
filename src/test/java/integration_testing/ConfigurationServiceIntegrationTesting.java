package integration_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.dto.UpdateAddressDto;
import com.example.medjool.dto.UpdateClientDto;
import com.example.medjool.dto.UpdateContactDto;
import com.example.medjool.model.Address;
import com.example.medjool.model.Client;
import com.example.medjool.model.ClientStatus;
import com.example.medjool.model.Contact;
import com.example.medjool.repository.AddressRepository;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.repository.ContactRepository;
import com.example.medjool.services.ConfigurationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MedjoolApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ClientIntegrationTest {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void testUpdateClientInformation_Integration() {
        // Setup: save initial data
        Address address = addressRepository.save(new Address(null, "Morocco", "Street 1", "State", "2300", "City"));
        Contact contact = contactRepository.save(new Contact(null, "D1", "contact@gmail.com", "079082"));

        Client client = new Client(1, "Client1", "GM", "Export and Import", "CC2233", "www.client1.com", "GMS", 10f, List.of(address), List.of(contact), ClientStatus.ACTIVE);
        client = clientRepository.save(client); // save and get ID

        // Prepare update DTOs
        UpdateAddressDto updateAddressDto = new UpdateAddressDto(address.getAddressId(), "Algeria", "Street 20", "State", "3900", "Oran");
        UpdateContactDto updateContactDto = new UpdateContactDto(contact.getContactId(), "FN", "", "contact@outlook.com");
        UpdateClientDto updateClientDto = new UpdateClientDto("Mafriq Limited", "Samid", "Export and Import", List.of(updateAddressDto), List.of(updateContactDto), null, null, "", "ACTIVE", 10f, "RR");

        // Act
        ResponseEntity<Object> response = configurationService.updateClient(client.getClientId(), updateClientDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());

        Client updatedClient = clientRepository.findById(client.getClientId()).orElseThrow();
        assertEquals("Mafriq Limited", updatedClient.getCompanyName());
        assertEquals("Samid", updatedClient.getGeneralManager());
        assertEquals("FN", updatedClient.getContacts().get(0).getDepartment());
        assertEquals("Algeria", updatedClient.getAddresses().get(0).getCountry());
    }
}
