package com.javanauta.user.infrastructure.security;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private  JwtRequestFilter jwtRequestFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    private static final String TOKEN = "valid-token";
    private static final String USERNAME = "user@gmail.com";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown(){

        SecurityContextHolder.clearContext();

    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "A missing Authorization header should not authenticate the request");

    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {

        request.addHeader("Authorization", "Basic token-invalido");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "A non-Bearer Authorization header should not authenticate the request");

    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + TOKEN);

        UserDetails userDetails = User.withUsername(USERNAME)
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(jwtUtil.extractEmailFromToken(TOKEN))
                .thenReturn(USERNAME);

        when(userDetailsService.loadUserByUsername(USERNAME))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(TOKEN, USERNAME))
                .thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

       var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication, "A valid token should create an authentication object");
        assertTrue(authentication.isAuthenticated(), "The authentication created by a valid token should be authenticated");
        assertEquals(USERNAME, authentication.getName(), "The authenticated principal should use the token username");
        assertEquals(userDetails, authentication.getPrincipal(), "The authentication principal should be the loaded UserDetails");


        verify(jwtUtil).extractEmailFromToken(TOKEN);
        verify(userDetailsService).loadUserByUsername(USERNAME);
        verify(jwtUtil).validateToken(TOKEN, USERNAME);
        verify(filterChain).doFilter(request, response);

    }

    @Test
    void shouldNotAuthenticateUserWhenAlreadyAuthenticated() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + TOKEN);

        var existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        USERNAME,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        when(jwtUtil.extractEmailFromToken(TOKEN))
                .thenReturn(USERNAME);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        assertSame(
                existingAuthentication,
                SecurityContextHolder.getContext().getAuthentication(),
                "Existing authentication should remain in the security context"
        );

        verify(jwtUtil).extractEmailFromToken(TOKEN);
        verifyNoInteractions(userDetailsService);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(filterChain).doFilter(request, response);


    }

    @Test
    void shouldContinueFilterChainWhenUsernameIsNull() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + TOKEN);

        when(jwtUtil.extractEmailFromToken(TOKEN))
                .thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "A token with no extractable username should not authenticate the request");

        verify(jwtUtil).extractEmailFromToken(TOKEN);
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);

    }

    @Test
    void shouldNotAuthenticateUserWhenTokenValidationFails() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + TOKEN);

        UserDetails userDetails = User.withUsername(USERNAME)
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(jwtUtil.extractEmailFromToken(TOKEN))
                .thenReturn(USERNAME);

        when(userDetailsService.loadUserByUsername(USERNAME))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(TOKEN, USERNAME))
                .thenReturn(false);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication(), "A token that fails validation should not authenticate the request");

        verify(jwtUtil).extractEmailFromToken(TOKEN);
        verify(userDetailsService).loadUserByUsername(USERNAME);
        verify(jwtUtil).validateToken(TOKEN, USERNAME);
        verify(filterChain).doFilter(request, response);



    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsExpired() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + TOKEN);

        ExpiredJwtException exception = mock(ExpiredJwtException.class);

        when(exception.getMessage())
                .thenReturn("JWT expired");

        when(jwtUtil.extractEmailFromToken(TOKEN))
                .thenThrow(exception);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus(), "An expired token should produce an unauthorized HTTP status");
        assertTrue(response.getContentType().contains("application/json"), "The expired-token error response should use JSON content");

        JsonNode error = objectMapper.readTree(response.getContentAsString());

        assertEquals("Token expired", error.get("message").asText(), "The error body should describe the expired token");
        assertEquals("JWT expired", error.get("error").asText(), "The error body should preserve the JWT exception message");

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "An expired token should leave the security context unauthenticated");

        verify(jwtUtil).extractEmailFromToken(TOKEN);
        verifyNoInteractions(userDetailsService);
        verify(filterChain, never()).doFilter(request, response);
    }

}
