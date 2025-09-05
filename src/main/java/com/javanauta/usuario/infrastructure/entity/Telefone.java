package com.javanauta.usuario.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "telefone")
public class Telefone {
    //Cria e entidade telefone no banco de dados e faz a dependência da tabela usuário

    @Id
    //Gera o ID de forma automática

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero" , length = 10)
    private String numero;
    @Column(name = "ddd", length = 3)
    private String ddd;

}
