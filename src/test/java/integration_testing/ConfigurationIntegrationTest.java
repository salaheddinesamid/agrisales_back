package integration_testing;

import com.example.medjool.MedjoolApplication;
import com.example.medjool.dto.*;
import com.example.medjool.model.*;
import com.example.medjool.repository.AddressRepository;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.repository.ContactRepository;
import com.example.medjool.repository.PalletRepository;
import com.example.medjool.services.implementation.ConfigurationServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MedjoolApplication.class)
@ActiveProfiles("test")
@Transactional
public class ConfigurationIntegrationTest {

    @Autowired
    private ConfigurationServiceImpl configurationService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PalletRepository palletRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;


    /** * This test verifies that a client can be successfully created with all required fields.
     * It checks that the client is saved in the database and that all fields are correctly set.
     */
    @Test
    public void testCreateClientSuccess(){

        // Given
        ContactDto contactDto = new ContactDto(
                "Management",
                "testclient@gmail.com",
                "000000"
        );

        AddressDto addressDto = new AddressDto(
                "Test Street",
                "Test City",
                "Test State",
                "12345",
                "Test Country"
        );
        ClientDto clientDto = new ClientDto(
                "Test client",
                "Manager",
                "Trading",
                "www.website.com",
                "Export A",
                10,
                List.of(contactDto),
                List.of(addressDto),
                "ACTIVE",
                "siret"

        );
        // When
        ResponseEntity<Object> response = configurationService.addClient(clientDto);
        // Then
        assertEquals(201,response.getStatusCodeValue());
        assertNotNull(clientRepository.findByCompanyName(clientDto.getCompanyName()));

        Client client = clientRepository.findByCompanyName(clientDto.getCompanyName());

        // Ensure that all the columns are set correctly:
        assertNotNull(client.getCompanyName());
        assertNotNull(client.getCompanyActivity());
        assertNotNull(client.getCommission());
        assertNotNull(client.getContacts());
        assertNotNull(client.getAddresses());
    }

    /** * This test verifies that a pallet can be successfully created with all required fields.
     * It checks that the client is saved in the database and that all fields are correctly set.
     */
    @Test
    public void testCreatePalletSuccess(){

        PalletDto palletDto = new PalletDto();
        palletDto.setPackaging(1);
        palletDto.setNumberOfBoxesInCarton(11);
        palletDto.setNumberOfCartonsInStory(10);
        palletDto.setNumberOfStoriesInPallet(8);

        // Costs:
        palletDto.setFuelCost(2);
        palletDto.setPackagingAT(1);
        palletDto.setPreliminaryLogistics(3);
        palletDto.setMarkupCost(0.8f);
        palletDto.setLaborTransportCost(2);
        palletDto.setLaborCost(4);
        palletDto.setInsuranceCost(0.33f);
        palletDto.setDatePurchase(0.99f);
        palletDto.setProductionCost(2);
        palletDto.setPackagingCost(1.88f);
        palletDto.setVat(2);
        palletDto.setTransportCost(3);

        // Dimensions:
        palletDto.setHeight(180);
        palletDto.setWidth(200);
        palletDto.setLength(100);


        // call the service:
        ResponseEntity<Object> response = configurationService.addPallet(palletDto);

        assertEquals(200, response.getStatusCodeValue());
        // Ensure all the costs are included:
        Pallet pallet = palletRepository.findByPackaging(1);
        assertEquals(2, pallet.getFuelCost());
        assertEquals(1, pallet.getPackagingAT());
        assertEquals(3, pallet.getPreliminaryLogisticsCost());
        assertEquals(0.8f, pallet.getMarkUpCost());
        assertEquals(2, pallet.getLaborTransportCost());
        assertEquals(4, pallet.getLaborCost());
        assertEquals(0.33f, pallet.getInsuranceCost());
        assertEquals(0.99f, pallet.getDatePurchase());
        assertEquals(2, pallet.getProductionCost());
        assertEquals(1.88f, pallet.getPackagingCost());
        assertEquals(2, pallet.getVat());
        assertEquals(3, pallet.getTransportationCost());
    }

