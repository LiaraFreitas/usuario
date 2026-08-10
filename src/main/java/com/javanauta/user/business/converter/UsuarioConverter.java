package com.javanauta.user.business.converter;

import com.javanauta.user.business.dto.EnderecoDTO;
import com.javanauta.user.business.dto.TelefoneDTO;
import com.javanauta.user.business.dto.UserDTO;
import com.javanauta.user.business.dto.UsuarioDTO;
import com.javanauta.user.infrastructure.entity.Endereco;
import com.javanauta.user.infrastructure.entity.Telefone;
import com.javanauta.user.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioConverter {

    public Usuario toUser(UserDTO userDto) {
        return Usuario.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .addresses(userDto.getAddresses() != null ?
                        toAddressList(userDto.getAddresses()) : null)
                .phones(userDto.getPhones() != null ?
                        toPhoneList(userDto.getPhones()) : null)
                .build();
    }

    public List<Endereco> toAddressList(List<EnderecoDTO> addressDtos) {
        return addressDtos.stream()
                .map(this::toAddress)
                .toList();
    }

    public Endereco toAddress(EnderecoDTO addressDto) {
        return Endereco.builder()
                .street(addressDto.getStreet())
                .number(addressDto.getNumber())
                .city(addressDto.getCity())
                .cep(addressDto.getCep())
                .state(addressDto.getState())
                .complement(addressDto.getComplement())
                .build();
    }

    public List<Telefone> toPhoneList(List<TelefoneDTO> phoneDtos) {
        return phoneDtos.stream().map(this::toPhone).toList();
    }

    public Telefone toPhone(TelefoneDTO phoneDto) {
        return Telefone.builder()
                .number(phoneDto.getNumber())
                .areaCode(phoneDto.getAreaCode())
                .build();
    }

    public UsuarioDTO toUserDTO(Usuario userEntity) {
        return UsuarioDTO.builder()
               .name(userEntity.getName())
               .email(userEntity.getEmail())
               .password(userEntity.getPassword())
               .addresses(userEntity.getAddresses() != null ?
                       toAddressDTOList(userEntity.getAddresses()) : null)
               .phones(userEntity.getPhones() != null ?
                       toPhoneDTOList(userEntity.getPhones()) : null)
               .build();
    }

    public List<EnderecoDTO> toAddressDTOList(List<Endereco> addresses) {
        return addresses.stream().map(this::toAddressDTO).toList();
    }

    public EnderecoDTO toAddressDTO(Endereco addressEntity) {
        return EnderecoDTO.builder()
               .id(addressEntity.getId())
               .street(addressEntity.getStreet())
               .number(addressEntity.getNumber())
               .city(addressEntity.getCity())
               .cep(addressEntity.getCep())
               .state(addressEntity.getState())
               .complement(addressEntity.getComplement())
               .build();
    }

    public List<TelefoneDTO> toPhoneDTOList(List<Telefone> phones) {
        return phones.stream().map(this::toPhoneDTO).toList();
    }

    public TelefoneDTO toPhoneDTO(Telefone phoneEntity) {
        return TelefoneDTO.builder()
               .id(phoneEntity.getId())
               .number(phoneEntity.getNumber())
               .areaCode(phoneEntity.getAreaCode())
               .build();
    }

    public Usuario updateUser(UsuarioDTO userDto, Usuario entity) {
        return Usuario.builder()
               .name(userDto.getName() != null ? userDto.getName() : entity.getName())
               .id(entity.getId())
               .password(userDto.getPassword() != null ? userDto.getPassword() : entity.getPassword())
               .email(userDto.getEmail() != null ? userDto.getEmail() : entity.getEmail())
               .addresses(entity.getAddresses())
               .phones(entity.getPhones())
               .build();
    }

    public Endereco updateAddress(EnderecoDTO addressDto, Endereco entity) {
        return Endereco.builder()
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

    public Telefone updatePhone(TelefoneDTO phoneDto, Telefone entity) {
        return Telefone.builder()
               .id(entity.getId())
               .areaCode(phoneDto.getAreaCode() != null ? phoneDto.getAreaCode() : entity.getAreaCode())
               .number(phoneDto.getNumber() != null ? phoneDto.getNumber() : entity.getNumber())
               .userId(entity.getUserId())
               .build();
    }

    public Endereco toAddressEntity(EnderecoDTO addressDto, Long userId) {
        return Endereco.builder()
               .street(addressDto.getStreet())
               .city(addressDto.getCity())
               .cep(addressDto.getCep())
               .complement(addressDto.getComplement())
               .state(addressDto.getState())
               .number(addressDto.getNumber())
               .userId(userId)
               .build();
    }

    public Telefone toPhoneEntity(TelefoneDTO phoneDto, Long userId) {
        return Telefone.builder()
               .number(phoneDto.getNumber())
               .areaCode(phoneDto.getAreaCode())
               .userId(userId)
               .build();
    }
}

