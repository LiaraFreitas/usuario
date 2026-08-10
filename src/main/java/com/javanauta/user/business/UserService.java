package com.javanauta.user.business;


import com.javanauta.user.business.converter.UserConverter;
import com.javanauta.user.business.dto.AddressDTO;
import com.javanauta.user.business.dto.PhoneDTO;
import com.javanauta.user.business.dto.UserDTO;
import com.javanauta.user.infrastructure.entity.Address;
import com.javanauta.user.infrastructure.entity.Phone;
import com.javanauta.user.infrastructure.entity.User;
import com.javanauta.user.infrastructure.exceptions.ConflictException;
import com.javanauta.user.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.user.infrastructure.exceptions.UnauthorizedException;
import com.javanauta.user.infrastructure.repository.AddressRepository;
import com.javanauta.user.infrastructure.repository.PhoneRepository;
import com.javanauta.user.infrastructure.repository.UserRepository;
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
public class UserService {

   private final UserRepository userRepository;
   private final UserConverter userConverter;
   private final PasswordEncoder passwordEncoder;
   private final JwtUtil jwtUtil;
   private final AddressRepository addressRepository;
   private final PhoneRepository phoneRepository;
   private final AuthenticationManager authenticationManager;

   public static final String REGISTERED_EMAIL = "Email já cadastrado ";
   public static final String EMAIL_NOT_FOUND = "Email não encontrado";
   public static final String ID_NOT_FOUND = "ID não encontrado";
   public static final String INVALID_USERNAME = "Usuário ou senha inválida: ";

   @Transactional
   public UserDTO createUser(UserDTO userDto) {
       try {
           userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
           User user = userConverter.toUser(userDto);
           return userConverter.toUserDTO(userRepository.save(user));
       } catch (DataIntegrityViolationException e) {
           if (e.getMessage().contains("email_unique")) {
               throw new ConflictException(REGISTERED_EMAIL + userDto.getEmail(), e);
           }
           throw e;
       }
   }

   public String authenticateUser(UserDTO userDto) {
       try {
           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
           );
           return "Bearer " + jwtUtil.generateToken(authentication.getName());
       } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
           throw new UnauthorizedException(INVALID_USERNAME, e.getCause());
       }
   }

   public UserDTO findUserByEmail(String emailAddress) {
       try {
           return userConverter.toUserDTO(
                   userRepository.findByEmail(emailAddress)
                           .orElseThrow(() -> new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress))
           );
       } catch (ResourceNotFoundException e) {
           throw new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress);
       }
   }

   public void deleteUserByEmail(String emailAddress) {
       userRepository.deleteByEmail(emailAddress);
   }

   public UserDTO updateUser(String authToken, UserDTO userDto) {
       String emailAddress = jwtUtil.extractEmailFromToken(authToken.substring(7));

       userDto.setPassword(userDto.getPassword() != null ? passwordEncoder.encode(userDto.getPassword()) : null);

       User userEntity = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND));

       User user = userConverter.updateUser(userDto, userEntity);

       return userConverter.toUserDTO(userRepository.save(user));
   }

   public AddressDTO updateAddress(Long addressId, AddressDTO addressDto) {
       Address addressEntity = addressRepository.findById(addressId).orElseThrow(() ->
               new ResourceNotFoundException(ID_NOT_FOUND + addressId));

       Address address = userConverter.updateAddress(addressDto, addressEntity);

       return userConverter.toAddressDTO(addressRepository.save(address));
   }

   public PhoneDTO updatePhone(Long phoneId, PhoneDTO phoneDto) {
       Phone phoneEntity = phoneRepository.findById(phoneId).orElseThrow(() ->
               new ResourceNotFoundException(ID_NOT_FOUND + phoneId));

       Phone phone = userConverter.updatePhone(phoneDto, phoneEntity);

       return userConverter.toPhoneDTO(phoneRepository.save(phone));
   }

   public AddressDTO addAddressForUser(String authToken, AddressDTO addressDto) {
       String emailAddress = jwtUtil.extractEmailFromToken(authToken.substring(7));
       User user = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress));

       Address address = userConverter.toAddressEntity(addressDto, user.getId());
       Address addressEntity = addressRepository.save(address);
       return userConverter.toAddressDTO(addressEntity);
   }

   public PhoneDTO addPhoneForUser(String authToken, PhoneDTO phoneDto) {
       String emailAddress = jwtUtil.extractEmailFromToken(authToken.substring(7));
       User user = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress));

       Phone phone = userConverter.toPhoneEntity(phoneDto, user.getId());
       Phone phoneEntity = phoneRepository.save(phone);
       return userConverter.toPhoneDTO(phoneEntity);
   }
}

