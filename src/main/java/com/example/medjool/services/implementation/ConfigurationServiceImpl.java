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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    // Add new client:
    @Override
    public ResponseEntity<Object> addClient(ClientDto clientDto) {

        if(clientRepository.findByCompanyName(clientDto.getCompanyName())!=null){
            throw new ClientAlreadyFoundException();
        }

        Client client = new Client();
        // Set Client details
        client.setCompanyName(clientDto.getCompanyName());

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


        // Save the client
        clientRepository.save(client);

        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<List<Client>> getAll(){
        List<Client> clients = clientRepository.findAll();
        return new ResponseEntity<>(clients,HttpStatus.OK);
    }


    // Update client information:
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

        clientRepository.save(client); // <- important
        return new ResponseEntity<>("Client updated successfully", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Object> deleteClient(Integer id) throws ClassNotFoundException {
        Optional<Client> client = clientRepository.findById(id);


        if (client.isEmpty()) {
            throw new ClassNotFoundException();
        }
        clientRepository.delete(client.get());
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    private List<AddressResponseDto> convertToAddressDto(List<Address> addresses) {
        return
                addresses.stream().map(address -> {
                    AddressResponseDto addressResponseDto = new AddressResponseDto();
                    addressResponseDto.setCity(address.getCity());
                    addressResponseDto.setCountry(address.getCountry());
                    addressResponseDto.setStreet(address.getStreet());
                    addressResponseDto.setState(address.getState());
                    return addressResponseDto;
                }).toList();
    }


    @Override
    public ResponseEntity<List<AddressResponseDto>> getClientAddresses(Integer id){
        List<Address> addresses = clientRepository.findById(id).get().getAddresses();

        List<AddressResponseDto> addressResponseDtos = convertToAddressDto(addresses);

        return new ResponseEntity<>(addressResponseDtos,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AddressResponseDto>> getClientAddressesByClientName(String clientName) {
        List<Address> addresses = clientRepository.findByCompanyName(clientName).getAddresses();

        List<AddressResponseDto> addressResponseDtos =
                convertToAddressDto(addresses);
        return new ResponseEntity<>(addressResponseDtos,HttpStatus.OK);
    }


    // -------------------------- Pallet Configuration Service ---------------------------
    @Override
    public ResponseEntity<Object> addPallet(PalletDto palletDto) {
        Pallet pallet = palletRepository.findByPackagingAndDimensions(palletDto.getPackaging(),palletDto.getDimensions());

        if(pallet != null){
            return new ResponseEntity<>("Pallet already exists", HttpStatus.CONFLICT);
        }
        Pallet newPallet = new Pallet();
        newPallet.setNumberOfStoriesInPallet(palletDto.getNumberOfStoriesInPallet());
        newPallet.setNumberOfBoxesInCarton(palletDto.getNumberOfBoxesInCarton());

        newPallet.setNumberOfCartonsInStory(palletDto.getNumberOfCartonsInStory());
        // Dimensions:
        newPallet.setDimensions(palletDto.getDimensions());
        // Preparation hours:
        newPallet.setPreparationTime(palletDto.getPreparationTime());
        newPallet.setPackaging(palletDto.getPackaging());
        newPallet.setTag(palletDto.getTag());
        newPallet.setTotalNet(palletDto.getTotalNet());

        palletRepository.save(newPallet);
        return ResponseEntity.ok().body(newPallet);
    }



    @Override
    public ResponseEntity<List<Pallet>> getAllPallets(){
        List<Pallet> pallets = palletRepository.findAll();
        return ResponseEntity.ok().body(pallets);
    }

    @Override
    public ResponseEntity<List<Pallet>> getAllPalletsByPackaging(float packaging) {
        List<Pallet> pallets = palletRepository.findAllByPackaging(
                packaging
        );

        return ResponseEntity.ok().body(pallets);
    }


    @Override
    public ResponseEntity<Object> deletePallet(Integer palletId) {
        Pallet pallet = palletRepository.findById(palletId).orElseThrow(null);
        palletRepository.delete(pallet);
        return ResponseEntity.ok().body(pallet);
    }
}
