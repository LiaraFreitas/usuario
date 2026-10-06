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
public class UserConverterTest {

    @InjectMocks
    private UserConverter userConverter;
    private User user;
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

    @BeforeEach
    public void setup() {
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


        user = User.builder()
                .name("User")
                .email("user@gmail.com")
                .password("1234")
                .addresses(List.of(addressEntityOne, addressEntityTwo))
                .phones(List.of(phoneEntityOne, phoneEntityTwo))
                .build();


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

    // ======================== TEST: toUserEntity ========================

    @Test
    void testConvertToUserEntity() {

        User userEntity = userConverter.toUserEntity(userDTORequest);

        assertEquals(user.getName(), userEntity.getName());
        assertEquals(user.getEmail(), userEntity.getEmail());
        assertEquals(user.getPassword(), userEntity.getPassword());
        assertEquals(2, userEntity.getAddresses().size());
        assertEquals(2, userEntity.getPhones().size());
    }

    @Test
    void testConvertToAddressEntity() {

        Address address = userConverter.toAddressEntity(addressDTORequestOne);

        assertEquals(addressDTORequestOne.getStreet(), address.getStreet());
        assertEquals(addressDTORequestOne.getNumber(), address.getNumber());
        assertEquals(addressDTORequestOne.getCity(), address.getCity());
        assertEquals(addressDTORequestOne.getState(), address.getState());
        assertEquals(addressDTORequestOne.getCep(), address.getCep());
        assertEquals(addressDTORequestOne.getComplement(), address.getComplement());
    }

    @Test
    void testConvertToAddressEntityList() {

        List<AddressDTORequest> requests = List.of(
                addressDTORequestOne,
                addressDTORequestTwo);

        List<Address> addresses = userConverter.toAddressEntityList(requests);
        assertEquals(2, addresses.size());

        assertEquals(addressDTORequestOne.getStreet(), addresses.get(0).getStreet());
        assertEquals(addressDTORequestOne.getNumber(), addresses.get(0).getNumber());

        assertEquals(addressDTORequestTwo.getStreet(), addresses.get(1).getStreet());
        assertEquals(addressDTORequestTwo.getNumber(), addresses.get(1).getNumber());
    }

    @Test
    void testConvertToPhoneEntity() {

        Phone phone = userConverter.toPhoneEntity(phoneDTORequestOne);

        assertEquals(phoneDTORequestOne.getNumber(), phone.getNumber());
        assertEquals(phoneDTORequestOne.getAreaCode(), phone.getAreaCode());
    }

    @Test
    void testConvertToPhoneEntityList() {

        List<PhoneDTORequest> requests = List.of(
                phoneDTORequestOne,
                phoneDTORequestTwo);

        List<Phone> phones = userConverter.toPhoneEntityList(requests);
        assertEquals(2, phones.size());

        assertEquals(phoneDTORequestOne.getNumber(), phones.get(0).getNumber());
        assertEquals(phoneDTORequestOne.getAreaCode(), phones.get(0).getAreaCode());

        assertEquals(phoneDTORequestTwo.getNumber(), phones.get(1).getNumber());
        assertEquals(phoneDTORequestTwo.getAreaCode(), phones.get(1).getAreaCode());
    }

    // ======================== TEST: toUserResponse ========================

    @Test
    void testConvertToUserResponse() {

        UserDTOResponse response = userConverter.toUserResponse(user);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getPassword(), response.getPassword());

        assertEquals(2, response.getAddresses().size());
        assertEquals(2, response.getPhones().size());
    }

    @Test
    void testConvertToUserResponseWithNullLists() {

        user.setAddresses(null);
        user.setPhones(null);

        UserDTOResponse response = userConverter.toUserResponse(user);

        assertNull(response.getAddresses());
        assertNull(response.getPhones());
    }

