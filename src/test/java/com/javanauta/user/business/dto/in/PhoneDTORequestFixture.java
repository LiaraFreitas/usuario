package com.javanauta.user.business.dto.in;

public class PhoneDTORequestFixture {

    public static PhoneDTORequest build (String number,
                                         String areaCode) {

        return new PhoneDTORequest(number, areaCode);
    }

}