    /** * This test verifies that a client can be successfully updated with new information.
     * It checks that the updated client is saved in the database and that all fields are correctly set.
     */
    @Test
    public void updateExistingClientSuccess(){

        // Mock an address:
        Address address = new Address(
                null,
                "",
                "",
                "",
                "",
                "12345"
        );

        addressRepository.save(address);
        // Mock a contact:
        Contact contact = new Contact(
                null,
                "Management",
                "email@gmail.com",
                "000000"
        );

        contactRepository.save(contact);
        // Mock a client
        Client client = new Client(
                null,
                "Test client",
                "Manager",
                "Trading",
                "--",
                "www.website.com",
                "Export A",
                10f,
                List.of(address),
                List.of(contact),
                ClientStatus.ACTIVE,
                null
        );

        clientRepository.save(client);
        assertEquals(client, clientRepository.findByCompanyName(client.getCompanyName()));

        UpdateClientDto updateClientDto = getUpdateClientDto(client);

        // Call the service:

        ResponseEntity<Object> response = configurationService.updateClient(client.getClientId(),updateClientDto);
        assertEquals(200,response.getStatusCodeValue());

        // Ensure that the client is updated in the database:

        // Ensure that the contacts are updated:

        // Ensure that the addresses are updated:
    }

    private static UpdateClientDto getUpdateClientDto(Client client) {
        Address address1 = client.getAddresses().get(0);
        Contact contact1 = client.getContacts().get(0);

        UpdateAddressDto updateAddressDto = new UpdateAddressDto();
        updateAddressDto.setAddressId(address1.getAddressId());
        updateAddressDto.setCity("");
        updateAddressDto.setCountry("");
        updateAddressDto.setStreet("");
        updateAddressDto.setZip("");

        UpdateContactDto updateContactDto = new UpdateContactDto();
        updateContactDto.setContactId(contact1.getContactId());
        updateContactDto.setNewEmailAddress("");
        updateContactDto.setNewDepartmentName("");
        updateContactDto.setNewPhoneNumber("");

        // Create update client dto:
        return new UpdateClientDto(
                "Interfood",
                "John Doe",
                "Import & Export",
                List.of(updateAddressDto),
                List.of(updateContactDto),
                null,
                null,
                "www.interfood.com",
                "INACTIVE",
                12f,
                "1234567890"
        );
    }

    /** * This test verifies that a pallet can be successfully updated with new information.
     * It checks that the updated pallet is saved in the database and that all fields are correctly set.
     */
    @Test
    public void updatePalletSuccess(){
        // Mock a pallet:
        Pallet pallet = new Pallet(
                null,
                1f,
                11,
                10,
                8,
                null,
                null,
                3f,
                0.8f,
                2f,
                4f,
                0.33f,
                0.99f,
                2f,
                1.88f,
                2f,
                3f,
                1f,
                1f,
                180,
                200,
                100,
                900.0f,
                "",
                "",
                10
        );

        palletRepository.save(pallet);
        assertEquals(pallet, palletRepository.findByPackaging(1));

        // Create update pallet dto:
        UpdatePalletDto updatePalletDto = new UpdatePalletDto(
                1,12,11,9,3,2,4,0.9f,3,5,0.44f,1.99f,3,2.88f,3,4, 2, 1.9f, 210, 110, 100, 900f, 10, "",""
        );

        // Call the service:
        ResponseEntity<Object> response = configurationService.updatePallet(pallet.getPalletId(),updatePalletDto);

        assertEquals(200,response.getStatusCodeValue());

        // Ensure that the pallet is updated in the database:
        Pallet updatedPallet = palletRepository.findByPackaging(updatePalletDto.getPackaging());
        assertNotNull(updatedPallet);

        // Ensure that all the costs are updated:
        assertEquals(updatePalletDto.getTotalCosts(),pallet.getTotalPalletCost());
    }
}