    @Test
    void testConvertToAddressResponse() {

        AddressDTOResponse response = userConverter.toAddressResponse(addressEntityOne);

        assertEquals(addressEntityOne.getStreet(), response.getStreet());
        assertEquals(addressEntityOne.getNumber(), response.getNumber());
        assertEquals(addressEntityOne.getCity(), response.getCity());
        assertEquals(addressEntityOne.getState(), response.getState());
        assertEquals(addressEntityOne.getCep(), response.getCep());
        assertEquals(addressEntityOne.getComplement(), response.getComplement());
    }

    @Test
    void testConvertToAddressesResponseList() {

        List<Address> responses = List.of(
                addressEntityOne,
                addressEntityTwo);

        List<AddressDTOResponse> addressesResponses = userConverter.toAddressResponseList(responses);
        assertEquals(2, addressesResponses.size());

        assertEquals(addressEntityOne.getStreet(), addressesResponses.get(0).getStreet());
        assertEquals(addressEntityOne.getNumber(), addressesResponses.get(0).getNumber());

        assertEquals(addressEntityTwo.getStreet(), addressesResponses.get(1).getStreet());
        assertEquals(addressEntityTwo.getNumber(), addressesResponses.get(1).getNumber());
    }

    @Test
    void testConvertToPhoneResponse() {

        PhoneDTOResponse response = userConverter.toPhoneResponse(phoneEntityOne);

        assertEquals(phoneEntityOne.getNumber(), response.getNumber());
        assertEquals(phoneEntityOne.getAreaCode(), response.getAreaCode());

    }

    @Test
    void testConvertToPhoneResponseList() {

        List<Phone> responses = List.of(
                phoneEntityOne,
                phoneEntityTwo);

        List<PhoneDTOResponse> phonesResponses = userConverter.toPhoneResponseList(responses);
        assertEquals(2, phonesResponses.size());

        assertEquals(phoneEntityOne.getNumber(), phonesResponses.get(0).getNumber());
        assertEquals(phoneEntityOne.getAreaCode(), phonesResponses.get(0).getAreaCode());

        assertEquals(phoneEntityTwo.getNumber(), phonesResponses.get(1).getNumber());
        assertEquals(phoneEntityTwo.getAreaCode(), phonesResponses.get(1).getAreaCode());
    }

    @Test
    void testUpdateUser() {

        User updateUser = userConverter.updateUser(updateUserDTORequest, user);

        assertEquals(updateUserDTORequest.getName(), updateUser.getName());
        assertEquals(updateUserDTORequest.getEmail(), updateUser.getEmail());
        assertEquals(updateUserDTORequest.getPassword(), updateUser.getPassword());
    }

    @Test
    void testUpdateUserKeepsEntityAddressesAndPhones() {

        User updateUser = userConverter.updateUser(updateUserWithoutChangingAddressAndPhone, user);

        assertEquals(user.getAddresses(), updateUser.getAddresses());
        assertEquals(user.getPhones(), updateUser.getPhones());
    }

    @Test
    void testUpdateUserKeepsOldValuesWhenRequestFieldsAreNull() {

        User updateUser = userConverter.updateUser(updateUserWithNulls, user);

        assertEquals(user.getName(), updateUser.getName());
        assertEquals(user.getEmail(), updateUser.getEmail());
        assertEquals(user.getPassword(), updateUser.getPassword());


    }

    @Test
    void testUpdateAddress() {

        Address updateAddress = userConverter.updateAddress(addressDTORequestThree, addressEntityOne);

        assertEquals(addressEntityOne.getId(), updateAddress.getId());
        assertEquals(addressEntityOne.getUserId(), updateAddress.getUserId());

        assertEquals(addressDTORequestThree.getStreet(), updateAddress.getStreet());
        assertEquals(addressDTORequestThree.getNumber(), updateAddress.getNumber());
        assertEquals(addressDTORequestThree.getComplement(), updateAddress.getComplement());
        assertEquals(addressDTORequestThree.getCity(), updateAddress.getCity());
        assertEquals(addressDTORequestThree.getState(), updateAddress.getState());
        assertEquals(addressDTORequestThree.getCep(), updateAddress.getCep());
    }

