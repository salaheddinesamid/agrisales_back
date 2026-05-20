package com.example.medjool.modules.client.service.implementation;

import com.example.medjool.exception.ClientAlreadyFoundException;
import com.example.medjool.modules.client.dto.ClientDto;
import com.example.medjool.modules.client.mapper.AddressMapper;
import com.example.medjool.modules.client.mapper.ContactMapper;
import com.example.medjool.modules.client.model.Address;
import com.example.medjool.modules.client.model.Client;
import com.example.medjool.modules.client.model.ClientStatus;
import com.example.medjool.modules.client.model.Contact;
import com.example.medjool.modules.client.repository.AddressRepository;
import com.example.medjool.modules.client.repository.ClientRepository;
import com.example.medjool.modules.client.repository.ContactRepository;
import com.example.medjool.modules.client.service.ClientAdderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClientAdderServiceImpl implements ClientAdderService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ContactRepository contactRepository;
    private final AddressMapper addressMapper;
    private final ContactMapper contactMapper;
    @Autowired
    public ClientAdderServiceImpl(ClientRepository clientRepository, AddressRepository addressRepository, ContactRepository contactRepository, AddressMapper addressMapper, ContactMapper contactMapper) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.addressMapper = addressMapper;
        this.contactMapper = contactMapper;
    }

    @Override
    public Client addClient(ClientDto clientDto) {

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


        List<Address> clientAddresses = addressMapper.mapToAddress(clientDto.getAddresses()); // Map addresses from DTO to entity
        addressRepository.saveAll(clientAddresses); // Save addresses to the repository

        List<Contact> clientContacts = contactMapper.mapToContact(clientDto.getContacts()); // Map contacts from DTO to entity
        contactRepository.saveAll(clientContacts); // Save contacts to the repository

        client.setAddresses(clientAddresses);
        client.setContacts(clientContacts);
        // Add client commission:
        client.setCommission(clientDto.getCommission());
        client.setSIRET(clientDto.getSiret());

        // Save the client
        return clientRepository.save(client);
    }
}
