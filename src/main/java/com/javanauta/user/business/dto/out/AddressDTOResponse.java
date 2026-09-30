package com.javanauta.user.business.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTOResponse {


    private Long id;
    private String street;
    private Long number;
    private String complement;
    private String city;
    private String state;
    private String cep;
}

