package com.javanauta.user.business;

import com.javanauta.user.business.converter.UsuarioConverter;
import com.javanauta.user.business.dto.EnderecoDTO;
import com.javanauta.user.business.dto.TelefoneDTO;
import com.javanauta.user.business.dto.UsuarioDTO;
import com.javanauta.user.infrastructure.entity.Endereco;
import com.javanauta.user.infrastructure.entity.Telefone;
import com.javanauta.user.infrastructure.entity.Usuario;
import com.javanauta.user.infrastructure.exceptions.ConflictException;
import com.javanauta.user.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.user.infrastructure.exceptions.UnauthorizedException;
import com.javanauta.user.infrastructure.repository.EnderecoRepository;
import com.javanauta.user.infrastructure.repository.TelefoneRepository;
import com.javanauta.user.infrastructure.repository.UsuarioRepository;
import com.javanauta.user.infrastructure.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

   private final UsuarioRepository userRepository;
   private final UsuarioConverter userConverter;
   private final PasswordEncoder passwordEncoder;
   private final JwtUtil jwtUtil;
   private final EnderecoRepository addressRepository;
   private final TelefoneRepository phoneRepository;
   private final AuthenticationManager authenticationManager;

   public static final String REGISTERED_EMAIL = "Email já cadastrado ";
   public static final String EMAIL_NOT_FOUND = "Email não encontrado";
   public static final String ID_NOT_FOUND = "ID não encontrado";
   public static final String INVALID_USERNAME = "Usuário ou senha inválida: ";

   @Transactional
   public UsuarioDTO salvaUsuario(UsuarioDTO userDto) {
       try {
           userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
           Usuario user = userConverter.paraUsuario(userDto);
           return userConverter.paraUsuarioDTO(userRepository.save(user));
       } catch (DataIntegrityViolationException e) {
           if (e.getMessage().contains("email_unique")) {
               throw new ConflictException(REGISTERED_EMAIL + userDto.getEmail(), e);
           }
           throw e;
       }
   }

   public String autenticarUsuario(UsuarioDTO userDto) {
       try {
           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
           );
           return "Bearer " + jwtUtil.generateToken(authentication.getName());
       } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
           throw new UnauthorizedException(INVALID_USERNAME, e.getCause());
       }
   }

   public UsuarioDTO buscarUsuarioPorEmail(String emailAddress) {
       try {
           return userConverter.paraUsuarioDTO(
                   userRepository.findByEmail(emailAddress)
                           .orElseThrow(() -> new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress))
           );
       } catch (ResourceNotFoundException e) {
           throw new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress);
       }
   }

   public void deletaUsuarioPorEmail(String emailAddress) {
       userRepository.deleteByEmail(emailAddress);
   }

   public UsuarioDTO atualizaDadosUsuario(String authToken, UsuarioDTO userDto) {
       String emailAddress = jwtUtil.extrairEmailToken(authToken.substring(7));

       userDto.setPassword(userDto.getPassword() != null ? passwordEncoder.encode(userDto.getPassword()) : null);

       Usuario userEntity = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND));

       Usuario user = userConverter.updateUsuario(userDto, userEntity);

       return userConverter.paraUsuarioDTO(userRepository.save(user));
   }

   public EnderecoDTO atualizaEndereco(Long addressId, EnderecoDTO addressDto) {
       Endereco addressEntity = addressRepository.findById(addressId).orElseThrow(() ->
               new ResourceNotFoundException(ID_NOT_FOUND + addressId));

       Endereco address = userConverter.updateEndereco(addressDto, addressEntity);

       return userConverter.paraEnderecoDTO(addressRepository.save(address));
   }

   public TelefoneDTO atualizaTelefone(Long phoneId, TelefoneDTO phoneDto) {
       Telefone phoneEntity = phoneRepository.findById(phoneId).orElseThrow(() ->
               new ResourceNotFoundException(ID_NOT_FOUND + phoneId));

       Telefone phone = userConverter.updateTelefone(phoneDto, phoneEntity);

       return userConverter.paraTelefoneDTO(phoneRepository.save(phone));
   }

   public EnderecoDTO cadastraEndereco(String authToken, EnderecoDTO addressDto) {
       String emailAddress = jwtUtil.extrairEmailToken(authToken.substring(7));
       Usuario user = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress));

       Endereco address = userConverter.paraEnderecoEntity(addressDto, user.getId());
       Endereco addressEntity = addressRepository.save(address);
       return userConverter.paraEnderecoDTO(addressEntity);
   }

   public TelefoneDTO cadastroTelefone(String authToken, TelefoneDTO phoneDto) {
       String emailAddress = jwtUtil.extrairEmailToken(authToken.substring(7));
       Usuario user = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress));

       Telefone phone = userConverter.paraTelefoneEntity(phoneDto, user.getId());
       Telefone phoneEntity = phoneRepository.save(phone);
       return userConverter.paraTelefoneDTO(phoneEntity);
   }
}

