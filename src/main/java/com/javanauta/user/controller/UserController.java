package com.javanauta.user.controller;

import com.javanauta.user.business.UserService;
import com.javanauta.user.business.ViaCepService;
import com.javanauta.user.business.dto.in.AddressDTORequest;
import com.javanauta.user.business.dto.in.PhoneDTORequest;
import com.javanauta.user.business.dto.in.UserDTORequest;
import com.javanauta.user.business.dto.out.AddressDTOResponse;
import com.javanauta.user.business.dto.out.PhoneDTOResponse;
import com.javanauta.user.business.dto.out.UserDTOResponse;
import com.javanauta.user.infrastructure.clients.ViaCepDTO;
import com.javanauta.user.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Cadastro de Usuários")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UserController {

    private final UserService userService;
    private final ViaCepService viaCepService;

    @Operation(summary = "Salvar Usuários", description = "Cria um novo usuário")
    @ApiResponse(responseCode = "200", description = "Usuário salvo com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário já cadastrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping
    public ResponseEntity<UserDTOResponse> createUser(@RequestBody UserDTORequest userDTORequest) {

        UserDTOResponse userDTOResponse = userService.createUser(userDTORequest);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(userDTOResponse);
    }

    @Operation(summary = "Realizar Login", description = "Efetua o login do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTORequest userDtoRequest) {
        return ResponseEntity.ok(userService.authenticateUser(userDtoRequest));
    }

    @Operation(summary = "Busca usuário por email", description = "Busca as informações do usuário por email")
    @ApiResponse(responseCode = "200", description = "Informações encontradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @GetMapping
    public ResponseEntity<UserDTOResponse> findUserByEmail(@RequestParam("email") String emailAddress) {
        return ResponseEntity.ok(userService.findUserByEmail(emailAddress));
    }

    @Operation(summary = "Deleta usuário por email", description = "Deleta o usuário por email")
    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String emailAddress) {
        userService.deleteUserByEmail(emailAddress);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualiza dados do usuário", description = "Atualiza dados do usuário através do Token")
    @ApiResponse(responseCode = "200", description = "Informações do usuário alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping
    public ResponseEntity<UserDTOResponse> updateUser(@RequestBody UserDTORequest userDTORequest,
                                                      @RequestHeader("Authorization") String authToken) {

        UserDTOResponse userDTOResponse = userService.updateUser(authToken, userDTORequest);


        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(userDTOResponse);


    }

    @Operation(summary = "Atualiza dados do endereço", description = "Atualiza dados do endereço do usuário através do ID")
    @ApiResponse(responseCode = "200", description = "Informações do endereço alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping("/endereco")
    public ResponseEntity<AddressDTOResponse> updateAddress(@RequestBody AddressDTORequest addressDTORequest,
                                                            @RequestParam("id") Long addressId) {


        AddressDTOResponse addressDTOResponse = userService.updateAddress(addressId, addressDTORequest);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(addressDTOResponse);

    }

    @Operation(summary = "Atualiza dados do telefone", description = "Atualiza dados do telefone do usuário através do ID")
    @ApiResponse(responseCode = "200", description = "Informações do telefone alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Telefone não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping("/telefone")
    public ResponseEntity<PhoneDTOResponse> updatePhone(@RequestBody PhoneDTORequest phoneDTORequest,
                                                        @RequestParam("id") Long phoneId) {

        PhoneDTOResponse phoneDTOResponse = userService.updatePhone(phoneId, phoneDTORequest);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(phoneDTOResponse);

    }

    @Operation(summary = "Salvar endereço do usuário", description = "Cria um novo endereço")
    @ApiResponse(responseCode = "200", description = "Endereço salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/endereco")
    public ResponseEntity<AddressDTOResponse> addAddress(@RequestBody AddressDTORequest addressDtoRequest,
                                                         @RequestHeader("Authorization") String authToken) {
      AddressDTOResponse addressDTOResponse = userService.addAddressForUser(authToken, addressDtoRequest);

      return ResponseEntity.
              status(HttpStatus.CREATED)
              .body(addressDTOResponse);

    }

    @Operation(summary = "Salvar telefone do usuário", description = "Cria um novo telefone")
    @ApiResponse(responseCode = "200", description = "Telefone salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/telefone")
    public ResponseEntity<PhoneDTOResponse> addPhone(@RequestBody PhoneDTORequest phoneDtoRequest,
                                                     @RequestHeader("Authorization") String authToken) {
        PhoneDTOResponse phoneDTOResponse = userService.addPhoneForUser(authToken, phoneDtoRequest);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(phoneDTOResponse);
    }

    @Operation(summary = "Busca CEP do usuário", description = "Busca CEP do usuário")
    @ApiResponse(responseCode = "200", description = "Informações encontradas com sucesso")
    @ApiResponse(responseCode = "403", description = "Dados não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @GetMapping("/endereco/{cep}")
    public ResponseEntity<ViaCepDTO> fetchAddressByPostalCode(@PathVariable("cep") String postalCode) {
        return ResponseEntity.ok(viaCepService.fetchAddressByPostalCode(postalCode));
    }
}

