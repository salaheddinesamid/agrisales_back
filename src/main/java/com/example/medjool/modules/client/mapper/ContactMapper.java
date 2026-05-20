package com.example.medjool.modules.client.mapper;

import com.example.medjool.modules.client.dto.ContactDto;
import com.example.medjool.modules.client.model.Contact;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactMapper {

    public List<Contact> mapToContact(List<ContactDto> dto){
        return dto.stream().map(
                contactDto -> {
                    Contact contact = new Contact();
                    contact.setEmail(contactDto.getEmail());
                    contact.setPhone(contactDto.getPhone());
                    contact.setDepartment(contactDto.getDepartment());
                    return contact;
                }
        ).toList();
    }
}
