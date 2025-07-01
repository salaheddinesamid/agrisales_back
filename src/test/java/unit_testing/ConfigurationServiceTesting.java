package unit_testing;

import com.example.medjool.dto.*;
import com.example.medjool.exception.ClientAlreadyFoundException;
import com.example.medjool.model.*;
import com.example.medjool.repository.*;
import com.example.medjool.services.implementation.ConfigurationServiceImpl;
import org.hibernate.sql.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ConfigurationServiceTesting {


    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PalletRepository palletRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ConfigurationServiceImpl configurationService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    //  --------------- Client tests ------------------//
    @Test
    void testCreateNewClient_AlreadyExists() throws ClientAlreadyFoundException {
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

        Client existedClient = new Client();
        when(clientRepository.findByCompanyName(
                clientDto.getCompanyName()
        )).thenReturn(existedClient);

        // Call the service method

        ClientAlreadyFoundException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        ClientAlreadyFoundException.class,
                        () -> configurationService.addClient(clientDto)
                );

    }


    @Test
    void createNewClient(){
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

        ContactDto contactDto = new ContactDto(
                "Purchase",
                "client1@gmail.com",
                "079082"
        );
        AddressDto addressDto = new AddressDto(
                "Street 1",
                "City",
                "State",
                "2300",
                ""
        );

        List<AddressDto> clientAddresses = List.of(addressDto);
        List<ContactDto> clientContacts = List.of(contactDto);

        clientDto.setAddresses(clientAddresses);
        clientDto.setContacts(clientContacts);

        when(clientRepository.findByCompanyName(
                clientDto.getCompanyName()
        )).thenReturn(null);

        // Call the service method
        ResponseEntity<Object> response = configurationService.addClient(clientDto);


        // Verification
        assertEquals(201, response.getStatusCodeValue());
    }


    @Test
    void getClientAddresses(){

        String companyName = "Client1";

        Address address = new Address(1L, "Morocco", "Street 1", "State", "2300", "City");

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        Contact contact = new Contact(1,"D1","contact@gmail.com","079082");
        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));

        Client existedClient = new Client();
        existedClient.setCompanyName("Client1");
        existedClient.setClientStatus(ClientStatus.ACTIVE);
        existedClient.setAddresses(List.of(address));
        existedClient.setContacts(List.of(contact));

        when(clientRepository.findByCompanyName(companyName)).thenReturn(existedClient);

        // Call the service method:
        ResponseEntity<List<AddressResponseDto>> response = configurationService.getClientAddressesByClientName(companyName);
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }


    @Test
    void testUpdateClientInformation_Success() {
        // Mock address and contact
        Address address = new Address(1L, "Morocco", "Street 1", "State", "2300", "City");
        Contact contact = new Contact(1, "D1", "contact@gmail.com", "079082");
        when(addressRepository.findById(1L)).thenReturn(java.util.Optional.of(address));
        when(contactRepository.findById(1)).thenReturn(java.util.Optional.of(contact));
        // Mock existing client
        Client existedClient = new Client(1, "Client1", "GM", "Export and Import", "CC2233", "www.client1.com", "GMS",10f, List.of(address), List.of(contact), ClientStatus.ACTIVE);
        when(clientRepository.findById(1)).thenReturn(java.util.Optional.of(existedClient));

        // Mock updated client
        UpdateAddressDto updateAddressDto = new UpdateAddressDto(1L, "Algeria", "Street 20", "State", "3900", "Oran");
        UpdateContactDto updateContactDto = new UpdateContactDto(1, "FN", "", "contact@outlook.com");
        UpdateClientDto updateClientDto = new UpdateClientDto("Mafriq Limited", "Samid", "Export and Import", List.of(updateAddressDto), List.of(updateContactDto),null,null,"", "ACTIVE", "RR");

        // Simulate save behavior
        when(clientRepository.save(existedClient)).thenReturn(existedClient);

        // Call the service method
        ResponseEntity<Object> response = configurationService.updateClient(1, updateClientDto);

        // Verification
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("FN",contact.getDepartment());
        assertEquals("Algeria",address.getCountry());
    }

    @Test
    void deleteClientTestSuccess(){
        // Mock the client to be deleted:
        Client client = new Client();
        client.setClientId(1);
        client.setCompanyName("Client1");
        client.setClientStatus(ClientStatus.ACTIVE);

        // Mock an order associated to the client
        Order order = new Order();
        OrderItem orderItem = new OrderItem();

        // Mock a product:
        Product product = new Product();
        product.setProductId(1L);
        product.setProductCode("P001");

        // Mock a pallet:
        Pallet pallet = new Pallet();
        pallet.setPalletId(1);

        orderItem.setPallet(pallet);
        orderItem.setProduct(product);

        order.setId(1L);
        order.setClient(client);
        order.setOrderItems(List.of(orderItem));

        when(clientRepository.findById(1)).thenReturn(java.util.Optional.of(client));
        when(palletRepository.findByPalletId(1)).thenReturn(pallet);
        when(orderItemRepository.findById(1L)).thenReturn(java.util.Optional.of(orderItem));
        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(order));


        ResponseEntity<Object> response = configurationService.deleteClient(1);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(0,addressRepository.findAll().size());
        assertEquals(0,contactRepository.findAll().size());
    }


    //  --------------- Pallet tests ------------------//

    @Test
    void createNewPalletTestSuccess(){

        // Mock the pallet dto:
        PalletDto palletDto = new PalletDto();
        palletDto.setPackaging(1);

        // Basic information:
        palletDto.setNumberOfBoxesInCarton(10);
        palletDto.setNumberOfCartonsInStory(30);
        palletDto.setNumberOfStoriesInPallet(12);

        // Dimension information:
        palletDto.setHeight(100);
        palletDto.setWidth(200);
        palletDto.setLength(300);

        // Costs information:
        palletDto.setProductionCost(2);
        palletDto.setDatePurchase(3);
        palletDto.setLaborCost(4);
        palletDto.setTransportCost(3);
        palletDto.setLaborTransportCost(2.4f);
        palletDto.setInsuranceCost(3);
        palletDto.setVat(4);
        palletDto.setMarkupCost(2.5f);
        palletDto.setPackagingCost(5);
        palletDto.setPreliminaryLogistics(3);
        palletDto.setFuelCost(1);
        palletDto.setNotes("");


        when(palletRepository.findByPackaging(palletDto.getPackaging()))
                .thenReturn(null);

        ResponseEntity<Object> response = configurationService.addPallet(palletDto);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void testDeletePalletAlreadyInUse(){

    }

    @Test
    void testUpdatePalletSuccess(){

        Pallet pallet = new Pallet();
        pallet.setPalletId(1);
        pallet.setNumberOfBoxesInCarton(20);
        pallet.setNumberOfCartonsInStory(30);
        pallet.setNumberOfStoriesInPallet(12);

        UpdatePalletDto updatePalletDto  = new UpdatePalletDto();
        updatePalletDto.setFuelCost(2);
        updatePalletDto.setMarkupCost(3);

        when(palletRepository.findByPalletId(1)).thenReturn(pallet);

        ResponseEntity<Object>  response = configurationService.updatePallet(1, updatePalletDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2,pallet.getFuelCost());
    }
}