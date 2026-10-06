package com.javanauta.user.business.dto.out;

public class PhoneDTOResponseFixture {
    public static PhoneDTOResponse build (Long id,
                                         String number,
                                         String areaCode) {

        return new PhoneDTOResponse(id, number, areaCode);
    }

}
