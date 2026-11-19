package com.javanauta.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanauta.user.business.UserService;
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
import com.javanauta.user.infrastructure.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.javanauta.user.business.UserService.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
 class UserControllerTest {

    @InjectMocks
    UserController userController;


    @Mock
    UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url;

    private UserDTORequest userDTORequest;

    private AddressDTORequest addressDTORequest;

    private PhoneDTORequest phoneDTORequest;

    private UserDTOResponse userDTOResponse;

    private AddressDTOResponse addressDTOResponse;

    private PhoneDTOResponse phoneDTOResponse;

    private String json;

    @BeforeEach
    void setup() throws JsonProcessingException {

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();

        url = "/user";



        addressDTORequest = AddressDTORequestFixture
                .build("Monte Belo",
                        430L,
                        "Casa",
                        "Blumenau",
                        "SC",
                        "89060-072");

        phoneDTORequest = PhoneDTORequestFixture
                .build("992120528",
                        "55");


        userDTORequest = UserDTORequestFixture
                .build("User",
                        "user@gmail.com",
                        "1234",
                        List.of(addressDTORequest),
                        List.of(phoneDTORequest));

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

        json = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(userDTORequest);


    }

    @Test
    void createUserWithSuccess() throws Exception {

        when(userService.createUser(any(UserDTORequest.class)))
                .thenReturn(userDTOResponse);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
              )
              .andExpect(status().isCreated())
              .andExpect(content().json(objectMapper.writeValueAsString(userDTOResponse)));

        verify(userService).createUser(any(UserDTORequest.class));
        verifyNoMoreInteractions(userService);


    }

    @Test
    void loginWithSuccess() throws Exception {

        String token = "Bearer jwt-token";

        when(userService.authenticateUser(any(UserDTORequest.class)))
                .thenReturn(token);

        mockMvc.perform(post(url + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(userService).authenticateUser(any(UserDTORequest.class));
        verifyNoMoreInteractions(userService);


    }

    @Test
    void shouldNotSaveUserWhenRequestBodyIsNull() throws Exception {


        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);

    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {

        when(userService.authenticateUser(any(UserDTORequest.class)))
                .thenThrow(new BadCredentialsException(INVALID_USERNAME));

        mockMvc.perform(post(url + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json)
                )
                .andExpect(status().isUnauthorized());

        verify(userService).authenticateUser(any(UserDTORequest.class));
        verifyNoMoreInteractions(userService);
    }


    @Test
    void shouldReturnBadRequestWhenLoginRequestBodyIsInvalid() throws Exception {

        mockMvc.perform(post(url + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(userService);


    }

    @Test
    void findUserByEmailWithSuccess() throws Exception {


        when(userService.findUserByEmail("user@gmail.com"))
                .thenReturn(userDTOResponse);

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("email", "user@gmail.com")
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTOResponse)));

        verify(userService).findUserByEmail("user@gmail.com");
        verifyNoMoreInteractions(userService);


    }

    @Test
    void shouldNotFindUserByEmailWhenParameterIsNull() throws Exception {

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);

    }

    @Test
    void deleUserByEmailWithSuccess() throws Exception {

        doNothing().when(userService).deleteUserByEmail("user@gmail.com");

        mockMvc.perform(delete(url + "/{email}", "user@gmail.com")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(userService).deleteUserByEmail("user@gmail.com");
        verifyNoMoreInteractions(userService);


    }

    @Test
    void shouldNotDeleteUserByEmailWhenPathVariableIsEmpty() throws Exception {


        mockMvc.perform(delete(url + "/")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUserWithSuccess() throws Exception {

        String authToken = "Bearer meu-token-jwt";

        when(userService.updateUser(eq(authToken), any(UserDTORequest.class)))
                .thenReturn(userDTOResponse);

        mockMvc.perform(put(url)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTOResponse)));

        verify(userService).updateUser(eq(authToken), any(UserDTORequest.class));
        verifyNoMoreInteractions(userService);


    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsInvalidOnUpdateUser() throws Exception {

        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);

    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmptyOnUpdateUser() throws Exception {

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequestWhenAuthorizationHeaderIsMissingOnUpdateUser() throws Exception {

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalidOnUpdateUser() throws Exception {

        String authToken = "Bearer token-invalido";

        when(userService.updateUser(
                eq(authToken),
                any(UserDTORequest.class)
        )).thenThrow(new BadCredentialsException("Token inválido"));

        mockMvc.perform(put(url)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isUnauthorized());

        verify(userService).updateUser(
                eq(authToken),
                any(UserDTORequest.class)
        );

        verifyNoMoreInteractions(userService);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistOnUpdateUser() throws Exception {

        String authToken = "Bearer meu-token-jwt";

        when(userService.updateUser(
                eq(authToken),
                any(UserDTORequest.class)
        )).thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(put(url)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());

        verify(userService).updateUser(
                eq(authToken),
                any(UserDTORequest.class)
        );

        verifyNoMoreInteractions(userService);
    }

    @Test
    void updateAddressWithSuccess() throws Exception {

        Long addressID = addressDTOResponse.getId();

        when(userService.updateAddress(eq(addressID), any(AddressDTORequest.class)))
                .thenReturn(addressDTOResponse);

        mockMvc.perform(put(url + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressDTOResponse)));

        verify(userService).updateAddress(eq(addressID), any(AddressDTORequest.class));
        verifyNoMoreInteractions(userService);

    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsInvalidOnUpdateAddress() throws Exception {

        mockMvc.perform(put(url + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);

    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmptyOnUpdateAddress() throws Exception {

        mockMvc.perform(put(url + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnNotFoundWhenAddressDoesNotExistOnUpdateAddress() throws Exception {

        Long addressID = addressDTOResponse.getId();

        when(userService.updateAddress(eq(addressID), any(AddressDTORequest.class)
        )).thenThrow(new ResourceNotFoundException(ID_NOT_FOUND + addressID));

        mockMvc.perform(put(url + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .content(json)
                )
                .andExpect(status().isNotFound());

        verify(userService).updateAddress(eq(addressID), any(AddressDTORequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void updatePhoneWithSuccess() throws Exception {

        Long phoneID = phoneDTOResponse.getId();

        when(userService.updatePhone(eq(phoneID), any(PhoneDTORequest.class)))
                .thenReturn(phoneDTOResponse);

        mockMvc.perform(put(url + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(phoneDTOResponse)));

        verify(userService).updatePhone(eq(phoneID), any(PhoneDTORequest.class));
        verifyNoMoreInteractions(userService);

    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsInvalidOnUpdatePhone() throws Exception {

        mockMvc.perform(put(url + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);

    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmptyOnUpdatePhone() throws Exception {

        mockMvc.perform(put(url + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnNotFoundWhenPhoneDoesNotExistOnUpdatePhone() throws Exception {

        Long phoneID = phoneDTOResponse.getId();

        when(userService.updatePhone(eq(phoneID), any(PhoneDTORequest.class)
        )).thenThrow(new ResourceNotFoundException(ID_NOT_FOUND + phoneID));

        mockMvc.perform(put(url + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .content(json)
                )
                .andExpect(status().isNotFound());

        verify(userService).updatePhone(eq(phoneID), any(PhoneDTORequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void addAddressWithSuccess() throws Exception {

        String authToken = "Bearer meu-token-jwt";

        when(userService.addAddressForUser(eq(authToken), any(AddressDTORequest.class)))
                .thenReturn(addressDTOResponse);

        mockMvc.perform(post(url + "/address")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressDTOResponse)));

        verify(userService).addAddressForUser(eq(authToken), any(AddressDTORequest.class));
        verifyNoMoreInteractions(userService);


    }


    @Test
    void shouldReturnBadRequestWhenRequestBodyIsInvalidOnAddAddress() throws Exception {

        mockMvc.perform(post(url + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmptyOnAddAddress() throws Exception {

        mockMvc.perform(post(url + "/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalidOnAddAddress() throws Exception {

        String authToken = "Bearer token-invalido";

        when(userService.addAddressForUser(
                eq(authToken),
                any(AddressDTORequest.class)
        )).thenThrow(new BadCredentialsException("Token inválido"));

        mockMvc.perform(post(url + "/address")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isUnauthorized());

        verify(userService).addAddressForUser(
                eq(authToken),
                any(AddressDTORequest.class)
        );

        verifyNoMoreInteractions(userService);
    }
    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistOnAddAddress() throws Exception {

        String authToken = "Bearer meu-token-jwt";

        when(userService.addAddressForUser(
                eq(authToken),
                any(AddressDTORequest.class)
        )).thenThrow(new ResourceNotFoundException(EMAIL_NOT_FOUND + userDTORequest.getEmail()));

        mockMvc.perform(post(url + "/address")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());

        verify(userService).addAddressForUser(
                eq(authToken),
                any(AddressDTORequest.class)
        );

        verifyNoMoreInteractions(userService);
    }


    @Test
    void addAPhoneWithSuccess() throws Exception {

        String authToken = "Bearer meu-token-jwt";

        when(userService.addPhoneForUser(eq(authToken), any(PhoneDTORequest.class)))
                .thenReturn(phoneDTOResponse);

        mockMvc.perform(post(url + "/phone")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(phoneDTOResponse)));

        verify(userService).addPhoneForUser(eq(authToken), any(PhoneDTORequest.class));
        verifyNoMoreInteractions(userService);


    }


    @Test
    void shouldReturnBadRequestWhenRequestBodyIsInvalidOnAddPhone() throws Exception {

        mockMvc.perform(post(url + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmptyOnAddPhone() throws Exception {

        mockMvc.perform(post(url + "/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalidOnAddPhone() throws Exception {

        String authToken = "Bearer token-invalido";

        when(userService.addPhoneForUser(eq(authToken), any(PhoneDTORequest.class)
        )).thenThrow(new BadCredentialsException("Token inválido"));

        mockMvc.perform(post(url + "/phone")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isUnauthorized());

        verify(userService).addPhoneForUser(eq(authToken), any(PhoneDTORequest.class)
        );

        verifyNoMoreInteractions(userService);
    }
    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistOnAddPhone() throws Exception {

        String authToken = "Bearer meu-token-jwt";

        when(userService.addPhoneForUser(eq(authToken), any(PhoneDTORequest.class)
        )).thenThrow(new ResourceNotFoundException(EMAIL_NOT_FOUND + userDTORequest.getEmail()));

        mockMvc.perform(post(url + "/phone")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());

        verify(userService).addPhoneForUser(eq(authToken), any(PhoneDTORequest.class));

        verifyNoMoreInteractions(userService);
    }





}
