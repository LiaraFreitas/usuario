package com.javanauta.user.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {
        SecurityConfig.class,
        SecurityConfigTest.TestConfig.class
    },
    properties = {
            "jwt.secret=S0V5VGVzdEtleUZvckpXVE1pbmltdW0zMkJ5dGVzIQ=="

    }
)

@AutoConfigureMockMvc
class SecurityConfigTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @TestConfiguration
    @EnableWebMvc
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    static class TestConfig {

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    static class TestController {

        @GetMapping("/user/profile")
        public ResponseEntity<String> profile() {
            return ResponseEntity.ok("Authenticated");
        }

        @PostMapping("/user/login")
        public ResponseEntity<String> login() {
            return ResponseEntity.ok("Public");
        }

        @PostMapping("/user")
        public ResponseEntity<String> createUser() {
            return ResponseEntity.ok("User created");
        }

        @GetMapping("/user/address/{cep}")
        public ResponseEntity<String> address() {
            return ResponseEntity.ok("Address found");
        }

    }

   @Test
   void shouldAllowAccessToPublicEndpointWithoutToken() throws Exception {
        mockMvc.perform(post("/user/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public"));

    }

    @Test
    void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void shouldEncodePasswordUsingBCrypt(){

        String password = "123456";

        String encodedPassword = passwordEncoder.encode(password);

        assertNotEquals(password, encodedPassword, "BCrypt should encode the plain-text password");
        assertTrue(passwordEncoder.matches(password, encodedPassword), "BCrypt should verify the original password against its encoded value");


    }

    @Test
    void shouldExposeAuthenticationManagerBean(){
        assertNotNull(authenticationManager, "Security configuration should expose an AuthenticationManager bean");
    }

    @Test
    void shouldAllowAccessToProtectedEndpointWithValidToken() throws Exception {

        String token = "valid-token";
        String email = "teste@email.com";

        UserDetails userDetails = User.withUsername(email)
                .password("123456")
                .authorities("ROLE_USER")
                .build();

        when(jwtUtil.extractEmailFromToken(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(token, email))
                .thenReturn(true);

        mockMvc.perform(get("/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Authenticated"));

    }

    @Test
    void shouldDenyAccessWhenTokenIsInvalid() throws Exception {

        String token = "invalid-token";
        String email = "teste@email.com";

        UserDetails userDetails = User.withUsername(email)
                .password("123456")
                .authorities("ROLE_USER")
                .build();

        when(jwtUtil.extractEmailFromToken(token))
                .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(token, email))
                .thenReturn(false);

        mockMvc.perform(get("/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowUserRegistrationWithoutToken() throws Exception {
        mockMvc.perform(post("/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("User created"));
    }

    @Test
    void shouldAllowAddressSearchWithoutToken() throws Exception {
        mockMvc.perform(get("/user/address/12345678"))
                .andExpect(status().isOk())
                .andExpect(content().string("Address found"));
    }



}
