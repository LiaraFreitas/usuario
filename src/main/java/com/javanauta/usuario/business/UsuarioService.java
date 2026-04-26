package com.javanauta.usuario.business;

import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Endereco;
import com.javanauta.usuario.infrastructure.entity.Telefone;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.exceptions.ConflictException;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.infrastructure.exceptions.UnauthorizedException;
import com.javanauta.usuario.infrastructure.repository.EnderecoRepository;
import com.javanauta.usuario.infrastructure.repository.TelefoneRepository;
import com.javanauta.usuario.infrastructure.repository.UsuarioRepository;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

        private final UsuarioRepository usuarioRepository;
        private final UsuarioConverter usuarioConverter;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final EnderecoRepository enderecoRepository;
        private final TelefoneRepository telefoneRepository;
        private final AuthenticationManager authenticationManager;

        public static final String REGISTERED_EMAIL = "Email já cadastrado";
        public static final String EMAIL_NOT_FOUND = "Email não encontrado";
        public static final String ID_NOT_FOUND = "ID não encontrado";
        public static final String INVALID_USERNAME = "Usuário ou senha inválida: ";




        public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
                emailExiste(usuarioDTO.getEmail());
                //criptografa a senha
                usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
                //converte usuarioDTO para usuarioEntity
                Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
                //salva o usuario no BD e o BD retorna usuarioEntity convertemos então para UsuarioDTO
                return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

        }

        public String autenticarUsuario(UsuarioDTO usuarioDTO) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                                        usuarioDTO.getSenha())
                        );
                        //Transforma o email e senha em um Token
                        return "Bearer " + jwtUtil.generateToken(authentication.getName());
                } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
                        throw new UnauthorizedException(INVALID_USERNAME, e.getCause());
                }
        }

        public void emailExiste(String email) {//Verifica se o email existe e gera Excpetion
                try {
                        boolean existe = verificaEmailExistente(email);
                        if (existe) {
                                throw new ConflictException(REGISTERED_EMAIL + email);
                        }
                } catch (ConflictException e) {
                        throw new ConflictException(REGISTERED_EMAIL + e.getCause());
                }
        }

        //Verifica se o email existe no banco de dados e sim retorna o email
        public boolean verificaEmailExistente(String email) {//Apenas chama o metodo da repository
                return usuarioRepository.existsByEmail(email);
        }

        public UsuarioDTO buscarUsuarioPorEmail (String email) {
                try {
                        return usuarioConverter.paraUsuarioDTO(
                                usuarioRepository.findByEmail(email).
                                        orElseThrow(
                                                () -> new ResourceNotFoundException(EMAIL_NOT_FOUND+ email)
                                        )
                        );
                } catch (ResourceNotFoundException e) {
                        throw new ResourceNotFoundException(EMAIL_NOT_FOUND + email);
                }
        }

        public void deletaUsuarioPorEmail(String email) {
                 usuarioRepository.deleteByEmail(email);
        }

        public UsuarioDTO atualizaDadosUsuario(String token,UsuarioDTO dto) {
                //Buscamos o email do usuário através do token (tirar a obrigatoriedade do email)
                String email = jwtUtil.extrairEmailToken(token.substring(7));

                //Se o DTO for <> null então encriptografa a nova senha
                dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

                //Busca os dados do usuário no banco de dados
                Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(EMAIL_NOT_FOUND));

                //Mesclou os dados que recebemos na requisição DTO com os dados do banco de dados
                Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

                //Salvou os dados do usuário convertido e depois pegou o retorno e converteu para UsuarioDTO
                return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

        }


        public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {
                Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow( () ->
                        new ResourceNotFoundException(ID_NOT_FOUND + idEndereco));

                Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO,entity);

                return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
        }

        public TelefoneDTO atualizaTelefone (Long idTelefone, TelefoneDTO dto) {
                Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                        new ResourceNotFoundException(ID_NOT_FOUND + idTelefone));

                Telefone telefone = usuarioConverter.updateTelefone(dto, entity);

                return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
        }

        //Recebe o token e o EnderecoDTO
        public EnderecoDTO cadastraEndereco (String token, EnderecoDTO dto) {
                //Pega o email do usuário, e vai na Usuario Repository buscar os dados do usuário
                String email = jwtUtil.extrairEmailToken(token.substring(7));
                Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                        new ResourceNotFoundException(EMAIL_NOT_FOUND + email));

                //Converte o DTO do endereco com id do usuario em um endereco entitu
                Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
                //Salva endereco entity
                Endereco enderecoEntity = enderecoRepository.save(endereco);
                return  usuarioConverter.paraEnderecoDTO(enderecoEntity);
        }

        public TelefoneDTO cadastroTelefone(String token, TelefoneDTO dto) {
                //Pega o email do usuário, e vai na Usuario Repository buscar os dados do usuário
                String email = jwtUtil.extrairEmailToken(token.substring(7));
                Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                        new ResourceNotFoundException(EMAIL_NOT_FOUND + email));

                //Converte o DTO do telefone com id do usuario em um telefone entity
                Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
                //Salva telefone entity
                Telefone telefoneEntity = telefoneRepository.save(telefone);
                return usuarioConverter.paraTelefoneDTO(telefoneEntity);
        }
}
