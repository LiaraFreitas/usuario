package com.javanauta.user.business.dto.out;

import java.util.List;

public class UserDTOResponseFixture {

    public static UserDTOResponse build (Long id,
                                        String name,
                                        String email,
                                        String password,
                                        List<AddressDTOResponse> addresses,
                                        List<PhoneDTOResponse> phones) {
        return new UserDTOResponse(id, name, email, password, addresses, phones);
    }
}
