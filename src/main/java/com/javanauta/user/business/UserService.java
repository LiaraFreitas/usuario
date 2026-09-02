package com.javanauta.user.business;


import com.javanauta.user.business.converter.UserConverter;
import com.javanauta.user.business.dto.in.AddressDTORequest;
import com.javanauta.user.business.dto.in.PhoneDTORequest;
import com.javanauta.user.business.dto.in.UserDTORequest;
import com.javanauta.user.business.dto.out.AddressDTOResponse;
import com.javanauta.user.business.dto.out.PhoneDTOResponse;
import com.javanauta.user.business.dto.out.UserDTOResponse;
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
   public UserDTOResponse createUser(UserDTORequest userDtoRequest) {
       try {
           userDtoRequest.setPassword(passwordEncoder.encode(userDtoRequest.getPassword()));

           User user = userConverter.toUserEntity(userDtoRequest);

           return userConverter.toUserResponse(userRepository.save(user));

       } catch (DataIntegrityViolationException e) {
           if (e.getMessage().contains("email_unique")) {
               throw new ConflictException(REGISTERED_EMAIL + userDtoRequest.getEmail(), e);
           }
           throw e;
       }
   }

   public String authenticateUser(UserDTORequest userDtoRequest) {
       try {
           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(userDtoRequest.getEmail(), userDtoRequest.getPassword())
           );
           return "Bearer " + jwtUtil.generateToken(authentication.getName());
       } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
           throw new UnauthorizedException(INVALID_USERNAME, e.getCause());
       }
   }

   public UserDTOResponse findUserByEmail(String emailAddress) {
       try {
           return userConverter.toUserResponse(
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

   public UserDTOResponse updateUser(String authToken, UserDTORequest userDTORequest) {
       String emailAddress = jwtUtil.extractEmailFromToken(authToken.substring(7));
       userDTORequest.setPassword(userDTORequest.getPassword() != null ? passwordEncoder.encode(userDTORequest.getPassword()) : null);
       User userEntity = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND));
       User user = userConverter.updateUser(userDTORequest, userEntity);
       return userConverter.toUserResponse(userRepository.save(user));
   }

   public AddressDTOResponse updateAddress(Long addressId, AddressDTORequest addressDtoRequest) {
       Address addressEntity = addressRepository.findById(addressId).orElseThrow(() ->
               new ResourceNotFoundException(ID_NOT_FOUND + addressId));

       Address address = userConverter.updateAddress(addressDtoRequest, addressEntity);

       return userConverter.toAddressResponse(addressRepository.save(address));
   }

   public PhoneDTOResponse updatePhone(Long phoneId, PhoneDTORequest phoneDtoRequest) {
       Phone phoneEntity = phoneRepository.findById(phoneId).orElseThrow(() ->
               new ResourceNotFoundException(ID_NOT_FOUND + phoneId));

       Phone phone = userConverter.updatePhone(phoneDtoRequest, phoneEntity);

       return userConverter.toPhoneResponse(phoneRepository.save(phone));
   }

   public AddressDTOResponse addAddressForUser(String authToken, AddressDTORequest addressDtoRequest) {
       String emailAddress = jwtUtil.extractEmailFromToken(authToken.substring(7));
       User user = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress));

       Address address = userConverter.toAddressEntity(addressDtoRequest, user.getId());
       Address addressEntity = addressRepository.save(address);
       return userConverter.toAddressResponse(addressEntity);
   }

   public PhoneDTOResponse addPhoneForUser(String authToken, PhoneDTORequest phoneDtoRequest) {
       String emailAddress = jwtUtil.extractEmailFromToken(authToken.substring(7));
       User user = userRepository.findByEmail(emailAddress).orElseThrow(() ->
               new ResourceNotFoundException(EMAIL_NOT_FOUND + emailAddress));

       Phone phone = userConverter.toPhoneEntity(phoneDtoRequest, user.getId());
       Phone phoneEntity = phoneRepository.save(phone);
       return userConverter.toPhoneResponse(phoneEntity);
   }
}

