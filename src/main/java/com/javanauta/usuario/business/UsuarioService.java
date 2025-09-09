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
import com.javanauta.usuario.infrastructure.repository.EnderecoRepository;
import com.javanauta.usuario.infrastructure.repository.TelefoneRepository;
import com.javanauta.usuario.infrastructure.repository.UsuarioRepository;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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

        public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
                emailExiste(usuarioDTO.getEmail());
                //criptografa a senha
                usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
                //converte usuarioDTO para usuarioEntity
                Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
                //salva o usuario no BD e o BD retorna usuarioEntity convertemos então para UsuarioDTO
                return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));


        }

        public void emailExiste(String email) {//Verifica se o email existe e gera Excpetion
                try {
                        boolean existe = verificaEmailExistente(email);
                        if (existe) {
                                throw new ConflictException("Email já cadastrado" + email);
                        }
                } catch (ConflictException e) {
                        throw new ConflictException("Email já cadastrado" + e.getCause());
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
                                                () -> new ResourceNotFoundException("Email não encontrado" + email)
                                        )
                        );
                } catch (ResourceNotFoundException e) {
                        throw new ResourceNotFoundException("Email não encontrado" + email);
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
                new ResourceNotFoundException("Email não localizado"));

                //Mesclou os dados que recebemos na requisição DTO com os dados do banco de dados
                Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

                //Salvou os dados do usuário convertido e depois pegou o retorno e converteu para UsuarioDTO
                return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

        }


        public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {
                Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow( () ->
                        new ResourceNotFoundException("ID não encontrado" + idEndereco));

                Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO,entity);

                return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
        }

        public TelefoneDTO atualizaTelefone (Long idTelefone, TelefoneDTO dto) {
                Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                        new ResourceNotFoundException("ID não encontrado" + idTelefone));

                Telefone telefone = usuarioConverter.updateTelefone(dto, entity);

                return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
        }
}
