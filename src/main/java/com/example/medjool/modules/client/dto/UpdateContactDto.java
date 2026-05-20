package com.example.medjool.modules.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateContactDto {
    private Integer contactId;
    private String newDepartmentName;
    private String newPhoneNumber;
    private String newEmailAddress;
}
