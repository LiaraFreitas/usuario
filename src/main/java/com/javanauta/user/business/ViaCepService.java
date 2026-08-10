package com.javanauta.user.business;

import com.javanauta.user.infrastructure.clients.ViaCepClient;
import com.javanauta.user.infrastructure.clients.ViaCepDTO;
import com.javanauta.user.infrastructure.exceptions.IllegalArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient client;

    public ViaCepDTO buscarDadosEndereco(String postalCode) {
        return client.buscaDadosEndereco(processarCep(postalCode));
    }

    private String processarCep(String postalCode) {
        String formattedPostalCode = postalCode.replace(" ", "").replace("-", "");

        if (!formattedPostalCode.matches("\\d+") || !Objects.equals(formattedPostalCode.length(), 8)) {
            throw new IllegalArgumentException("O CEP contém caracteres inválidos, por favor verificar");
        }
        return formattedPostalCode;
    }
}