    @Test
    void testUpdateAddresesPartially(){

        Address updateAddress = userConverter.updateAddress(updateAddresesDTORequest,addressEntityOne);

        assertEquals(addressEntityOne.getId(), updateAddress.getId());
        assertEquals(addressEntityOne.getUserId(), updateAddress.getUserId());

        assertEquals(updateAddresesDTORequest.getStreet(), updateAddress.getStreet());
        assertEquals(updateAddresesDTORequest.getNumber(), updateAddress.getNumber());
        assertEquals(updateAddresesDTORequest.getComplement(), updateAddress.getComplement());

        assertEquals(addressEntityOne.getCity(), updateAddress.getCity());
        assertEquals(addressEntityOne.getState(), updateAddress.getState());
        assertEquals(addressEntityOne.getCep(), updateAddress.getCep());

    }

    @Test
    void testUpdateAddressKeepsOldValuesWhenRequestFieldsAreNull() {

        Address updateAddress = userConverter.updateAddress(updateAddresesWithNulls, addressEntityTwo);

        assertEquals(addressEntityTwo.getId(), updateAddress.getId());
        assertEquals(addressEntityTwo.getUserId(), updateAddress.getUserId());

        assertEquals(addressEntityTwo.getStreet(), updateAddress.getStreet());
        assertEquals(addressEntityTwo.getNumber(), updateAddress.getNumber());
        assertEquals(addressEntityTwo.getComplement(), updateAddress.getComplement());
        assertEquals(addressEntityTwo.getCity(), updateAddress.getCity());
        assertEquals(addressEntityTwo.getState(), updateAddress.getState());
        assertEquals(addressEntityTwo.getCep(), updateAddress.getCep());


    }


}





