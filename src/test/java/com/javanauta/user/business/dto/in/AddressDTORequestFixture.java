package com.javanauta.user.business.dto.in;


public class AddressDTORequestFixture {

    public static AddressDTORequest build(String street,
                                          Long number,
                                          String complement,
                                          String city,
                                          String state,
                                          String cep) {
        return new AddressDTORequest(street, number, complement, city,state,cep
        );
    }
}
