package com.javanauta.user.business.dto.out;

public class AddressDTOResponseFixture {
    public static AddressDTOResponse build (Long id,
                                           String street,
                                           Long number,
                                           String complement,
                                           String city,
                                           String state,
                                           String cep) {
        return new AddressDTOResponse(id, street, number, complement, city,state,cep
        );
    }
}
