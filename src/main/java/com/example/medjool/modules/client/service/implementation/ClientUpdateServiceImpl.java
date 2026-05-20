package com.example.medjool.modules.client.service.implementation;

import com.example.medjool.modules.client.dto.UpdateClientDto;
import com.example.medjool.modules.client.model.Address;
import com.example.medjool.modules.client.model.Client;
import com.example.medjool.modules.client.model.ClientStatus;
import com.example.medjool.modules.client.model.Contact;
import com.example.medjool.modules.client.repository.AddressRepository;
import com.example.medjool.modules.client.repository.ClientRepository;
import com.example.medjool.modules.client.repository.ContactRepository;
import com.example.medjool.modules.client.service.ClientUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ClientUpdateServiceImpl implements ClientUpdateService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public ClientUpdateServiceImpl(ClientRepository clientRepository, AddressRepository addressRepository, ContactRepository contactRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
    }

    @Transactional
    public Client updateClient(Integer clientId, UpdateClientDto updateClientDto) {
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

        return clientRepository.save(client);
    }

    @Override
    public void removeClient(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client with ID " + id + " not found"));

        clientRepository.delete(client);
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
}
