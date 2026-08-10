package com.javanauta.user.business.converter;

import com.javanauta.user.business.dto.AddressDTO;
import com.javanauta.user.business.dto.PhoneDTO;
import com.javanauta.user.business.dto.UserDTO;
import com.javanauta.user.infrastructure.entity.Address;
import com.javanauta.user.infrastructure.entity.Phone;
import com.javanauta.user.infrastructure.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConverter {

    public User toUser(UserDTO userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .addresses(userDto.getAddresses() != null ?
                        toAddressList(userDto.getAddresses()) : null)
                .phones(userDto.getPhones() != null ?
                        toPhoneList(userDto.getPhones()) : null)
                .build();
    }

    public List<Address> toAddressList(List<AddressDTO> addressDtos) {
        return addressDtos.stream()
                .map(this::toAddress)
                .toList();
    }

    public Address toAddress(AddressDTO addressDto) {
        return Address.builder()
                .street(addressDto.getStreet())
                .number(addressDto.getNumber())
                .city(addressDto.getCity())
                .cep(addressDto.getCep())
                .state(addressDto.getState())
                .complement(addressDto.getComplement())
                .build();
    }

    public List<Phone> toPhoneList(List<PhoneDTO> phoneDtos) {
        return phoneDtos.stream().map(this::toPhone).toList();
    }

    public Phone toPhone(PhoneDTO phoneDto) {
        return Phone.builder()
                .number(phoneDto.getNumber())
                .areaCode(phoneDto.getAreaCode())
                .build();
    }

    public UserDTO toUserDTO(User userEntity) {
        return UserDTO.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .addresses(userEntity.getAddresses() != null ?
                        toAddressDTOList(userEntity.getAddresses()) : null)
                .phones(userEntity.getPhones() != null ?
                        toPhoneDTOList(userEntity.getPhones()) : null)
                .build();
    }

    public List<AddressDTO> toAddressDTOList(List<Address> addresses) {
        return addresses.stream().map(this::toAddressDTO).toList();
    }

    public AddressDTO toAddressDTO(Address addressEntity) {
        return AddressDTO.builder()
                .id(addressEntity.getId())
                .street(addressEntity.getStreet())
                .number(addressEntity.getNumber())
                .city(addressEntity.getCity())
                .cep(addressEntity.getCep())
                .state(addressEntity.getState())
                .complement(addressEntity.getComplement())
                .build();
    }

    public List<PhoneDTO> toPhoneDTOList(List<Phone> phones) {
        return phones.stream().map(this::toPhoneDTO).toList();
    }

    public PhoneDTO toPhoneDTO(Phone phoneEntity) {
        return PhoneDTO.builder()
                .id(phoneEntity.getId())
                .number(phoneEntity.getNumber())
                .areaCode(phoneEntity.getAreaCode())
                .build();
    }

    public User updateUser(UserDTO userDto, User entity) {
        return User.builder()
                .name(userDto.getName() != null ? userDto.getName() : entity.getName())
                .id(entity.getId())
                .password(userDto.getPassword() != null ? userDto.getPassword() : entity.getPassword())
                .email(userDto.getEmail() != null ? userDto.getEmail() : entity.getEmail())
                .addresses(entity.getAddresses())
                .phones(entity.getPhones())
                .build();
    }

    public Address updateAddress(AddressDTO addressDto, Address entity) {
        return Address.builder()
                .id(entity.getId())
                .street(addressDto.getStreet() != null ? addressDto.getStreet() : entity.getStreet())
                .number(addressDto.getNumber() != null ? addressDto.getNumber() : entity.getNumber())
                .city(addressDto.getCity() != null ? addressDto.getCity() : entity.getCity())
                .cep(addressDto.getCep() != null ? addressDto.getCep() : entity.getCep())
                .complement(addressDto.getComplement() != null ? addressDto.getComplement() : entity.getComplement())
                .userId(entity.getUserId())
                .state(addressDto.getState() != null ? addressDto.getState() : entity.getState())
                .build();
    }

    public Phone updatePhone(PhoneDTO phoneDto, Phone entity) {
        return Phone.builder()
                .id(entity.getId())
                .areaCode(phoneDto.getAreaCode() != null ? phoneDto.getAreaCode() : entity.getAreaCode())
                .number(phoneDto.getNumber() != null ? phoneDto.getNumber() : entity.getNumber())
                .userId(entity.getUserId())
                .build();
    }

    public Address toAddressEntity(AddressDTO addressDto, Long userId) {
        return Address.builder()
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .cep(addressDto.getCep())
                .complement(addressDto.getComplement())
                .state(addressDto.getState())
                .number(addressDto.getNumber())
                .userId(userId)
                .build();
    }

    public Phone toPhoneEntity(PhoneDTO phoneDto, Long userId) {
        return Phone.builder()
                .number(phoneDto.getNumber())
                .areaCode(phoneDto.getAreaCode())
                .userId(userId)
                .build();
    }
}

