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

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "A resource-not-found exception should return HTTP 404");
        assertNotNull(response.getBody(), "The exception handler should provide an error response body");
        assertEquals(404, response.getBody().getStatus(), "The error body should contain status 404");
        assertEquals("Not Found", response.getBody().getError(), "The resource-not-found error label should be Not Found");
        assertNotNull(response.getBody().getTimestamp(), "The error response should include a timestamp");

    }

    @Test
    void shouldReturnUnauthorizedWhenBadCredentialsExceptionIsThrown(){

        BadCredentialsException exception =
                new BadCredentialsException("Unauthorized");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "An unauthorized exception should return HTTP 401");
        assertNotNull(response.getBody(), "The exception handler should provide an error response body");
        assertEquals(401, response.getBody().getStatus(), "The unauthorized error body should contain status 401");
        assertEquals("Unauthorized", response.getBody().getError(), "The unauthorized error label should be Unauthorized");
        assertNotNull(response.getBody().getTimestamp(), "The error response should include a timestamp");

    }

    @Test
    void shouldReturnConflictWhenConflictExceptionIsThrown(){

        ConflictException exception =
                new ConflictException(REGISTERED_EMAIL);

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleConflictExcpetion(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "A conflict exception should return HTTP 409");
        assertNotNull(response.getBody(), "The exception handler should provide an error response body");
        assertEquals(409, response.getBody().getStatus(), "The conflict error body should contain status 409");
        assertEquals(REGISTERED_EMAIL, response.getBody().getMessage(), "The conflict response should include the registered-email message");
        assertEquals("Conflict", response.getBody().getError(), "The conflict error label should be Conflict");
        assertNotNull(response.getBody().getTimestamp(), "The error response should include a timestamp");

    }


    @Test
    void shouldReturnUnauthorizedWhenUnauthorizedExceptionIsThrown(){

        UnauthorizedException exception =
                new UnauthorizedException(INVALID_USERNAME);

        ResponseEntity<ErrorResponseDTO>response =
                handler.handleUnauthorizedExcpetion(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "An unauthorized exception should return HTTP 401");
        assertNotNull(response.getBody(), "The exception handler should provide an error response body");
        assertEquals(401, response.getBody().getStatus(), "The unauthorized error body should contain status 401");
        assertEquals(INVALID_USERNAME, response.getBody().getMessage(), "The unauthorized response should include the invalid-username message");
        assertEquals("Unauthorized", response.getBody().getError(), "The unauthorized error label should be Unauthorized");
        assertNotNull(response.getBody().getTimestamp(), "The error response should include a timestamp");

    }

    @Test
    void shouldReturnBadRequestWhenInvalidRequestExceptionIsThrown(){

        InvalidRequestException exception =
                new InvalidRequestException("Invalid request");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "An invalid request should return HTTP 400");
        assertNotNull(response.getBody(), "The exception handler should provide an error response body");
        assertEquals(400, response.getBody().getStatus(), "The bad-request error body should contain status 400");
        assertEquals("Invalid request", response.getBody().getMessage(), "The invalid-request response should explain that the request is invalid");
        assertEquals("Bad Request", response.getBody().getError(), "The invalid-request error label should be Bad Request");
        assertNotNull(response.getBody().getTimestamp(), "The error response should include a timestamp");

    }

}
