package com.javanauta.user.infrastructure.repository;

import com.javanauta.user.infrastructure.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Serve para evitar o retorno de informações nulas, ele trata o retorno nulo.
    Optional<User> findByEmail(String email);

    @Transactional
     void deleteByEmail(String email);

}

