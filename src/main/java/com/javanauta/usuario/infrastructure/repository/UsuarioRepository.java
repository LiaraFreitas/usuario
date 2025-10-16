package com.javanauta.usuario.infrastructure.repository;

import com.javanauta.usuario.infrastructure.entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Para cada entity criada eu preciso ter uma repository

    //Valida se o email existe no BD
    boolean existsByEmail(String email);

    //Serve para evitar o retorno de informações nulas, ele trata o retorno nulo.
    //Irá buscar o email e caso não retornar irá gerar em uma exceção na UserDetailsServiceImpl
    Optional<Usuario> findByEmail(String email);

    @Transactional
     void deleteByEmail(String email);

}
