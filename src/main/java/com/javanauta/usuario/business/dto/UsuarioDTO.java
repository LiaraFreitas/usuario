package com.javanauta.usuario.business.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder //Conversor de dados
public class UsuarioDTO {
//São o meio de campo de entrada e saída de dados
//É usado para os expor os dados de uma forma que seja possível filtrar o que não
//deve ser exposto para os clientes, o servidor irá filtrar e expor apenas o que é importante
//Podem ser tanto de Request quando recebem os dados ou como Response quando eles respondem os dados

    private String nome;
    private String email;
    private String senha;
    private List<EnderecoDTO> enderecos;
    private List<TelefoneDTO> telefones;
}
