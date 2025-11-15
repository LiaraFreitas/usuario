package com.javanauta.usuario.controller;

import com.javanauta.usuario.business.UsuarioService;
import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import com.javanauta.usuario.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Cadastro de Usuários")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UsuarioController {


    private final UsuarioService usuarioService;
    //Realizada a injeção de dependência
    private final AuthenticationManager authenticationManager;
    //Realizada a injeção de dependência
    private final JwtUtil jwtUtil;


    @Operation(summary = "Salvar Usuários", description = "Cria um novo usuário")
    @ApiResponse(responseCode = "200", description = "Usuário salvo com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário já cadastrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    //Metodo criado para salvar usuário
    @PostMapping
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    @Operation(summary = "Realizar Login", description = "Efetua o login do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    //Metodo criado para realizar login, rebece o email,senha para realizar a autenticação
    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuarioDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                        usuarioDTO.getSenha())
        );
        //Transforma o email e senha em um Token
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }


    @Operation(summary = "Busca usuário por email", description = "Busca as informações do usuário por email")
    @ApiResponse(responseCode = "200", description = "Informações encontradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    //Metodo reponsável por buscar as informações do usuário ao passar o email
    @GetMapping
    public ResponseEntity<UsuarioDTO> buscaUsuarioPorEmail(@RequestParam("email") String email) {
        return  ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @Operation(summary = "Deleta usuário por email", description = "Deleta o usuário por email")
    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualiza dados do usuário", description = "Atualiza dados do usuário através do Token")
    @ApiResponse(responseCode = "200", description = "Informações do usuário alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping
    public ResponseEntity<UsuarioDTO> atualizaDadosUsuario (@RequestBody UsuarioDTO dto, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token,dto));
    }

    @Operation(summary = "Atualiza dados do endereço", description = "Atualiza dados do endereço do usuário através do ID")
    @ApiResponse(responseCode = "200", description = "Informações do endereço alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping("/endereco")
    public ResponseEntity<EnderecoDTO>atualizaEndereco(@RequestBody EnderecoDTO dto,
                                                       @RequestParam("id") Long id) {
        return ResponseEntity.ok(usuarioService.atualizaEndereco(id, dto));
    }

    @Operation(summary = "Atualiza dados do telefone", description = "Atualiza dados do telefone do usuário através do ID")
    @ApiResponse(responseCode = "200", description = "Informações do telefone alteradas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autorizado")
    @ApiResponse(responseCode = "403", description = "Telefone não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PutMapping("/telefone")
    public ResponseEntity<TelefoneDTO>atualizaTelefone(@RequestBody TelefoneDTO dto,
                                                       @RequestParam("id") Long id) {
        return ResponseEntity.ok(usuarioService.atualizaTelefone(id, dto));
    }

    @Operation(summary = "Salvar endereço do usuário", description = "Cria um novo endereço")
    @ApiResponse(responseCode = "200", description = "Endereço salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/endereco")
    public ResponseEntity<EnderecoDTO>cadastraEndereco(@RequestBody EnderecoDTO dto,
                                                        @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.cadastraEndereco(token, dto));
    }

    @Operation(summary = "Salvar telefone do usuário", description = "Cria um novo telefone")
    @ApiResponse(responseCode = "200", description = "Telefone salvo com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    @PostMapping("/telefone")
    public ResponseEntity<TelefoneDTO>cadastraTelefone(@RequestBody TelefoneDTO dto,
                                                       @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usuarioService.cadastroTelefone(token, dto));
    }
}
