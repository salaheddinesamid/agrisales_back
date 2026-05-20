package com.example.medjool.modules.client.service.implementation;

import com.example.medjool.modules.client.dto.AddressResponseDto;
import com.example.medjool.modules.client.dto.ClientResponseDto;
import com.example.medjool.modules.client.mapper.AddressMapper;
import com.example.medjool.modules.client.model.Address;
import com.example.medjool.modules.client.model.Client;
import com.example.medjool.modules.client.repository.ClientRepository;
import com.example.medjool.modules.client.service.ClientQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClientQueryServiceImpl implements ClientQueryService {

    private final ClientRepository clientRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public ClientQueryServiceImpl(ClientRepository clientRepository, AddressMapper addressMapper) {
        this.clientRepository = clientRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    public List<ClientResponseDto> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .map(ClientResponseDto::new).toList();
    }

    @Override
    public List<AddressResponseDto> getClientAddressesById(Integer id) {
        List<Address> addresses = clientRepository.findById(id).get().getAddresses();
        return addressMapper.mapToDto(addresses);
    }

    @Override
    public List<AddressResponseDto> getClientAddressesByName(String name) {
        List<Address> addresses = clientRepository.findByCompanyName(name).getAddresses();
        return addressMapper.mapToDto(addresses);
    }
}
