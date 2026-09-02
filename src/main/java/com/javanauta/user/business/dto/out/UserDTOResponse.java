package com.javanauta.user.business.dto.out;

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
public class UserDTOResponse {

    private String name;
    private String email;
    private List<AddressDTOResponse> addresses;
    private List<PhoneDTOResponse> phones;
}

