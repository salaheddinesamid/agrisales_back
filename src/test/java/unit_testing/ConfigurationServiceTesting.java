package unit_testing;

import com.example.medjool.dto.*;
import com.example.medjool.exception.ClientAlreadyFoundException;
import com.example.medjool.model.*;
import com.example.medjool.repository.AddressRepository;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.repository.ContactRepository;
import com.example.medjool.repository.PalletRepository;
import com.example.medjool.services.implementation.ConfigurationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        Client existedClient = new Client(1, "Client1", "GM", "Export and Import", "CC2233", "www.client1.com", "GMS", List.of(address), List.of(contact), ClientStatus.ACTIVE);
        when(clientRepository.findById(1)).thenReturn(java.util.Optional.of(existedClient));

        // Mock updated client
        UpdateAddressDto updateAddressDto = new UpdateAddressDto(1L, "Algeria", "Street 20", "State", "3900", "Oran");
        UpdateContactDto updateContactDto = new UpdateContactDto(1, "FN", "", "contact@outlook.com");
        UpdateClientDto updateClientDto = new UpdateClientDto("Mafriq Limited", "Samid", "Export and Import", List.of(updateAddressDto), List.of(updateContactDto),"www.mafriq.com", "ACTIVE", "RR");

        // Simulate save behavior
        when(clientRepository.save(existedClient)).thenReturn(existedClient);

        // Call the service method
        ResponseEntity<Object> response = configurationService.updateClient(1, updateClientDto);

        // Verification
        assertEquals(200, response.getStatusCodeValue());
    }


    //  --------------- Pallet tests ------------------//

}