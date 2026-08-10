package com.javanauta.user.infrastructure.entity;

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
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rua")
    private String street;
    @Column(name = "numero")
    private Long number;
    @Column(name = "complemento", length = 10)
    private String complement;
    @Column(name = "cidade", length = 150)
    private String city;
    @Column(name = "estado", length = 2)
    private String state;
    @Column(name = "cep", length = 9)
    private String cep;
    @Column (name = "usuario_id")
    private Long userId;
}