//
//
//    @Test
//    void testToUserEntitySuccess() {
//        // Act
//        User result = userConverter.toUserEntity(userDTORequest);
//
//        // Assert
//        assertAll("Validar conversão de UserDTORequest para User",
//                () -> assertNotNull(result, "User não deve ser nulo"),
//                () -> assertEquals(userDTORequest.getName(), result.getName(), "Nome deve ser igual"),
//                () -> assertEquals(userDTORequest.getEmail(), result.getEmail(), "Email deve ser igual"),
//                () -> assertEquals(userDTORequest.getPassword(), result.getPassword(), "Senha deve ser igual"),
//                () -> assertNull(result.getId(), "ID deve ser nulo (novo usuário)"),
//                () -> assertNotNull(result.getAddresses(), "Addresses não deve ser nula"),
//                () -> assertNotNull(result.getPhones(), "Phones não deve ser nula")
//        );
//    }
//
//    @Test
//    void testToUserEntityWithoutAddresses() {
//        // Arrange
//        userDTORequest.setAddresses(null);
//
//        // Act
//        User result = userConverter.toUserEntity(userDTORequest);
//
//        // Assert
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertNull(result.getAddresses(), "Addresses deve ser nula")
//        );
//    }
//
//    @Test
//    void testToUserEntityWithoutPhones() {
//        // Arrange
//        userDTORequest.setPhones(null);
//
//        // Act
//        User result = userConverter.toUserEntity(userDTORequest);
//
//        // Assert
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertNull(result.getPhones(), "Phones deve ser nula")
//        );
//    }
//
//    // ======================== TESTES: toAddressEntityByUserID ========================
//    @Test
//    void testToAddressEntitySuccess() {
//        // Act
//        Address result = userConverter.toAddressEntityByUserID(addressDTORequestOne);
//
//        // Assert
//        assertAll("Validar conversão de AddressDTORequest para Address",
//                () -> assertNotNull(result),
//                () -> assertEquals(addressDTORequestOne.getStreet(), result.getStreet()),
//                () -> assertEquals(addressDTORequestOne.getNumber(), result.getNumber()),
//                () -> assertEquals(addressDTORequestOne.getCity(), result.getCity()),
//                () -> assertEquals(addressDTORequestOne.getCep(), result.getCep()),
//                () -> assertEquals(addressDTORequestOne.getState(), result.getState()),
//                () -> assertEquals(addressDTORequestOne.getComplement(), result.getComplement()),
//                () -> assertNull(result.getUserId(), "UserID deve ser nulo")
//        );
//    }
//
//    @Test
//    void testToAddressEntityWithUserId() {
//        // Arrange
//        Long userId = 1L;
//
//        // Act
//        Address result = userConverter.toAddressEntityByUserID(addressDTORequestOne, userId);
//
//        // Assert
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(userId, result.getUserId(), "UserId deve ser igual"),
//                () -> assertEquals(addressDTORequestOne.getStreet(), result.getStreet())
//        );
//    }
//
//    // ======================== TESTES: toAddressEntityList ========================
//    @Test
//    void testToAddressEntityListSuccess() {
//        // Arrange - Criando múltiplos endereços
//        AddressDTORequest addressEntityTwo = AddressDTORequestFixture
//                .build(4L, "Rua Principal", 100L, "Apto 101", "São Paulo", "SP", "01310-100");
//        List<AddressDTORequest> addressList = List.of(addressDTORequestOne, addressEntityTwo);
//
//        // Act
//        List<Address> result = userConverter.toAddressEntityList(addressList);
//
//        // Assert
//        assertAll("Validar conversão de lista de AddressDTORequest",
//                () -> assertNotNull(result, "Lista não deve ser nula"),
//                () -> assertEquals(addressList.size(), result.size(), "Tamanho da lista deve ser igual"),
//                () -> assertEquals(addressDTORequestOne.getStreet(), result.get(0).getStreet()),
//                () -> assertEquals(addressEntityTwo.getStreet(), result.get(1).getStreet())
//        );
//    }
//
//    @Test
//    void testToAddressEntityListEmpty() {
//        // Act
//        List<Address> result = userConverter.toAddressEntityList(List.of());
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty(), "Lista deve estar vazia");
//    }
//
//    // ======================== TESTES: toPhoneEntityByUserID ========================
//    @Test
//    void testToPhoneEntitySuccess() {
//        // Act
//        Phone result = userConverter.toPhoneEntityByUserID(phoneDTORequestOne);
//
//        // Assert
//        assertAll("Validar conversão de PhoneDTORequest para Phone",
//                () -> assertNotNull(result),
//                () -> assertEquals(phoneDTORequestOne.getNumber(), result.getNumber()),
//                () -> assertEquals(phoneDTORequestOne.getAreaCode(), result.getAreaCode()),
//                () -> assertNull(result.getUserId(), "UserID deve ser nulo")
//        );
//    }
//
//    @Test
//    void testToPhoneEntityWithUserId() {
//        // Arrange
//        Long userId = 1L;
//
//        // Act
//        Phone result = userConverter.toPhoneEntityByUserID(phoneDTORequestOne, userId);
//
//        // Assert
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(userId, result.getUserId()),
//                () -> assertEquals(phoneDTORequestOne.getNumber(), result.getNumber())
//        );
//    }
//
//    // ======================== TESTES: toPhoneEntityList ========================
//    @Test
//    void testToPhoneEntityListSuccess() {
//        // Arrange - Criando múltiplos telefones
//        PhoneDTORequest phoneEntityTwo = PhoneDTORequestFixture.build(3L, "988887777", "11");
//        List<PhoneDTORequest> phoneList = List.of(phoneDTORequestOne, phoneEntityTwo);
//
//        // Act
//        List<Phone> result = userConverter.toPhoneEntityList(phoneList);
//
//        // Assert
//        assertAll("Validar conversão de lista de PhoneDTORequest",
//                () -> assertNotNull(result),
//                () -> assertEquals(phoneList.size(), result.size()),
//                () -> assertEquals(phoneDTORequestOne.getNumber(), result.get(0).getNumber()),
//                () -> assertEquals(phoneEntityTwo.getNumber(), result.get(1).getNumber())
//        );
//    }
//
//    // ======================== TESTES: toUserResponse ========================
//    @Test
//    void testToUserResponseSuccess() {
//        // Act
//        UserDTOResponse result = userConverter.toUserResponse(user);
//
//        // Assert
//        assertAll("Validar conversão de User para UserDTOResponse",
//                () -> assertNotNull(result),
//                () -> assertEquals(user.getId(), result.getId()),
//                () -> assertEquals(user.getName(), result.getName()),
//                () -> assertEquals(user.getEmail(), result.getEmail()),
//                () -> assertEquals(user.getPassword(), result.getPassword()),
//                () -> assertNotNull(result.getAddresses()),
//                () -> assertNotNull(result.getPhones())
//        );
//    }
//
//    @Test
//    void testToUserResponseWithoutAddresses() {
//        // Arrange
//        user.setAddresses(null);
//
//        // Act
//        UserDTOResponse result = userConverter.toUserResponse(user);
//
//        // Assert
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertNull(result.getAddresses())
//        );
//    }
//
//    // ======================== TESTES: toAddressResponseList ========================
//    @Test
//    void testToAddressResponseListSuccess() {
//        // Act
//        List<AddressDTOResponse> result = userConverter.toAddressResponseList(addressesList);
//
//        // Assert
//        assertAll("Validar conversão de lista de Address",
//                () -> assertNotNull(result),
//                () -> assertEquals(addressesList.size(), result.size()),
//                () -> assertEquals(addressEntityTwo.getStreet(), result.get(0).getStreet()),
//                () -> assertEquals(addressEntityTwo.getId(), result.get(0).getId())
//        );
//    }
//
//    // ======================== TESTES: toAddressResponse ========================
//    @Test
//    void testToAddressResponseSuccess() {
//        // Act
//        AddressDTOResponse result = userConverter.toAddressResponse(addressEntityTwo);
//
//        // Assert
//        assertAll("Validar conversão de Address para AddressDTOResponse",
//                () -> assertNotNull(result),
//                () -> assertEquals(addressEntityTwo.getId(), result.getId()),
//                () -> assertEquals(addressEntityTwo.getStreet(), result.getStreet()),
//                () -> assertEquals(addressEntityTwo.getNumber(), result.getNumber()),
//                () -> assertEquals(addressEntityTwo.getCity(), result.getCity()),
//                () -> assertEquals(addressEntityTwo.getCep(), result.getCep()),
//                () -> assertEquals(addressEntityTwo.getState(), result.getState()),
//                () -> assertEquals(addressEntityTwo.getComplement(), result.getComplement())
//        );
//    }
//
//    // ======================== TESTES: toPhoneResponseList ========================
//    @Test
//    void testToPhoneResponseListSuccess() {
//        // Act
//        List<PhoneDTOResponse> result = userConverter.toPhoneResponseList(phonesList);
//
//        // Assert
//        assertAll("Validar conversão de lista de Phone",
//                () -> assertNotNull(result),
//                () -> assertEquals(phonesList.size(), result.size()),
//                () -> assertEquals(phoneEntityTwo.getNumber(), result.get(0).getNumber()),
//                () -> assertEquals(phoneEntityTwo.getId(), result.get(0).getId())
//        );
//    }
//
//    // ======================== TESTES: toPhoneResponse ========================
//    @Test
//    void testToPhoneResponseSuccess() {
//        // Act
//        PhoneDTOResponse result = userConverter.toPhoneResponse(phoneEntityTwo);
//
//        // Assert
//        assertAll("Validar conversão de Phone para PhoneDTOResponse",
//                () -> assertNotNull(result),
//                () -> assertEquals(phoneEntityTwo.getId(), result.getId()),
//                () -> assertEquals(phoneEntityTwo.getNumber(), result.getNumber()),
//                () -> assertEquals(phoneEntityTwo.getAreaCode(), result.getAreaCode())
//        );
//    }
//
//    // ======================== TESTES: updateUser ========================
//    @Test
//    void testUpdateUserSuccess() {
//        // Arrange
//        UserDTORequest updateRequest = UserDTORequestFixture
//                .build(null, "User Updated", "newemail@gmail.com", "5678", null, null);
//
//        // Act
//        User result = userConverter.updateUser(updateRequest, user);
//
//        // Assert
//        assertAll("Validar atualização de User",
//                () -> assertNotNull(result),
//                () -> assertEquals(user.getId(), result.getId(), "ID deve ser preservado"),
//                () -> assertEquals("User Updated", result.getName()),
//                () -> assertEquals("newemail@gmail.com", result.getEmail()),
//                () -> assertEquals("5678", result.getPassword())
//        );
//    }
//
//    @Test
//    void testUpdateUserKeepOldValues() {
//        // Arrange - UserDTORequest com valores nulos
//        UserDTORequest updateRequest = UserDTORequestFixture
//                .build(null, null, null, null, null, null);
//
//        // Act
//        User result = userConverter.updateUser(updateRequest, user);
//
//        // Assert
//        assertAll(
//                () -> assertEquals(user.getName(), result.getName()),
//                () -> assertEquals(user.getEmail(), result.getEmail()),
//                () -> assertEquals(user.getPassword(), result.getPassword())
//        );
//    }
//
//    // ======================== TESTES: updateAddress ========================
//    @Test
//    void testUpdateAddressSuccess() {
//        // Arrange
//        AddressDTORequest updateRequest = AddressDTORequestFixture
//                .build(null, "Rua Nova", 999L, "Apto Novo", "Curitiba", "PR", "81000-000");
//
//        // Act
//        Address result = userConverter.updateAddress(updateRequest, addressEntityTwo);
//
//        // Assert
//        assertAll("Validar atualização de Address",
//                () -> assertEquals(addressEntityTwo.getId(), result.getId()),
//                () -> assertEquals(addressEntityTwo.getUserId(), result.getUserId()),
//                () -> assertEquals("Rua Nova", result.getStreet()),
//                () -> assertEquals(999L, result.getNumber()),
//                () -> assertEquals("Curitiba", result.getCity())
//        );
//    }
//
//    @Test
//    void testUpdateAddressKeepOldValues() {
//        // Arrange
//        AddressDTORequest updateRequest = AddressDTORequestFixture
//                .build(null, null, null, null, null, null, null);
//
//        // Act
//        Address result = userConverter.updateAddress(updateRequest, addressEntityTwo);
//
//        // Assert
//        assertEquals(addressEntityTwo.getStreet(), result.getStreet());
//    }
//
//    // ======================== TESTES: updatePhone ========================
//    @Test
//    void testUpdatePhoneSuccess() {
//        // Arrange
//        PhoneDTORequest updateRequest = PhoneDTORequestFixture
//                .build(null, "999999999", "21");
//
//        // Act
//        Phone result = userConverter.updatePhone(updateRequest, phoneEntityTwo);
//
//        // Assert
//        assertAll("Validar atualização de Phone",
//                () -> assertEquals(phoneEntityTwo.getId(), result.getId()),
//                () -> assertEquals(phoneEntityTwo.getUserId(), result.getUserId()),
//                () -> assertEquals("999999999", result.getNumber()),
//                () -> assertEquals("21", result.getAreaCode())
//        );
//    }
//
//    @Test
//    void testUpdatePhoneKeepOldValues() {
//        // Arrange
//        PhoneDTORequest updateRequest = PhoneDTORequestFixture
//                .build(null, null, null);
//
//        // Act
//        Phone result = userConverter.updatePhone(updateRequest, phoneEntityTwo);
//
//        // Assert
//        assertEquals(phoneEntityTwo.getNumber(), result.getNumber());
//    }


