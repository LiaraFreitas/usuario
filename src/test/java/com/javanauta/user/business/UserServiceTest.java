package com.javanauta.user.business;
import com.javanauta.user.business.converter.UserConverter;
import com.javanauta.user.business.dto.in.AddressDTORequest;
import com.javanauta.user.business.dto.in.AddressDTORequestFixture;
import com.javanauta.user.business.dto.in.PhoneDTORequest;
import com.javanauta.user.business.dto.in.PhoneDTORequestFixture;
import com.javanauta.user.business.dto.in.UserDTORequest;
import com.javanauta.user.business.dto.in.UserDTORequestFixture;
import com.javanauta.user.business.dto.out.AddressDTOResponse;
import com.javanauta.user.business.dto.out.AddressDTOResponseFixture;
import com.javanauta.user.business.dto.out.PhoneDTOResponse;
import com.javanauta.user.business.dto.out.PhoneDTOResponseFixture;
import com.javanauta.user.business.dto.out.UserDTOResponse;
import com.javanauta.user.business.dto.out.UserDTOResponseFixture;
import com.javanauta.user.infrastructure.entity.Address;
import com.javanauta.user.infrastructure.entity.Phone;
import com.javanauta.user.infrastructure.entity.User;
import com.javanauta.user.infrastructure.exceptions.ConflictException;
import com.javanauta.user.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.user.infrastructure.exceptions.UnauthorizedException;
import com.javanauta.user.infrastructure.repository.AddressRepository;
import com.javanauta.user.infrastructure.repository.PhoneRepository;
import com.javanauta.user.infrastructure.repository.UserRepository;
import com.javanauta.user.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static com.javanauta.user.business.UserService.EMAIL_NOT_FOUND;
import static com.javanauta.user.business.UserService.ID_NOT_FOUND;
import static com.javanauta.user.business.UserService.INVALID_USERNAME;
import static com.javanauta.user.business.UserService.REGISTERED_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class UserServiceTest {

    private static final String JWT_TOKEN = "my-token-jwt";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;


    private User userEntity;
    private Address addressEntityOne;
    private Address addressEntityTwo;
    private Phone phoneEntityOne;
    private Phone phoneEntityTwo;
    private UserDTORequest userDTORequest;
    private AddressDTORequest addressDTORequestOne;
    private AddressDTORequest addressDTORequestTwo;
    private PhoneDTORequest phoneDTORequestOne;
    private PhoneDTORequest phoneDTORequestTwo;
    private UserDTOResponse userDTOResponse;
    private AddressDTOResponse addressDTOResponse;
    private PhoneDTOResponse phoneDTOResponse;



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

        phoneDTORequestOne = PhoneDTORequestFixture
                .build("992120528",
                        "55");

        phoneDTORequestTwo = PhoneDTORequestFixture
                .build("981141827",
                        "47");


        userDTORequest = UserDTORequestFixture
                .build("User",
                        "user@gmail.com",
                        "1234",
                        List.of(addressDTORequestOne, addressDTORequestTwo),
                        List.of(phoneDTORequestOne, phoneDTORequestTwo));



 //====================== RESPONSE ===============================
        addressDTOResponse = AddressDTOResponseFixture
                .build(1L,
                        "Monte Belo",
                        430L,
                        "Casa",
                        "Blumenau",
                        "SC",
                        "89060-072");

        phoneDTOResponse = PhoneDTOResponseFixture
                .build(1L,
                        "992120528",
                        "55");

        userDTOResponse = UserDTOResponseFixture
                .build(1L,
                        "User",
                        "user@gmail.com",
                        passwordEncoder.toString(),
                        List.of(addressDTOResponse),
                        List.of(phoneDTOResponse));


    }


    @Test
    void createUserWithSuccess() {

        //arrange - Preparar

        String originalPassword = userDTORequest.getPassword();
        String encodedPassword = userDTOResponse.getPassword();

        userEntity.setPassword(encodedPassword);

        when(passwordEncoder.encode(originalPassword))
                .thenReturn(encodedPassword);

        when(userConverter.toUserEntity(any(UserDTORequest.class)))
                .thenReturn(userEntity);

        when(userRepository.save(userEntity))
                .thenReturn(userEntity);

        when(userConverter.toUserResponse(userEntity))
                .thenReturn(userDTOResponse);

        //act - Executar

        UserDTOResponse response = userService.createUser(userDTORequest);


        //assert - verificar

        assertNotNull(response, "Creating a user should return a response DTO");
        assertEquals(response, userDTOResponse, "The created user response should match the converter result");

        ArgumentCaptor<UserDTORequest> requestArgumentCaptor =
                ArgumentCaptor.forClass(UserDTORequest.class);

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);


        InOrder inOrder = inOrder(passwordEncoder, userConverter, userRepository);

        inOrder.verify(passwordEncoder).encode(originalPassword);
        inOrder.verify(userConverter).toUserEntity(requestArgumentCaptor.capture());
        inOrder.verify(userRepository).save(userArgumentCaptor.capture());
        inOrder.verify(userConverter).toUserResponse(userEntity);


        assertEquals(encodedPassword, requestArgumentCaptor.getValue().getPassword(), "The encoded password should be assigned to the request entity");
        assertEquals(encodedPassword, userArgumentCaptor.getValue().getPassword(), "The encoded password should be stored on the user entity");

    }


    @Test
    void createUserWithRegisteredEmailShouldThrowConflictException(){

        when(userConverter.toUserEntity(any(UserDTORequest.class)))
                .thenReturn(userEntity);

        when(userRepository.save(userEntity))
                .thenThrow(new DataIntegrityViolationException("email_unique"));


        ConflictException exception = assertThrows(ConflictException.class, () -> {
            userService.createUser(userDTORequest);
        });

        assertEquals(REGISTERED_EMAIL + userDTORequest.getEmail(), exception.getMessage(), "Registration with an existing email should report the registered-email conflict");

    }

    @Test
    void authenticateUserWithSuccess(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn(userDTORequest.getEmail());

        when(jwtUtil.generateToken(userDTORequest.getEmail()))
                .thenReturn(JWT_TOKEN);

        String response = userService.authenticateUser(userDTORequest);

        assertEquals("Bearer " + JWT_TOKEN, response, "Authentication should return a Bearer token");
    }

    @Test
    void authenticateUserWhenUserDoesNotExist(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow( new UsernameNotFoundException(INVALID_USERNAME));


        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> userService.authenticateUser(userDTORequest));

        assertEquals(INVALID_USERNAME, exception.getMessage(), "Authentication failures should report the invalid-username message");


        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class));

        verify(jwtUtil, never()).generateToken(anyString());


    }

    @Test
    void authenticateUserWithInvalidPassword(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow( new BadCredentialsException(INVALID_USERNAME));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> userService.authenticateUser(userDTORequest));

        assertEquals(INVALID_USERNAME, exception.getMessage(), "Authentication failures should report the invalid-username message");


        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class));

        verify(jwtUtil, never()).generateToken(anyString());

    }

    @Test
    void authenticateUserWithInvalidCredentials(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow( new AuthorizationDeniedException(INVALID_USERNAME));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> userService.authenticateUser(userDTORequest));

        assertEquals(INVALID_USERNAME, exception.getMessage(), "Authentication failures should report the invalid-username message");


        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class));

        verify(jwtUtil, never()).generateToken(anyString());

    }

    @Test
    void findUserByEmailWithSuccess(){

        String email = userEntity.getEmail();

        when(userConverter.toUserResponse(userEntity))
                .thenReturn(userDTOResponse);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));


        UserDTOResponse response = userService.findUserByEmail(email);

        assertEquals(userDTOResponse, response, "The service should return the converted user DTO");

        verify(userConverter).toUserResponse(userEntity);
        verify(userRepository).findByEmail(email);

    }

    @Test
    void findUserByEmailWhenUserDoesNotExist(){

        String email = userEntity.getEmail();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
           userService.findUserByEmail(email);
        });

        assertEquals(EMAIL_NOT_FOUND + userEntity.getEmail(), exception.getMessage(), "The missing-user exception should include the requested email");
    }


    @Test
    void deleteUserByEmailWithSuccess(){

        String email = userEntity.getEmail();

        doNothing().when(userRepository).deleteByEmail(email);

        userService.deleteUserByEmail(email);

        verify(userRepository).deleteByEmail(email);


    }


    @Test
    void updateUserWithSucess(){

        String authToken = "Bearer " + JWT_TOKEN;
        String email = userDTORequest.getEmail();


        String originalPassword = userDTORequest.getPassword();
        String encodedPassword = userDTOResponse.getPassword();

        userEntity.setPassword(encodedPassword);

        when(jwtUtil.extractEmailFromToken(JWT_TOKEN))
                .thenReturn(email);


        when(passwordEncoder.encode(originalPassword))
                .thenReturn(encodedPassword);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));

        when(userConverter.updateUser(userDTORequest, userEntity))
                .thenReturn(userEntity);

        when(userRepository.save(userEntity))
                .thenReturn(userEntity);

        when(userConverter.toUserResponse(userEntity))
                .thenReturn(userDTOResponse);


        UserDTOResponse response = userService.updateUser(authToken, userDTORequest);

        assertEquals(userDTOResponse, response, "The service should return the converted user DTO");


        verify(jwtUtil).extractEmailFromToken(JWT_TOKEN);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).findByEmail(email);
        verify(userConverter).updateUser(userDTORequest, userEntity);
        verify(userRepository).save(userEntity);
        verify(userConverter).toUserResponse(userEntity);


    }

    @Test
    void updateUserWhenUserDoesNotExist(){

        String authToken = "Bearer " + JWT_TOKEN;
        String email = userDTORequest.getEmail();


        String originalPassword = userDTORequest.getPassword();
        String encodedPassword = userDTOResponse.getPassword();

        userEntity.setPassword(encodedPassword);

        when(jwtUtil.extractEmailFromToken(JWT_TOKEN))
                .thenReturn(email);


        when(passwordEncoder.encode(originalPassword))
                .thenReturn(encodedPassword);


        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(authToken, userDTORequest);
        });

        assertEquals(EMAIL_NOT_FOUND, exception.getMessage(), "The missing-user exception should use the email-not-found message");

        verify(jwtUtil).extractEmailFromToken(JWT_TOKEN);
        verify(userRepository).findByEmail(email);

        verify(userConverter, never()).updateUser(any(), any());
        verify(userRepository, never()).save(any(User.class));
        verify(userConverter, never()).toUserResponse(any());
    }

    @Test
    void updateAddressWithSucess(){
        Long addressID = addressEntityOne.getId();

        when(addressRepository.findById(addressID))
                .thenReturn(Optional.of(addressEntityOne));

        when(addressRepository.save(addressEntityOne))
                .thenReturn(addressEntityOne);

        when(userConverter.toAddressResponse(addressEntityOne))
                .thenReturn(addressDTOResponse);

        when(userConverter.updateAddress(addressDTORequestOne, addressEntityOne))
                .thenReturn(addressEntityOne);

        AddressDTOResponse response = userService.updateAddress(addressID, addressDTORequestOne);

        assertEquals(addressDTOResponse, response, "The service should return the converted address DTO");

        verify(addressRepository).findById(addressID);
        verify(addressRepository).save(addressEntityOne);
        verify(userConverter).toAddressResponse(addressEntityOne);

    }

    @Test
    void updateAddressWhenIdNotFound(){

        Long addressID = addressEntityOne.getId();

        when(addressRepository.findById(addressID))
                .thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateAddress(addressID, addressDTORequestOne));

        assertEquals(ID_NOT_FOUND + addressID, exception.getMessage(), "The missing-address exception should include the address ID");


    }

    @Test
    void updatePhoneWithSucess(){
        Long phoneID = phoneEntityOne.getId();

        when(phoneRepository.findById(phoneID))
                .thenReturn(Optional.of(phoneEntityOne));

        when(phoneRepository.save(phoneEntityOne))
                .thenReturn(phoneEntityOne);

        when(userConverter.toPhoneResponse(phoneEntityOne))
                .thenReturn(phoneDTOResponse);

        when(userConverter.updatePhone(phoneDTORequestOne, phoneEntityOne))
                .thenReturn(phoneEntityOne);

        PhoneDTOResponse response = userService.updatePhone(phoneID, phoneDTORequestOne);

        assertEquals(phoneDTOResponse, response, "The service should return the converted phone DTO");

        verify(phoneRepository).findById(phoneID);
        verify(phoneRepository).save(phoneEntityOne);
        verify(userConverter).toPhoneResponse(phoneEntityOne);

    }

    @Test
    void updatePhoneWhenIdNotFound(){
        Long phoneID = phoneEntityOne.getId();

        when(phoneRepository.findById(phoneID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updatePhone(phoneID, phoneDTORequestOne));


        assertEquals(ID_NOT_FOUND + phoneID, exception.getMessage(), "The missing-phone exception should include the phone ID");

    }

    @Test
    void addAddressForUserWithSucess(){

        String authToken = "Bearer " + JWT_TOKEN;
        String email = userDTORequest.getEmail();



        when(jwtUtil.extractEmailFromToken(JWT_TOKEN))
                .thenReturn(email);


        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));

        when(userConverter.toAddressEntity(addressDTORequestOne, userEntity.getId()))
                .thenReturn(addressEntityOne);

        when(addressRepository.save(addressEntityOne))
                .thenReturn(addressEntityOne);

        when(userConverter.toAddressResponse(addressEntityOne))
                .thenReturn(addressDTOResponse);

        AddressDTOResponse response = userService.addAddressForUser(authToken, addressDTORequestOne);


        assertEquals(addressDTOResponse, response, "The service should return the converted address DTO");

        verify(jwtUtil).extractEmailFromToken(JWT_TOKEN);
        verify(userRepository).findByEmail(email);
        verify(userConverter).toAddressEntity(addressDTORequestOne, userEntity.getId());
        verify(addressRepository).save(addressEntityOne);
        verify(userConverter).toAddressResponse(addressEntityOne);

    }

    @Test
    void addAddressWhenUserNotFound(){
        String authToken = "Bearer " + JWT_TOKEN;
        String email = userDTORequest.getEmail();


        when(jwtUtil.extractEmailFromToken(JWT_TOKEN))
                .thenReturn(email);


        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.addAddressForUser(authToken, addressDTORequestOne);
        });

        assertEquals(EMAIL_NOT_FOUND + email, exception.getMessage(), "The missing-user exception should include the requested email");

        verify(jwtUtil).extractEmailFromToken(JWT_TOKEN);
        verify(userRepository).findByEmail(email);
        verify(userConverter, never()).toAddressEntity(any(AddressDTORequest.class), anyLong());
        verify(addressRepository, never()).save(any(Address.class));
        verify(userConverter, never()).toAddressResponse(any(Address.class));

    }

    @Test
    void addPhoneForUserWithSucess(){


        String authToken = "Bearer " + JWT_TOKEN;
        String email = userDTORequest.getEmail();



        when(jwtUtil.extractEmailFromToken(JWT_TOKEN))
                .thenReturn(email);


        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(userEntity));

        when(userConverter.toPhoneEntity(phoneDTORequestOne, userEntity.getId()))
                .thenReturn(phoneEntityOne);

        when(phoneRepository.save(phoneEntityOne))
                .thenReturn(phoneEntityOne);

        when(userConverter.toPhoneResponse(phoneEntityOne))
                .thenReturn(phoneDTOResponse);

        PhoneDTOResponse response = userService.addPhoneForUser(authToken, phoneDTORequestOne);


        assertEquals(phoneDTOResponse, response, "The service should return the converted phone DTO");

        verify(jwtUtil).extractEmailFromToken(JWT_TOKEN);
        verify(userRepository).findByEmail(email);
        verify(userConverter).toPhoneEntity(phoneDTORequestOne, userEntity.getId());
        verify(phoneRepository).save(phoneEntityOne);
        verify(userConverter).toPhoneResponse(phoneEntityOne);


    }

    @Test
    void addPhoneWhenUserNotFound(){
        String authToken = "Bearer " + JWT_TOKEN;
        String email = userDTORequest.getEmail();


        when(jwtUtil.extractEmailFromToken(JWT_TOKEN))
                .thenReturn(email);


        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.addPhoneForUser(authToken, phoneDTORequestOne);
        });

        assertEquals(EMAIL_NOT_FOUND + email, exception.getMessage(), "The missing-user exception should include the requested email");

        verify(jwtUtil).extractEmailFromToken(JWT_TOKEN);
        verify(userRepository).findByEmail(email);
        verify(userConverter, never()).toPhoneEntity(any(PhoneDTORequest.class), anyLong());
        verify(phoneRepository, never()).save(any(Phone.class));
        verify(userConverter, never()).toPhoneResponse(any(Phone.class));


    }




}



