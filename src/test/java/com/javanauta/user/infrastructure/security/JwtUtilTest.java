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
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

        assertNotNull(token, "Generating a JWT should return a token");
        assertFalse(token.isBlank(), "The generated JWT should not be blank");
        assertEquals(USERNAME, jwtUtil.extractEmailFromToken(token), "The token subject should contain the supplied username");
    }


    @Test
    void shouldExtractClaimsFromValidToken(){
        String token = jwtUtil.generateToken(USERNAME);

        var claims = jwtUtil.extractClaims(token);

        assertNotNull(claims, "A valid JWT should expose its claims");
        assertEquals(USERNAME, claims.getSubject(), "The JWT subject should match the supplied username");
        assertNotNull(claims.getIssuedAt(), "The JWT claims should include an issued-at date");
        assertNotNull(claims.getExpiration(), "The JWT claims should include an expiration date");
        assertTrue(claims.getExpiration().toInstant().isAfter(claims.getIssuedAt().toInstant()), "The JWT expiration should occur after its issued-at date");
    }

    @Test
    void shouldExtractEmailFromValidToken(){

        String token = jwtUtil.generateToken(USERNAME);

        String email = jwtUtil.extractEmailFromToken(token);

        assertEquals(USERNAME, email, "Email extraction should return the JWT subject");


    }

    @Test
    void shouldReturnFalseWhenTokenIsNotExpired(){

        String token = jwtUtil.generateToken(USERNAME);

        boolean expired = jwtUtil.isTokenExpired(token);

        assertFalse(expired, "A newly generated JWT should not be expired");

    }

    @Test
    void shouldThrowExpiredJwtExceptionWhenTokenIsExpired(){

        String expiredToken = generateExpiredToken();

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtUtil.extractClaims(expiredToken)
        );

    }

    @Test
    void shouldThrowSignatureExceptionWhenTokenWasSignedWithAnotherKey(){

        String token = generateTokenWithAnotherKey();

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

    private String generateExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(SECRET_KEY)
        );

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(JwtUtilTest.USERNAME)
                .issuedAt(Date.from(now.minusSeconds(7_200)))
                .expiration(Date.from(now.minusSeconds(3_600)))
                .signWith(key)
                .compact();
    }

    private String generateTokenWithAnotherKey(){

        SecretKey key =  Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(OTHER_SECRET_KEY)
        );

        Instant now = Instant.now();

        return  Jwts.builder()
                .subject(JwtUtilTest.USERNAME)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3_600)))
                .signWith(key)
                .compact();

    }
}


