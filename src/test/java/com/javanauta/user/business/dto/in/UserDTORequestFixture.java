package com.javanauta.user.business.dto.in;

import java.util.List;

public class UserDTORequestFixture {
    
    public static UserDTORequest build (String name,
                                        String email,
                                        String password,
                                        List<AddressDTORequest> addresses,
                                        List<PhoneDTORequest> phones) {
        return new UserDTORequest(name, email, password, addresses, phones);
    }
}
