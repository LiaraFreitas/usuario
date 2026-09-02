package com.javanauta.user.business.converter;

import com.javanauta.user.business.dto.in.AddressDTORequest;
import com.javanauta.user.business.dto.in.PhoneDTORequest;
import com.javanauta.user.business.dto.in.UserDTORequest;
import com.javanauta.user.business.dto.out.AddressDTOResponse;
import com.javanauta.user.business.dto.out.PhoneDTOResponse;
import com.javanauta.user.business.dto.out.UserDTOResponse;
import com.javanauta.user.infrastructure.entity.Address;
import com.javanauta.user.infrastructure.entity.Phone;
import com.javanauta.user.infrastructure.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConverter {

    public User toUserEntity(UserDTORequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .addresses(request.getAddresses() != null ?
                        toAddressEntityList(request.getAddresses()) : null)
                .phones(request.getPhones() != null ?
                        toPhoneEntityList(request.getPhones()) : null)
                .build();
    }

    public List<Address> toAddressEntityList(List<AddressDTORequest> requests) {
        return requests.stream()
                .map(this::toAddressEntity)
                .toList();
    }

    public Address toAddressEntity(AddressDTORequest request) {
        return Address.builder()
                .street(request.getStreet())
                .number(request.getNumber())
                .city(request.getCity())
                .cep(request.getCep())
                .state(request.getState())
                .complement(request.getComplement())
                .build();
    }

    public List<Phone> toPhoneEntityList(List<PhoneDTORequest> requests) {
        return requests.stream().map(this::toPhoneEntity).toList();
    }

    public Phone toPhoneEntity(PhoneDTORequest request) {
        return Phone.builder()
                .number(request.getNumber())
                .areaCode(request.getAreaCode())
                .build();
    }

    public UserDTOResponse toUserResponse(User userEntity) {
        return UserDTOResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .addresses(userEntity.getAddresses() != null ?
                        toAddressResponseList(userEntity.getAddresses()) : null)
                .phones(userEntity.getPhones() != null ?
                        toPhoneResponseList(userEntity.getPhones()) : null)
                .build();
    }

    public List<AddressDTOResponse> toAddressResponseList(List<Address> responseAddresses) {
        return responseAddresses.stream().map(this::toAddressResponse).toList();
    }

    public AddressDTOResponse toAddressResponse(Address addressEntity) {
        return AddressDTOResponse.builder()
                .street(addressEntity.getStreet())
                .number(addressEntity.getNumber())
                .city(addressEntity.getCity())
                .cep(addressEntity.getCep())
                .state(addressEntity.getState())
                .complement(addressEntity.getComplement())
                .build();
    }

    public List<PhoneDTOResponse> toPhoneResponseList(List<Phone> responsePhones) {
        return responsePhones.stream().map(this::toPhoneResponse).toList();
    }

    public PhoneDTOResponse toPhoneResponse(Phone phoneEntity) {
        return PhoneDTOResponse.builder()
                .number(phoneEntity.getNumber())
                .areaCode(phoneEntity.getAreaCode())
                .build();
    }

    public User updateUser(UserDTORequest userDTORequest, User entity) {
        return User.builder()
                .name(userDTORequest.getName() != null ? userDTORequest.getName() : entity.getName())
                .id(entity.getId())
                .password(userDTORequest.getPassword() != null ? userDTORequest.getPassword() : entity.getPassword())
                .email(userDTORequest.getEmail() != null ? userDTORequest.getEmail() : entity.getEmail())
                .addresses(entity.getAddresses())
                .phones(entity.getPhones())
                .build();
    }

    public Address updateAddress(AddressDTORequest addressDtoRequest, Address addressEntity) {
        return Address.builder()
                .street(addressDtoRequest.getStreet() != null ? addressDtoRequest.getStreet() : addressEntity.getStreet())
                .number(addressDtoRequest.getNumber() != null ? addressDtoRequest.getNumber() : addressEntity.getNumber())
                .city(addressDtoRequest.getCity() != null ? addressDtoRequest.getCity() : addressEntity.getCity())
                .cep(addressDtoRequest.getCep() != null ? addressDtoRequest.getCep() : addressEntity.getCep())
                .complement(addressDtoRequest.getComplement() != null ? addressDtoRequest.getComplement() : addressEntity.getComplement())
                .userId(addressEntity.getUserId())
                .state(addressDtoRequest.getState() != null ? addressDtoRequest.getState() : addressEntity.getState())
                .build();
    }

    public Phone updatePhone(PhoneDTORequest phoneDTORequest, Phone entity) {
        return Phone.builder()
                .id(entity.getId())
                .areaCode(phoneDTORequest.getAreaCode() != null ? phoneDTORequest.getAreaCode() : entity.getAreaCode())
                .number(phoneDTORequest.getNumber() != null ? phoneDTORequest.getNumber() : entity.getNumber())
                .userId(entity.getUserId())
                .build();
    }

    public Address toAddressEntity(AddressDTORequest addressDTORequest, Long userId) {
        return Address.builder()
                .street(addressDTORequest.getStreet())
                .city(addressDTORequest.getCity())
                .cep(addressDTORequest.getCep())
                .complement(addressDTORequest.getComplement())
                .state(addressDTORequest.getState())
                .number(addressDTORequest.getNumber())
                .userId(userId)
                .build();
    }

    public Phone toPhoneEntity(PhoneDTORequest phoneDTORequest, Long userId) {
        return Phone.builder()
                .number(phoneDTORequest.getNumber())
                .areaCode(phoneDTORequest.getAreaCode())
                .userId(userId)
                .build();
    }
}

