package com.javanauta.user.controller;

import com.javanauta.user.infrastructure.exceptions.ConflictException;
import com.javanauta.user.infrastructure.exceptions.InvalidRequestException;
import com.javanauta.user.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.user.infrastructure.exceptions.UnauthorizedException;
import com.javanauta.user.infrastructure.exceptions.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static com.javanauta.user.business.UserService.INVALID_USERNAME;
import static com.javanauta.user.business.UserService.REGISTERED_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
 class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup(){
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnNotFoundWhenResourceNotFoundExceptionIsThrow(){

        ResourceNotFoundException exception =
                new ResourceNotFoundException("Not Found");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());

    }

    @Test
    void shouldReturnUnauthorizedWhenBadCredentialsExceptionIsThrown(){

        BadCredentialsException exception =
                new BadCredentialsException("Unauthorized");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());

    }

    @Test
    void shouldReturnConflictWhenConflictExceptionIsThrown(){

        ConflictException exception =
                new ConflictException(REGISTERED_EMAIL);

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleConflictExcpetion(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals(REGISTERED_EMAIL, response.getBody().getMessage());
        assertEquals("Conflict", response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());

    }


    @Test
    void shouldReturnUnauthorizedWhenUnauthorizedExceptionIsThrown(){

        UnauthorizedException exception =
                new UnauthorizedException(INVALID_USERNAME);

        ResponseEntity<ErrorResponseDTO>response =
                handler.handleUnauthorizedExcpetion(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals(INVALID_USERNAME, response.getBody().getMessage());
        assertEquals("Unauthorized", response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());

    }

    @Test
    void shouldReturnBadRequestWhenInvalidRequestExceptionIsThrown(){

        InvalidRequestException exception =
                new InvalidRequestException("Invalid request");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid request", response.getBody().getMessage());
        assertEquals("Bad Request", response.getBody().getError());
        assertNotNull(response.getBody().getTimestamp());

    }

}
