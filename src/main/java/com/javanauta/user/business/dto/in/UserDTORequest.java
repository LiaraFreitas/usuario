package com.javanauta.user.business.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTORequest {

    private String name;
    private String email;
    private String password;
    private List<AddressDTORequest> addresses;
    private List<PhoneDTORequest> phones;
}

