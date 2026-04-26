package com.javanauta.usuario.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column (name = "usuario_id")
    private Long usuarioID;

}
