package com.javanauta.user.controller;

import com.javanauta.user.business.UsuarioService;
import com.javanauta.user.business.ViaCepService;
import com.javanauta.user.business.dto.EnderecoDTO;
import com.javanauta.user.business.dto.TelefoneDTO;
import com.javanauta.user.business.dto.UsuarioDTO;
import com.javanauta.user.infrastructure.clients.ViaCepDTO;
import com.javanauta.user.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
public class UsuarioController {

    private final UsuarioService userService;
    private final ViaCepService viaCepService;

    @Operation(summary = "Salvar Usuários", description = "Cria um novo usuário")
    @ApiResponse(responseCode = "200", description = "Usuário salvo com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário já cadastrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO userDto) {
        return ResponseEntity.ok(userService.salvaUsuario(userDto));
    }

    @Operation(summary = "Realizar Login", description = "Efetua o login do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsuarioDTO userDto) {
        return ResponseEntity.ok(userService.autenticarUsuario(userDto));
    }

    @Operation(summary = "Busca usuário por email", description = "Busca as informações do usuário por email")
    @ApiResponse(responseCode = "200", description = "Informações encontradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @GetMapping
    public ResponseEntity<UsuarioDTO> buscaUsuarioPorEmail(@RequestParam("email") String emailAddress) {
        return ResponseEntity.ok(userService.buscarUsuarioPorEmail(emailAddress));
    }

    @Operation(summary = "Deleta usuário por email", description = "Deleta o usuário por email")
    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String emailAddress) {
        userService.deletaUsuarioPorEmail(emailAddress);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualiza dados do usuário", description = "Atualiza dados do usuário através do Token")
    @ApiResponse(responseCode = "200", description = "Informações do usuário alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping
    public ResponseEntity<UsuarioDTO> atualizaDadosUsuario(@RequestBody UsuarioDTO userDto,
                                                           @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(userService.atualizaDadosUsuario(authToken, userDto));
    }

    @Operation(summary = "Atualiza dados do endereço", description = "Atualiza dados do endereço do usuário através do ID")
    @ApiResponse(responseCode = "200", description = "Informações do endereço alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping("/endereco")
    public ResponseEntity<EnderecoDTO> atualizaEndereco(@RequestBody EnderecoDTO addressDto,
                                                       @RequestParam("id") Long addressId) {
        return ResponseEntity.ok(userService.atualizaEndereco(addressId, addressDto));
    }

    @Operation(summary = "Atualiza dados do telefone", description = "Atualiza dados do telefone do usuário através do ID")
    @ApiResponse(responseCode = "200", description = "Informações do telefone alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Telefone não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping("/telefone")
    public ResponseEntity<TelefoneDTO> atualizaTelefone(@RequestBody TelefoneDTO phoneDto,
                                                        @RequestParam("id") Long phoneId) {
        return ResponseEntity.ok(userService.atualizaTelefone(phoneId, phoneDto));
    }

    @Operation(summary = "Salvar endereço do usuário", description = "Cria um novo endereço")
    @ApiResponse(responseCode = "200", description = "Endereço salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/endereco")
    public ResponseEntity<EnderecoDTO> cadastraEndereco(@RequestBody EnderecoDTO addressDto,
                                                       @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(userService.cadastraEndereco(authToken, addressDto));
    }

    @Operation(summary = "Salvar telefone do usuário", description = "Cria um novo telefone")
    @ApiResponse(responseCode = "200", description = "Telefone salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/telefone")
    public ResponseEntity<TelefoneDTO> cadastraTelefone(@RequestBody TelefoneDTO phoneDto,
                                                       @RequestHeader("Authorization") String authToken) {
        return ResponseEntity.ok(userService.cadastroTelefone(authToken, phoneDto));
    }

    @Operation(summary = "Busca CEP do usuário", description = "Busca CEP do usuário")
    @ApiResponse(responseCode = "200", description = "Informações encontradas com sucesso")
    @ApiResponse(responseCode = "403", description = "Dados não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @GetMapping("/endereco/{cep}")
    public ResponseEntity<ViaCepDTO> buscarDadosCep(@PathVariable("cep") String postalCode) {
        return ResponseEntity.ok(viaCepService.buscarDadosEndereco(postalCode));
    }
}

