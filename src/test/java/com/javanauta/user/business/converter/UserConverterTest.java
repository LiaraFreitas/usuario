package com.javanauta.user.business.converter;
import com.javanauta.user.business.dto.in.AddressDTORequest;
import com.javanauta.user.business.dto.in.AddressDTORequestFixture;
import com.javanauta.user.business.dto.in.PhoneDTORequest;
import com.javanauta.user.business.dto.in.PhoneDTORequestFixture;
import com.javanauta.user.business.dto.in.UserDTORequest;
import com.javanauta.user.business.dto.in.UserDTORequestFixture;
import com.javanauta.user.business.dto.out.AddressDTOResponse;
import com.javanauta.user.business.dto.out.PhoneDTOResponse;
import com.javanauta.user.business.dto.out.UserDTOResponse;
import com.javanauta.user.infrastructure.entity.Address;
import com.javanauta.user.infrastructure.entity.Phone;
import com.javanauta.user.infrastructure.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
 class UserConverterTest {

    @InjectMocks
    private UserConverter userConverter;
    private User userEntity;
    private Address addressEntityOne;
    private Address addressEntityTwo;
    private Phone phoneEntityOne;
    private Phone phoneEntityTwo;
    private UserDTORequest userDTORequest;
    private UserDTORequest updateUserDTORequest;
    private UserDTORequest updateUserWithoutChangingAddressAndPhone;
    private UserDTORequest updateUserWithNulls;
    private AddressDTORequest addressDTORequestOne;
    private AddressDTORequest addressDTORequestTwo;
    private AddressDTORequest addressDTORequestThree;
    private AddressDTORequest updateAddresesDTORequest;
    private AddressDTORequest updateAddresesWithNulls;
    private PhoneDTORequest phoneDTORequestOne;
    private PhoneDTORequest phoneDTORequestTwo;
    private PhoneDTORequest phoneDTORequestThree;
    private PhoneDTORequest updatePhoneDTORequest;
    private PhoneDTORequest updatePhoneWithNulls;



    @BeforeEach
     void setup() {

    //================================ USER ENTITY =========================================

        addressEntityOne = Address.builder()
                .id(1L)
                .userId(2L)
                .street("Monte Belo")
                .number(430L)
                .city("Blumenau")
                .cep("89060-072")
                .state("SC")
                .complement("Casa")
                .build();

        phoneEntityOne = Phone.builder()
                .number("992120528")
                .areaCode("55")
                .build();

        addressEntityTwo = Address.builder()
                .id(2L)
                .userId(3L)
                .street("Rua XV de Novembro")
                .number(100L)
                .city("Blumenau")
                .cep("89060-010")
                .state("SC")
                .complement("Apartamento 101")
                .build();

        phoneEntityTwo = Phone.builder()
                .number("981141827")
                .areaCode("47")
                .build();


        userEntity = User.builder()
                .name("User")
                .email("user@gmail.com")
                .password("1234")
                .addresses(List.of(addressEntityOne, addressEntityTwo))
                .phones(List.of(phoneEntityOne, phoneEntityTwo))
                .build();

//====================== REQUEST ===============================

        addressDTORequestOne = AddressDTORequestFixture
                .build("Monte Belo",
                        430L,
                        "Casa",
                        "Blumenau",
                        "SC",
                        "89060-072");

        addressDTORequestTwo = AddressDTORequestFixture
                .build("Rua XV de Novembro",
                        100L,
                        "Apartamento 101",
                        "Blumenau",
                        "SC",
                        "89060-010");

        addressDTORequestThree = AddressDTORequestFixture
                .build("Rua 20 de Outubro",
                        301L,
                        "Apartamento 603",
                        "Ijuí",
                        "RS",
                        "98700-000");

        updateAddresesDTORequest = AddressDTORequestFixture
                .build("Rua Nova",
                        30L,
                        "Casa",
                        null,
                        null,
                        null);

        updateAddresesWithNulls = AddressDTORequestFixture
                .build(null,
                        null,
                        null,
                        null,
                        null,
                        null);


        phoneDTORequestOne = PhoneDTORequestFixture
                .build("992120528",
                        "55");

        phoneDTORequestTwo = PhoneDTORequestFixture
                .build("981141827",
                        "47");

        phoneDTORequestThree = PhoneDTORequestFixture
                .build("991191523",
                        "35");

        updatePhoneDTORequest = PhoneDTORequestFixture
                .build(null,
                        "46");

        updatePhoneWithNulls = PhoneDTORequestFixture
                .build(null,
                        null);

        userDTORequest = UserDTORequestFixture
                .build("User",
                        "user@gmail.com",
                        "1234",
                        List.of(addressDTORequestOne, addressDTORequestTwo),
                        List.of(phoneDTORequestOne, phoneDTORequestTwo));


        updateUserDTORequest = UserDTORequestFixture
                .build("User Teste",
                        "usertest@gmail.com",
                        "12345",
                        null,
                        null);


        updateUserWithoutChangingAddressAndPhone = UserDTORequestFixture
                .build("User Teste",
                        "usertest@gmail.com",
                        "12345",
                        List.of(addressDTORequestThree),
                        List.of(phoneDTORequestThree));

        updateUserWithNulls = UserDTORequestFixture
                .build(null,
                        null,
                        null,
                        null,
                        null);


    }


    @Test
    void testConvertToUserEntity() {

        User user = userConverter.toUserEntity(userDTORequest);

        assertEquals(userDTORequest.getName(), user.getName(), "User name must be converted correctly");
        assertEquals(userDTORequest.getEmail(), user.getEmail(), "User email must be converted correctly");
        assertEquals(userDTORequest.getPassword(), user.getPassword(), "User password must be converted correctly");
        assertEquals(2, user.getAddresses().size(), "Address list must contain 2 items");
        assertEquals(2, user.getPhones().size(), "Phone list must contain 2 items");
    }

    @Test
    void testConvertToAddressEntity() {

        Address address = userConverter.toAddressEntity(addressDTORequestOne);

        assertEquals(addressDTORequestOne.getStreet(), address.getStreet(), "Address street must be converted correctly");
        assertEquals(addressDTORequestOne.getNumber(), address.getNumber(), "Address number must be converted correctly");
        assertEquals(addressDTORequestOne.getCity(), address.getCity(), "Address city must be converted correctly");
        assertEquals(addressDTORequestOne.getState(), address.getState(), "Address state must be converted correctly");
        assertEquals(addressDTORequestOne.getCep(), address.getCep(), "Address ZIP code must be converted correctly");
        assertEquals(addressDTORequestOne.getComplement(), address.getComplement(), "Address complement must be converted correctly");
    }

    @Test
    void testConvertToAddressEntityList() {

        List<AddressDTORequest> requests = List.of(
                addressDTORequestOne,
                addressDTORequestTwo);

        List<Address> addresses = userConverter.toAddressEntityList(requests);
        assertEquals(2, addresses.size(), "Address list must contain 2 items");

        assertEquals(addressDTORequestOne.getStreet(), addresses.get(0).getStreet(), "The first address must keep the correct street");
        assertEquals(addressDTORequestOne.getNumber(), addresses.get(0).getNumber(), "The first address must keep the correct number");

        assertEquals(addressDTORequestTwo.getStreet(), addresses.get(1).getStreet(), "The second address must keep the correct street");
        assertEquals(addressDTORequestTwo.getNumber(), addresses.get(1).getNumber(), "The second address must keep the correct number");
    }

    @Test
    void testConvertToPhoneEntity() {

        Phone phone = userConverter.toPhoneEntity(phoneDTORequestOne);

        assertEquals(phoneDTORequestOne.getNumber(), phone.getNumber(), "Phone number must be converted correctly");
        assertEquals(phoneDTORequestOne.getAreaCode(), phone.getAreaCode(), "Phone area code must be converted correctly");
    }

    @Test
    void testConvertToPhoneEntityList() {

        List<PhoneDTORequest> requests = List.of(
                phoneDTORequestOne,
                phoneDTORequestTwo);

        List<Phone> phones = userConverter.toPhoneEntityList(requests);
        assertEquals(2, phones.size(), "Phone list must contain 2 items");

        assertEquals(phoneDTORequestOne.getNumber(), phones.get(0).getNumber(), "The first phone must keep the correct number");
        assertEquals(phoneDTORequestOne.getAreaCode(), phones.get(0).getAreaCode(), "The first phone must keep the correct area code");

        assertEquals(phoneDTORequestTwo.getNumber(), phones.get(1).getNumber(), "The second phone must keep the correct number");
        assertEquals(phoneDTORequestTwo.getAreaCode(), phones.get(1).getAreaCode(), "The second phone must keep the correct area code");
    }

    // ======================== TEST: toUserResponse ========================

    @Test
    void testConvertToUserResponse() {

        UserDTOResponse response = userConverter.toUserResponse(userEntity);

        assertEquals(userEntity.getName(), response.getName(), "Response name must match the user");
        assertEquals(userEntity.getEmail(), response.getEmail(), "Response email must match the user");
        assertEquals(userEntity.getPassword(), response.getPassword(), "Response password must match the user");

        assertEquals(2, response.getAddresses().size(), "Response address list must contain 2 items");
        assertEquals(2, response.getPhones().size(), "Response phone list must contain 2 items");
    }

    @Test
    void testConvertToUserResponseWithNullLists() {

        userEntity.setAddresses(null);
        userEntity.setPhones(null);

        UserDTOResponse response = userConverter.toUserResponse(userEntity);

        assertNull(response.getAddresses(), "Address list must remain null");
        assertNull(response.getPhones(), "Phone list must remain null");
    }

    @Test
    void testConvertToAddressResponse() {

        AddressDTOResponse response = userConverter.toAddressResponse(addressEntityOne);

        assertEquals(addressEntityOne.getStreet(), response.getStreet(), "Response street must match the entity");
        assertEquals(addressEntityOne.getNumber(), response.getNumber(), "Response number must match the entity");
        assertEquals(addressEntityOne.getCity(), response.getCity(), "Response city must match the entity");
        assertEquals(addressEntityOne.getState(), response.getState(), "Response state must match the entity");
        assertEquals(addressEntityOne.getCep(), response.getCep(), "Response ZIP code must match the entity");
        assertEquals(addressEntityOne.getComplement(), response.getComplement(), "Response complement must match the entity");
    }

    @Test
    void testConvertToAddressesResponseList() {

        List<Address> responses = List.of(
                addressEntityOne,
                addressEntityTwo);

        List<AddressDTOResponse> addressesResponses = userConverter.toAddressResponseList(responses);
        assertEquals(2, addressesResponses.size(), "Address response list must contain 2 items");

        assertEquals(addressEntityOne.getStreet(), addressesResponses.get(0).getStreet(), "The first address response must keep the correct street");
        assertEquals(addressEntityOne.getNumber(), addressesResponses.get(0).getNumber(), "The first address response must keep the correct number");

        assertEquals(addressEntityTwo.getStreet(), addressesResponses.get(1).getStreet(), "The second address response must keep the correct street");
        assertEquals(addressEntityTwo.getNumber(), addressesResponses.get(1).getNumber(), "The second address response must keep the correct number");
    }

    @Test
    void testConvertToPhoneResponse() {

        PhoneDTOResponse response = userConverter.toPhoneResponse(phoneEntityOne);

        assertEquals(phoneEntityOne.getNumber(), response.getNumber(), "Response phone number must match the entity");
        assertEquals(phoneEntityOne.getAreaCode(), response.getAreaCode(), "Response phone area code must match the entity");

    }

    @Test
    void testConvertToPhoneResponseList() {

        List<Phone> responses = List.of(
                phoneEntityOne,
                phoneEntityTwo);

        List<PhoneDTOResponse> phonesResponses = userConverter.toPhoneResponseList(responses);
        assertEquals(2, phonesResponses.size(), "Phone response list must contain 2 items");

        assertEquals(phoneEntityOne.getNumber(), phonesResponses.get(0).getNumber(), "The first phone response must keep the correct number");
        assertEquals(phoneEntityOne.getAreaCode(), phonesResponses.get(0).getAreaCode(), "The first phone response must keep the correct area code");

        assertEquals(phoneEntityTwo.getNumber(), phonesResponses.get(1).getNumber(), "The second phone response must keep the correct number");
        assertEquals(phoneEntityTwo.getAreaCode(), phonesResponses.get(1).getAreaCode(), "The second phone response must keep the correct area code");
    }

    @Test
    void testUpdateUser() {

        User updateUser = userConverter.updateUser(updateUserDTORequest, userEntity);

        assertEquals(updateUserDTORequest.getName(), updateUser.getName(), "Updated name must be kept correctly");
        assertEquals(updateUserDTORequest.getEmail(), updateUser.getEmail(), "Updated email must be kept correctly");
        assertEquals(updateUserDTORequest.getPassword(), updateUser.getPassword(), "Updated password must be kept correctly");
    }

    @Test
    void testUpdateUserKeepsEntityAddressesAndPhones() {

        User updateUser = userConverter.updateUser(updateUserWithoutChangingAddressAndPhone, userEntity);

        assertEquals(userEntity.getAddresses(), updateUser.getAddresses(), "Old addresses must be preserved when not changed");
        assertEquals(userEntity.getPhones(), updateUser.getPhones(), "Old phones must be preserved when not changed");
    }

    @Test
    void testUpdateUserKeepsOldValuesWhenRequestFieldsAreNull() {

        User updateUser = userConverter.updateUser(updateUserWithNulls, userEntity);

        assertEquals(userEntity.getName(), updateUser.getName(), "Old name must be kept when the request is null");
        assertEquals(userEntity.getEmail(), updateUser.getEmail(), "Old email must be kept when the request is null");
        assertEquals(userEntity.getPassword(), updateUser.getPassword(), "Old password must be kept when the request is null");


    }

    @Test
    void testUpdateAddress() {

        Address updateAddress = userConverter.updateAddress(addressDTORequestThree, addressEntityOne);

        assertEquals(addressEntityOne.getId(), updateAddress.getId(), "Address ID must be preserved");
        assertEquals(addressEntityOne.getUserId(), updateAddress.getUserId(), "Address user ID must be preserved");

        assertEquals(addressDTORequestThree.getStreet(), updateAddress.getStreet(), "Address street must be updated correctly");
        assertEquals(addressDTORequestThree.getNumber(), updateAddress.getNumber(), "Address number must be updated correctly");
        assertEquals(addressDTORequestThree.getComplement(), updateAddress.getComplement(), "Address complement must be updated correctly");
        assertEquals(addressDTORequestThree.getCity(), updateAddress.getCity(), "Address city must be updated correctly");
        assertEquals(addressDTORequestThree.getState(), updateAddress.getState(), "Address state must be updated correctly");
        assertEquals(addressDTORequestThree.getCep(), updateAddress.getCep(), "Address ZIP code must be updated correctly");
    }

    @Test
    void testUpdateAddresesPartially(){

        Address updateAddress = userConverter.updateAddress(updateAddresesDTORequest,addressEntityOne);

        assertEquals(addressEntityOne.getId(), updateAddress.getId(), "Address ID must be preserved during partial update");
        assertEquals(addressEntityOne.getUserId(), updateAddress.getUserId(), "Address user ID must be preserved during partial update");

        assertEquals(updateAddresesDTORequest.getStreet(), updateAddress.getStreet(), "Address street must be updated partially");
        assertEquals(updateAddresesDTORequest.getNumber(), updateAddress.getNumber(), "Address number must be updated partially");
        assertEquals(updateAddresesDTORequest.getComplement(), updateAddress.getComplement(), "Address complement must be updated partially");

        assertEquals(addressEntityOne.getCity(), updateAddress.getCity(), "Address city must keep the previous value when not provided");
        assertEquals(addressEntityOne.getState(), updateAddress.getState(), "Address state must keep the previous value when not provided");
        assertEquals(addressEntityOne.getCep(), updateAddress.getCep(), "Address ZIP code must keep the previous value when not provided");

    }

    @Test
    void testUpdateAddressKeepsOldValuesWhenRequestFieldsAreNull() {

        Address updateAddress = userConverter.updateAddress(updateAddresesWithNulls, addressEntityTwo);

        assertEquals(addressEntityTwo.getId(), updateAddress.getId(), "Address ID must be preserved when the request is null");
        assertEquals(addressEntityTwo.getUserId(), updateAddress.getUserId(), "Address user ID must be preserved when the request is null");

        assertEquals(addressEntityTwo.getStreet(), updateAddress.getStreet(), "Address street must remain unchanged when the request is null");
        assertEquals(addressEntityTwo.getNumber(), updateAddress.getNumber(), "Address number must remain unchanged when the request is null");
        assertEquals(addressEntityTwo.getComplement(), updateAddress.getComplement(), "Address complement must remain unchanged when the request is null");
        assertEquals(addressEntityTwo.getCity(), updateAddress.getCity(), "Address city must remain unchanged when the request is null");
        assertEquals(addressEntityTwo.getState(), updateAddress.getState(), "Address state must remain unchanged when the request is null");
        assertEquals(addressEntityTwo.getCep(), updateAddress.getCep(), "Address ZIP code must remain unchanged when the request is null");


    }


    @Test
    void testUpdatePhone() {

        Phone updatePhone = userConverter.updatePhone(phoneDTORequestTwo, phoneEntityTwo);

        assertEquals(phoneEntityTwo.getId(), updatePhone.getId(), "Phone ID must be preserved");
        assertEquals(phoneEntityTwo.getUserId(), updatePhone.getUserId(), "Phone user ID must be preserved");

        assertEquals(phoneDTORequestTwo.getAreaCode(), updatePhone.getAreaCode(), "Phone areacode must be updated correctly");
        assertEquals(phoneDTORequestTwo.getNumber(), updatePhone.getNumber(), "Phone number must be updated correctly");


    }

    @Test
    void testUpdatePhonePartially() {

        Phone updatePhone = userConverter.updatePhone(updatePhoneDTORequest, phoneEntityOne);

        assertEquals(phoneEntityOne.getId(), updatePhone.getId(), "Phone ID must be preserved");
        assertEquals(phoneEntityOne.getUserId(), updatePhone.getUserId(), "Phone user ID must be preserved");

        assertEquals(updatePhoneDTORequest.getAreaCode(), updatePhone.getAreaCode(), "Phone areacode must be updated correctly");

        assertEquals(phoneEntityOne.getNumber(), updatePhone.getNumber(),"Phone number must keep the previous value when not provided");

    }

    @Test
    void testUpdatePhoneKeepsOldValuesWhenRequestFieldsAreNull(){

        Phone updatePhone = userConverter.updatePhone(updatePhoneWithNulls, phoneEntityOne);

        assertEquals(phoneEntityOne.getId(), updatePhone.getId(), "Phone ID must be preserved");
        assertEquals(phoneEntityOne.getUserId(), updatePhone.getUserId(), "Phone user ID must be preserved");
        assertEquals(phoneEntityOne.getAreaCode(), updatePhone.getAreaCode(), "Phone areacode must remain unchanged when the request is nul");
        assertEquals(phoneEntityOne.getNumber(), updatePhone.getNumber(), "Phone number must remain unchanged when the request is nul");

    }


}



