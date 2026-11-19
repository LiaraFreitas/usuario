package com.javanauta.user.infrastructure.security;

import com.javanauta.user.infrastructure.entity.User;
import com.javanauta.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private static final String EMAIL = "teste@email.com";
    private static final String PASSWORD = "password";

    @Test
    void shouldLoadUserDetailsSuccessfully(){
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));


        when(user.getEmail())
                .thenReturn(EMAIL);

        when(user.getPassword())
                .thenReturn(PASSWORD);

        UserDetails result = userDetailsService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());

        verify(userRepository).findByEmail(EMAIL);


    }


    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist(){

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(EMAIL)

        );

        assertEquals("User not found: " + EMAIL,
                exception.getMessage());


        verify(userRepository).findByEmail(EMAIL);
    }



}
