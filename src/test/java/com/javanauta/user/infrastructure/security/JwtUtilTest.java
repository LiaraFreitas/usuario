package com.javanauta.user.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
 class JwtUtilTest {


    private JwtUtil jwtUtil;

    private  static final String USERNAME = "usuario@email.com";

    private static  final String SECRET_KEY = Base64.getEncoder()
            .encodeToString(
                    "0123456789abcdef0123456789abcdef"
                            .getBytes(StandardCharsets.UTF_8)
            );

    private static final String OTHER_SECRET_KEY = Base64.getEncoder()
            .encodeToString(
                    "abcdef0123456789abcdef0123456789"
                            .getBytes(StandardCharsets.UTF_8)
            );

    @BeforeEach
    void setUp(){

        jwtUtil = new JwtUtil(SECRET_KEY);

    }

    @Test
    void shouldGenerateTokenSuccessfully(){

        String token = jwtUtil.generateToken(USERNAME);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(USERNAME, jwtUtil.extractEmailFromToken(token));
    }


    @Test
    void shouldExtractClaimsFromValidToken(){
        String token = jwtUtil.generateToken(USERNAME);

        var claims = jwtUtil.extractClaims(token);

        assertNotNull(claims);
        assertEquals(USERNAME, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void shouldExtractEmailFromValidToken(){

        String token = jwtUtil.generateToken(USERNAME);

        String email = jwtUtil.extractEmailFromToken(token);

        assertEquals(USERNAME, email);


    }

    @Test
    void shouldReturnFalseWhenTokenIsNotExpired(){

        String token = jwtUtil.generateToken(USERNAME);

        boolean expired = jwtUtil.isTokenExpired(token);

        assertFalse(expired);

    }

    @Test
    void shouldThrowExpiredJwtExceptionWhenTokenIsExpired(){

        String expiredToken = generateExpiredToken(USERNAME);

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtUtil.extractClaims(expiredToken)
        );

    }

    @Test
    void shouldThrowSignatureExceptionWhenTokenWasSignedWithAnotherKey(){

        String token = generateTokenWithAnotherKey(USERNAME);

        assertThrows(
                SignatureException.class,
                () -> jwtUtil.extractClaims(token)
        );

    }

    @Test
    void shouldThrowJwtExceptionWhenTokenIsMalformed(){

        String malformedToken = "token-invalido";

        assertThrows(
                JwtException.class,
                () -> jwtUtil.extractClaims(malformedToken)
        );
    }

    private String generateExpiredToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(SECRET_KEY)
        );

        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(currentTime - 7_200_000))
                .expiration(new Date(currentTime - 3_600_000))
                .signWith(key)
                .compact();
    }

    private String generateTokenWithAnotherKey(String username){

        SecretKey key =  Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(OTHER_SECRET_KEY)
        );

        return  Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(key)
                .compact();

    }
}


