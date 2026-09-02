package com.javanauta.user.business;

import com.javanauta.user.business.converter.UserConverter;
import com.javanauta.user.business.dto.out.UserDTOResponse;
import com.javanauta.user.infrastructure.entity.User;
import com.javanauta.user.infrastructure.exceptions.ConflictException;
import com.javanauta.user.infrastructure.repository.AddressRepository;
import com.javanauta.user.infrastructure.repository.PhoneRepository;
import com.javanauta.user.infrastructure.repository.UserRepository;
import com.javanauta.user.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - createUser")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private UserDTOResponse userDTOResponse;
    private User user;
    private UserDTOResponse expectedUserDTOResponse;

    @BeforeEach
    void setUp() {
        userDTOResponse = UserDTOResponse.builder()
                .name("João Silva")
                .email("joao@example.com")
                .password("senha123")
                .build();

        user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@example.com")
                .password("encoded_password")
                .build();

        expectedUserDTOResponse = UserDTOResponse.builder()
                .name("João Silva")
                .email("joao@example.com")
                .password("encoded_password")
                .build();
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void testCreateUserSuccess() {
        // Arrange
        when(passwordEncoder.encode("senha123")).thenReturn("encoded_password");
        when(userConverter.requestToEntityUser(userDTOResponse)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userConverter.toUserResponse(user)).thenReturn(expectedUserDTOResponse);

        // Act
        UserDTOResponse result = userService.createUser(userDTOResponse);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        assertEquals("joao@example.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userConverter, times(1)).requestToEntityUser(any(UserDTOResponse.class));
        verify(userRepository, times(1)).save(user);
        verify(userConverter, times(1)).toUserResponse(user);
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando email já está cadastrado")
    void testCreateUserWithDuplicateEmail() {
        // Arrange
        when(passwordEncoder.encode("senha123")).thenReturn("encoded_password");
        when(userConverter.requestToEntityUser(userDTOResponse)).thenReturn(user);

        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Unique constraint violation: email_unique"
        );
        when(userRepository.save(user)).thenThrow(exception);

        // Act & Assert
        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            userService.createUser(userDTOResponse);
        });

        assertEquals("Email já cadastrado joao@example.com", conflictException.getMessage());
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userConverter, times(1)).requestToEntityUser(any(UserDTOResponse.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve relançar DataIntegrityViolationException quando não for relacionado a email")
    void testCreateUserWithOtherDataIntegrityException() {
        // Arrange
        when(passwordEncoder.encode("senha123")).thenReturn("encoded_password");
        when(userConverter.requestToEntityUser(userDTOResponse)).thenReturn(user);

        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Some other constraint violation"
        );
        when(userRepository.save(user)).thenThrow(exception);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.createUser(userDTOResponse);
        });

        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userConverter, times(1)).requestToEntityUser(any(UserDTOResponse.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve codificar a senha do usuário antes de salvar")
    void testCreateUserEncodesPassword() {
        // Arrange
        String rawPassword = "senha123";
        String encodedPassword = "encoded_senha123";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userConverter.requestToEntityUser(userDTOResponse)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userConverter.toUserResponse(user)).thenReturn(expectedUserDTOResponse);

        // Act
        userService.createUser(userDTOResponse);

        // Assert
        verify(passwordEncoder, times(1)).encode(rawPassword);
        assertEquals(encodedPassword, userDTOResponse.getPassword());
    }
}

