package com.javanauta.user.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private String name;
    private String email;
    private String password;
    private List<EnderecoDTO> addresses;
    private List<TelefoneDTO> phones;
}

