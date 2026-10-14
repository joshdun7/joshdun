// src/test/java/com/ufcg/psoft/project/controller/UsuarioTestAula.java

package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.model.Usuario;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Buscas de usuarios")
class UsuarioTestAula {

    private static final String URI_USUARIOS = "/usuario";

    @Autowired
    MockMvc driver;

    @Autowired
    UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
        usuarioRepository.saveAll(List.of(
                Usuario.builder()
                        .nome("Usuario")
                        .endereco("Rua 123")
                        .codigo("123456")
                        .build(),
                Usuario.builder()
                        .nome("Usuaria")
                        .endereco("RUA 234")
                        .codigo("123456")
                        .build(),
                Usuario.builder()
                        .nome("Beltrano")
                        .endereco("Avenida Central")
                        .codigo("123456")
                        .build()
        ));
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Busca por parte do nome sem diferenciar maiusculas e em ordem alfabetica")
    void buscaUsuariosPorNome() throws Exception {
        driver.perform(get(URI_USUARIOS).param("nome", "uSuA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Usuaria"))
                .andExpect(jsonPath("$[1].nome").value("Usuario"));
    }

    @Test
    @DisplayName("Busca por parte do endereco sem diferenciar maiusculas e em ordem alfabetica")
    void buscaUsuariosPorEndereco() throws Exception {
        driver.perform(get(URI_USUARIOS).param("endereco", "rua"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Usuaria"))
                .andExpect(jsonPath("$[1].nome").value("Usuario"));
    }

    @Test
    @DisplayName("Busca sem correspondencias retorna lista vazia")
    void buscaUsuariosSemCorrespondencias() throws Exception {
        driver.perform(get(URI_USUARIOS).param("nome", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
