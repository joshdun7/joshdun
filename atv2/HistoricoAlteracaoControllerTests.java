package com.ufcg.psoft.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.project.dto.UsuarioPostPutRequestDTO;
import com.ufcg.psoft.project.model.Usuario;
import com.ufcg.psoft.project.repository.HistoricoAlteracaoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Historico de alteracoes de usuarios")
class HistoricoAlteracaoControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    HistoricoAlteracaoRepository historicoAlteracaoRepository;

    Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = usuarioRepository.save(Usuario.builder()
                .nome("Usuario")
                .endereco("Endereco antigo")
                .codigo("123456")
                .build());
    }

    @AfterEach
    void tearDown() {
        historicoAlteracaoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Registra mudancas de codigo e endereco e ignora dados identicos")
    void registraSomenteAlteracoes() throws Exception {
        alterarUsuario("Endereco novo", "654321", "123456");

        driver.perform(get("/historico/usuario/{id}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuario.id").value(usuario.getId()))
                .andExpect(jsonPath("$[0].campo").value("codigo"))
                .andExpect(jsonPath("$[0].valorAnterior").value("123456"))
                .andExpect(jsonPath("$[0].novoValor").value("654321"))
                .andExpect(jsonPath("$[0].dataHora").exists())
                .andExpect(jsonPath("$[1].campo").value("endereco"));

        alterarUsuario("Endereco novo", "654321", "654321");

        driver.perform(get("/historico/usuario/{id}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Consulta somente o historico do usuario informado em ordem cronologica")
    void consultaHistoricoDoUsuario() throws Exception {
        alterarUsuario("Primeiro endereco", "123456", "123456");
        alterarUsuario("Segundo endereco", "123456", "123456");

        Usuario outroUsuario = usuarioRepository.save(Usuario.builder()
                .nome("Outro usuario")
                .endereco("Endereco")
                .codigo("111111")
                .build());
        alterarUsuario(outroUsuario, "Outro endereco", "111111", "111111");

        driver.perform(get("/historico/usuario/{id}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].novoValor").value("Primeiro endereco"))
                .andExpect(jsonPath("$[1].novoValor").value("Segundo endereco"));
    }

    @Test
    @DisplayName("Consulta o historico geral e retorna lista vazia quando nao ha registros")
    void consultaHistoricoGeral() throws Exception {
        driver.perform(get("/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        alterarUsuario("Primeiro endereco", "123456", "123456");
        alterarUsuario("Segundo endereco", "123456", "123456");

        driver.perform(get("/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].novoValor").value("Primeiro endereco"))
                .andExpect(jsonPath("$[1].novoValor").value("Segundo endereco"));
    }

    private void alterarUsuario(String endereco, String novoCodigo, String codigoAtual) throws Exception {
        alterarUsuario(usuario, endereco, novoCodigo, codigoAtual);
    }

    private void alterarUsuario(Usuario usuarioAlterado, String endereco,
                                String novoCodigo, String codigoAtual) throws Exception {
        UsuarioPostPutRequestDTO dados = UsuarioPostPutRequestDTO.builder()
                .nome(usuarioAlterado.getNome())
                .endereco(endereco)
                .codigo(novoCodigo)
                .build();

        driver.perform(put("/usuario/{id}", usuarioAlterado.getId())
                        .param("codigo", codigoAtual)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk());
    }
}
