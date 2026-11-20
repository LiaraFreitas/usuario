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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        assertNotNull(result, "Loading an existing user should return UserDetails");
        assertEquals(EMAIL, result.getUsername(), "UserDetails should contain the requested email");
        assertEquals(PASSWORD, result.getPassword(), "UserDetails should contain the user's stored password");
        assertTrue(result.getAuthorities().isEmpty(), "The loaded user should have no authorities");

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
                exception.getMessage(),
                "Exception message should identify the missing user");


        verify(userRepository).findByEmail(EMAIL);
    }



}
