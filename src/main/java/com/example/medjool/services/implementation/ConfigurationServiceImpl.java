package com.example.medjool.services.implementation;

import com.example.medjool.dto.*;
import com.example.medjool.exception.ClientAlreadyFoundException;
import com.example.medjool.model.*;
import com.example.medjool.repository.*;
import com.example.medjool.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** * Implementation of the ConfigurationService interface for managing clients and pallets.
 */

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;
    private final PalletRepository palletRepository;
    private final ForexRepository forexRepository;

    @Autowired
    public ConfigurationServiceImpl(ClientRepository clientRepository, AddressRepository addressRepository, ContactRepository contactRepository, PalletRepository palletRepository, ForexRepository forexRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.palletRepository = palletRepository;
        this.forexRepository = forexRepository;
    }

    /**     * Adds a new client to the system.
     *
     * @param clientDto the DTO containing client details
     * @return ResponseEntity with the created client or an error message
     */
    @Override
    @Transactional
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


        List<Address> clientAddresses = mapAddresses(clientDto.getAddresses()); // Map addresses from DTO to entity
        addressRepository.saveAll(clientAddresses); // Save addresses to the repository

        List<Contact> clientContacts = mapContacts(clientDto.getContacts()); // Map contacts from DTO to entity
        contactRepository.saveAll(clientContacts); // Save contacts to the repository

        client.setAddresses(clientAddresses);
        client.setContacts(clientContacts);
        // Add client commission:
        client.setCommission(clientDto.getCommission());
        client.setSIRET(clientDto.getSiret());

        // Save the client
        clientRepository.save(client);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    private List<Address> mapAddresses(List<AddressDto> addresses){
        return addresses.stream().map(addressDto -> {
                    Address address = new Address();
                    address.setCity(addressDto.getCity());
                    address.setCountry(addressDto.getCountry());
                    address.setStreet(addressDto.getStreet());
                    address.setState(addressDto.getState());
                    address.setPostalCode(addressDto.getPostalCode());
                    return address;
                }).toList();

    }
    private List<Contact> mapContacts(List<ContactDto> contacts){
        return contacts.stream().map(
                contactDto -> {
                    Contact contact = new Contact();
                    contact.setEmail(contactDto.getEmail());
                    contact.setPhone(contactDto.getPhone());
                    contact.setDepartment(contactDto.getDepartment());
                    return contact;
                }
        ).toList();
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
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Update client fields
        client.setCompanyName(updateClientDto.getNewCompanyName());
        client.setGeneralManager(updateClientDto.getNewGeneralManager());
        client.setCompanyActivity(updateClientDto.getNewCompanyActivity());
        client.setSIRET(updateClientDto.getSiret());
        client.setWebSite(updateClientDto.getWebsite());
        client.setCommission(updateClientDto.getCommission());
        client.setClientStatus(ClientStatus.valueOf(updateClientDto.getClientStatus()));

        // --- Optimize Address Mapping ---
        Map<Long, Address> addressMap = addressRepository.findAll().stream()
                .collect(Collectors.toMap(Address::getAddressId, Function.identity()));


        // --- Optimize Contact Mapping ---
        Map<Integer, Contact> contactMap = contactRepository.findAll().stream()
                .collect(Collectors.toMap(Contact::getContactId, Function.identity()));
        // Update contacts:
        updateContacts(client,updateClientDto, contactMap);

        // Update addresses:
        updateAddresses(client, updateClientDto, addressMap);

        clientRepository.save(client);
        return ResponseEntity.ok("Client updated successfully");
    }

    @Transactional
    void updateContacts(Client client, UpdateClientDto updateClientDto, Map<Integer, Contact> contactMap) {
        List<Contact> updatedContacts = updateClientDto.getNewContacts().stream().map(dto -> {
            Contact contact = contactMap.get(dto.getContactId());
            if (contact == null) {
                contact = new Contact();
            }
            contact.setEmail(dto.getNewEmailAddress());
            contact.setPhone(dto.getNewPhoneNumber());
            contact.setDepartment(dto.getNewDepartmentName());
            return contactRepository.save(contact);
        }).collect(Collectors.toList());

        client.setContacts(updatedContacts);
    }

    @Transactional
    void updateAddresses(Client client, UpdateClientDto updateClientDto, Map<Long, Address> addressMap) {
        List<Address> updatedAddresses = updateClientDto.getNewAddresses().stream().map(dto -> {
            Address address = addressMap.get(dto.getAddressId());
            if (address == null) {
                address = new Address();
            }
            address.setCity(dto.getCity());
            address.setCountry(dto.getCountry());
            address.setStreet(dto.getStreet());
            address.setState(dto.getState());
            address.setPostalCode(dto.getZip());
            return addressRepository.save(address);
        }).collect(Collectors.toList());

        client.setAddresses(updatedAddresses);
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
        addPalletDimensions(newPallet, palletDto);

        // Costs:
        addPalletCosts(newPallet, palletDto);

        // Preparation hours:
        newPallet.setPreparationTime(palletDto.getPreparationTime());
        newPallet.setPackaging(palletDto.getPackaging());
        newPallet.setTag(palletDto.getTag());
        newPallet.setTotalNet(palletDto.getTotalNet());

        palletRepository.save(newPallet);
        return ResponseEntity.ok().body(newPallet);
    }
    /**     * Adds dimensions to a new pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing pallet dimensions
     */
    private void addPalletDimensions(Pallet pallet, PalletDto palletDto) {
        pallet.setHeight(palletDto.getHeight());
        pallet.setWidth(palletDto.getWidth());
        pallet.setLength(palletDto.getLength());

    }

    /**     * Adds costs to a new pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing pallet costs
     */
    private void addPalletCosts(Pallet pallet, PalletDto palletDto) {
        pallet.setProductionCost(palletDto.getProductionCost());
        pallet.setDatePurchase(palletDto.getDatePurchase());
        pallet.setLaborCost(palletDto.getLaborCost());
        pallet.setPackagingCost(palletDto.getPackagingCost());
        pallet.setFuelCost(palletDto.getFuelCost());
        pallet.setTransportationCost(palletDto.getTransportCost());
        pallet.setPackagingAT(palletDto.getPackagingAT());
        pallet.setLaborTransportCost(palletDto.getLaborTransportCost());
        pallet.setMarkUpCost(palletDto.getMarkupCost());
        pallet.setVat(palletDto.getVat());
        pallet.setPreliminaryLogisticsCost(palletDto.getPreliminaryLogistics());
        pallet.setInsuranceCost(palletDto.getInsuranceCost());
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
    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseEntity<Object> updatePallet(Integer id, UpdatePalletDto palletDto) {
        Pallet pallet = palletRepository.findByPalletId(id);
        // Update dimensions:
        updatePalletDimensions(pallet,palletDto);
        // Update costs:
        updatePalletCosts(pallet,palletDto);
        // Update basic information:
        updatePalletBasicInformation(pallet,palletDto);

        return new ResponseEntity<>("Pallet updated successfully", HttpStatus.OK);
    }

    /**     * Updates the dimensions of an existing pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing updated pallet dimensions
     */
    private void updatePalletDimensions(Pallet pallet, UpdatePalletDto palletDto) {
        pallet.setHeight(palletDto.getHeight());
        pallet.setWidth(palletDto.getWidth());
        pallet.setLength(palletDto.getLength());
    }

    /**     * Updates the costs of an existing pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing updated pallet costs
     */
    private void updatePalletCosts(Pallet pallet, UpdatePalletDto palletDto){
        pallet.setProductionCost(palletDto.getProductionCost());
        pallet.setLaborCost(palletDto.getLaborCost());
        pallet.setPackagingCost(palletDto.getPackagingCost());
        pallet.setTransportationCost(palletDto.getTransportCost());
        pallet.setMarkUpCost(palletDto.getMarkupCost());
        pallet.setVat(palletDto.getVat());
        pallet.setPreliminaryLogisticsCost(palletDto.getPreliminaryLogistics());
        pallet.setInsuranceCost(palletDto.getInsuranceCost());
        pallet.setFuelCost(palletDto.getFuelCost());
        pallet.setDatePurchase(palletDto.getDatePurchase());
        pallet.setLaborTransportCost(palletDto.getLaborTransportCost());
        pallet.setPackagingAT(palletDto.getPackagingAT());
    }
    /**     * Updates the basic information of an existing pallet.
     *
     * @param pallet the Pallet object to update
     * @param palletDto the DTO containing updated pallet basic information
     */
    private void updatePalletBasicInformation(Pallet pallet, UpdatePalletDto palletDto) {
        pallet.setNumberOfBoxesInCarton(palletDto.getNumberOfBoxesInCarton());
        pallet.setNumberOfCartonsInStory(palletDto.getNumberOfCartonsInStory());
        pallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());
        pallet.setNumberOfBoxesInStory(palletDto.getNumberOfBoxesInStory());
        pallet.setNumberOfBoxesInPallet(palletDto.getNumberOfBoxesInPallet());
        pallet.setPreparationTime(palletDto.getPreparationTime());
        pallet.setTag(palletDto.getTag());
        pallet.setTotalNet(palletDto.getTotalNet());
        pallet.setPackaging(palletDto.getPackaging());
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

    @Override
    public ResponseEntity<List<Forex>> getAllForex() {
        return ResponseEntity.ok(forexRepository.findAll());
    }

    /**     * Updates an existing Forex currency by ID.
     *
     * @param forexId the ID of the Forex currency to update
     * @param forexDto the DTO containing updated Forex details
     * @return ResponseEntity with a success message or an error message
     */
    @Override
    public ResponseEntity<Object> updateForex(Long forexId, UpdateForexDto forexDto) {
        try{
            Forex forex = forexRepository.findById(forexId).orElseThrow(null);
            forex.setBuyingRate(forexDto.getBuyingRate());
            forexRepository.save(forex);

            return new ResponseEntity<>("Forex updated successfully", HttpStatus.OK);
        }catch (RuntimeException ex){
            return new ResponseEntity<>("Forex not found", HttpStatus.NOT_FOUND);
        }
    }

    /**     * Adds a new Forex currency to the system.
     *
     * @param forexDto the DTO containing Forex currency details
     * @return ResponseEntity with a success message or an error message
     */
    @Override
    public ResponseEntity<Object> addForex(NewForexCurrencyDto forexDto) {
        try{
            boolean exists = forexRepository.existsByCurrency(ForexCurrency.valueOf(forexDto.getCurrencyName()));

            if(exists){
                return new ResponseEntity<>("Forex already exists", HttpStatus.CONFLICT);
            }
            else{
                Forex forex = new Forex();
                forex.setCurrency(ForexCurrency.valueOf(forexDto.getCurrencyName()));
                forex.setBuyingRate(forexDto.getBuyingRate());
                forexRepository.save(forex);
                return new ResponseEntity<>("Forex added successfully", HttpStatus.CREATED);
            }
        }catch (RuntimeException exception){
            throw new RuntimeException();
        }
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
