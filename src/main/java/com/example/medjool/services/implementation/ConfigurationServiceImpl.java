package com.example.medjool.services.implementation;

import com.example.medjool.dto.*;
import com.example.medjool.exception.ClientAlreadyFoundException;
import com.example.medjool.model.*;
import com.example.medjool.repository.AddressRepository;
import com.example.medjool.repository.ClientRepository;
import com.example.medjool.repository.ContactRepository;
import com.example.medjool.repository.PalletRepository;
import com.example.medjool.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** * Implementation of the ConfigurationService interface for managing clients and pallets.
 */

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;
    private final PalletRepository palletRepository;

    @Autowired
    public ConfigurationServiceImpl(ClientRepository clientRepository, AddressRepository addressRepository, ContactRepository contactRepository, PalletRepository palletRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.palletRepository = palletRepository;
    }

    /**     * Adds a new client to the system.
     *
     * @param clientDto the DTO containing client details
     * @return ResponseEntity with the created client or an error message
     */
    @Override
    public ResponseEntity<Object> addClient(ClientDto clientDto) {

        if(clientRepository.findByCompanyName(clientDto.getCompanyName())!=null){
            throw new ClientAlreadyFoundException("Client already exists");
        }

        Client client = new Client();
        // Set Client details
        client.setCompanyName(clientDto.getCompanyName());
        client.setPreferredProductQuality(clientDto.getPreferredProductQuality());
        client.setCompanyActivity(clientDto.getCompanyActivity());
        client.setGeneralManager(clientDto.getGeneralManager());
        client.setClientStatus(ClientStatus.valueOf(clientDto.getStatus()));
        List<Address> clientAddresses = clientDto
                .getAddresses().stream().map(addressDto -> {
                    Address address = new Address();
                    address.setCity(addressDto.getCity());
                    address.setCountry(addressDto.getCountry());
                    address.setStreet(addressDto.getStreet());
                    address.setState(addressDto.getState());
                    address.setPostalCode(addressDto.getPostalCode());
                    addressRepository.save(address);
                    return address;
                }).toList();

        List<Contact> clientContacts = clientDto.getContacts().stream().map(
                contactDto -> {
                    Contact contact = new Contact();
                    contact.setEmail(contactDto.getEmail());
                    contact.setPhone(contactDto.getPhone());
                    contact.setDepartment(contactDto.getDepartment());
                    contactRepository.save(contact);
                    return contact;
                }
        ).toList();

        client.setAddresses(clientAddresses);
        client.setContacts(clientContacts);

        // Add client commission:
        client.setCommission(clientDto.getCommission());


        // Save the client
        clientRepository.save(client);

        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }


    /**
     * Retrieves all clients from the system.
     *
     * @return ResponseEntity containing a list of all clients
     */
    @Override
    public ResponseEntity<List<ClientResponseDto>> getAll(){
        List<Client> clients = clientRepository.findAll();
        List<ClientResponseDto> response = clients.stream()
                .map(ClientResponseDto::new).toList();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @Override
    @Transactional
    public ResponseEntity<Object> updateClient(Integer clientId, UpdateClientDto updateClientDto) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isEmpty()) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
        }

        Client client = optionalClient.get();

        client.setCompanyName(updateClientDto.getNewCompanyName());
        client.setGeneralManager(updateClientDto.getNewGeneralManager());
        client.setCompanyActivity(updateClientDto.getNewCompanyActivity());

        List<Address> newClientAddresses = updateClientDto.getNewAddresses().stream().map(
                addressDto -> {
                    Address address = addressRepository.findById(addressDto.getAddressId()).orElse(null);
                    assert address != null;
                    address.setCity(addressDto.getCity());
                    address.setCountry(addressDto.getCountry());
                    address.setStreet(addressDto.getStreet());
                    address.setState(addressDto.getState());
                    address.setPostalCode(addressDto.getZip());
                    return address;
                }
        ).toList();


        client.setAddresses(newClientAddresses);

        List<Contact> newClientContacts = updateClientDto.getNewContacts().stream().map(
                contactDto -> {
                    Contact contact = contactRepository.findById(contactDto.getContactId()).orElse(null);
                    assert contact != null;
                    contact.setEmail(contactDto.getNewEmailAddress());
                    contact.setPhone(contactDto.getNewPhoneNumber());
                    contact.setDepartment(contactDto.getNewDepartmentName());
                    return contact;
                }
        ).toList();


        client.setContacts(newClientContacts);
        client.setSIRET(updateClientDto.getSiret());
        client.setWebSite(updateClientDto.getWebsite());
        client.setClientStatus(ClientStatus.valueOf(updateClientDto.getClientStatus()));
        return new ResponseEntity<>("Client updated successfully", HttpStatus.OK);
    }



    /**     * Deletes a client by ID.
     *
     * @param id the ID of the client to delete
     * @return ResponseEntity with a success message or an error message
     */
   @Override
    public ResponseEntity<Object> deleteClient(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client with ID " + id + " not found"));

        clientRepository.delete(client);
        return new ResponseEntity<>("The client has been deleted", HttpStatus.OK);
    }

    private List<AddressResponseDto> convertToAddressDto(List<Address> addresses) {
        return
                addresses.stream().map(address -> {
                    AddressResponseDto addressResponseDto = new AddressResponseDto();
                    addressResponseDto.setCity(address.getCity());
                    addressResponseDto.setCountry(address.getCountry());
                    addressResponseDto.setStreet(address.getStreet());
                    addressResponseDto.setState(address.getState());
                    addressResponseDto.setPostalCode(address.getPostalCode());
                    return addressResponseDto;
                }).toList();
    }


    /**     * Retrieves addresses of a client by client ID.
     *
     * @param id the ID of the client
     * @return ResponseEntity containing a list of addresses for the client
     */
    @Override
    public ResponseEntity<List<AddressResponseDto>> getClientAddresses(Integer id){
        List<Address> addresses = clientRepository.findById(id).get().getAddresses();

        List<AddressResponseDto> addressResponseDtos = convertToAddressDto(addresses);

        return new ResponseEntity<>(addressResponseDtos,HttpStatus.OK);
    }

    /**     * Retrieves addresses of a client by client name.
     *
     * @param clientName the name of the client
     * @return ResponseEntity containing a list of addresses for the client
     */
    @Override
    public ResponseEntity<List<AddressResponseDto>> getClientAddressesByClientName(String clientName) {
        List<Address> addresses = clientRepository.findByCompanyName(clientName).getAddresses();

        List<AddressResponseDto> addressResponseDtos =
                convertToAddressDto(addresses);
        return new ResponseEntity<>(addressResponseDtos,HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Object> addPallet(PalletDto palletDto) {
        Pallet pallet = palletRepository.findByPackaging(palletDto.getPackaging());

        if(pallet != null){
            return new ResponseEntity<>("Pallet already exists", HttpStatus.CONFLICT);
        }
        Pallet newPallet = new Pallet();

        int totalBoxes = 0;

        if(palletDto.getPackaging() == 5){
            newPallet.setPackaging(5);
            newPallet.setNumberOfBoxesInStory(palletDto.getNumberOfBoxesInStory());
            newPallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());
            totalBoxes = newPallet.getNumberOfBoxesInStory() * newPallet.getNumberOfStoriesInPallet();
            newPallet.setNumberOfBoxesInPallet(totalBoxes);
        }else{
            newPallet.setPackaging(palletDto.getPackaging());
            newPallet.setNumberOfBoxesInCarton(palletDto.getNumberOfBoxesInCarton());
            newPallet.setNumberOfCartonsInStory(palletDto.getNumberOfCartonsInStory());
            newPallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());

            totalBoxes = newPallet.getNumberOfBoxesInCarton() * newPallet.getNumberOfCartonsInStory() * newPallet.getNumberOfStoriesInPallet();
            newPallet.setNumberOfBoxesInPallet(totalBoxes);
        }

        // Dimensions:
        newPallet.setHeight(palletDto.getHeight());
        newPallet.setWidth(palletDto.getWidth());
        newPallet.setLength(palletDto.getLength());

        // Costs:
        newPallet.setProductionCost(palletDto.getProductionCost());
        newPallet.setDatePurchase(palletDto.getDatePurchase());
        newPallet.setLaborCost(palletDto.getLaborCost());
        newPallet.setPackagingCost(palletDto.getPackagingCost());
        newPallet.setTransportationCost(palletDto.getTransportCost());
        newPallet.setTransportationCost(palletDto.getTransportCost());
        newPallet.setMarkUpCost(palletDto.getMarkupCost());
        newPallet.setVat(palletDto.getVat());
        newPallet.setPreliminaryLogisticsCost(palletDto.getPreliminaryLogistics());
        newPallet.setInsuranceCost(palletDto.getInsuranceCost());

        // Preparation hours:
        newPallet.setPreparationTime(palletDto.getPreparationTime());
        newPallet.setPackaging(palletDto.getPackaging());
        newPallet.setTag(palletDto.getTag());
        newPallet.setTotalNet(palletDto.getTotalNet());

        palletRepository.save(newPallet);
        return ResponseEntity.ok().body(newPallet);
    }

    /**     * Retrieves all pallets from the system.
     *
     * @return ResponseEntity containing a list of all pallets
     */
    @Override
    public ResponseEntity<List<Pallet>> getAllPallets(){
        List<Pallet> pallets = palletRepository.findAll();
        return ResponseEntity.ok().body(pallets);
    }

    /**     * Updates an existing pallet by ID.
     *
     * @param id the ID of the pallet to update
     * @param palletDto the DTO containing updated pallet details
     * @return ResponseEntity with a success message or an error message
     */
    @Override
    public ResponseEntity<Object> updatePallet(Integer id, UpdatePalletDto palletDto) {
        Pallet pallet = palletRepository.findByPalletId(id);

        // Update dimensions:
        pallet.setHeight(palletDto.getHeight());
        pallet.setWidth(palletDto.getWidth());
        pallet.setLength(palletDto.getLength());

        // Update costs:

        // Update preparation time:
        pallet.setPreparationTime(palletDto.getPreparationTime());
        pallet.setTag(palletDto.getTag());
        pallet.setTotalNet(palletDto.getTotalNet());
        pallet.setPackaging(palletDto.getPackaging());

        // Update basic information:
        pallet.setNumberOfBoxesInCarton(palletDto.getNumberOfBoxesInCarton());
        pallet.setNumberOfCartonsInStory(palletDto.getNumberOfCartonsInStory());
        pallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());
        pallet.setNumberOfBoxesInStory(palletDto.getNumberOfBoxesInStory());
        pallet.setNumberOfBoxesInPallet(palletDto.getNumberOfBoxesInPallet());

        palletRepository.save(pallet);

        return new ResponseEntity<>("Pallet updated successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Pallet>> getAllPalletsByPackaging(float packaging) {
        List<Pallet> pallets = palletRepository.findAllByPackaging(
                packaging
        );

        return ResponseEntity.ok().body(pallets);
    }

    /**     * Retrieves a pallet by ID.
     *
     * @param id the ID of the pallet to retrieve
     * @return the Pallet object with the specified ID
     */
    @Override
    public Pallet getPalletById(Integer id) {
        return palletRepository.findById(id).orElseThrow(null);
    }

    /**     * Deletes a pallet by ID.
     *
     * @param palletId the ID of the pallet to delete
     * @return ResponseEntity with a success message or an error message
     */
    @Override
    public ResponseEntity<Object> deletePallet(Integer palletId) {
        Pallet pallet = palletRepository.findById(palletId).orElseThrow(null);
        palletRepository.delete(pallet);
        return ResponseEntity.ok().body(pallet);
    }
}
