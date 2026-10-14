package com.ufcg.psoft.project.service.usuario;

import com.ufcg.psoft.project.dto.UsuarioPostPutRequestDTO;
import com.ufcg.psoft.project.dto.UsuarioResponseDTO;
import com.ufcg.psoft.project.model.HistoricoAlteracao;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO alterar(Long id, String codigoAcesso, UsuarioPostPutRequestDTO usuarioPostPutRequestDTO);

    List<UsuarioResponseDTO> listar();

    List<UsuarioResponseDTO> buscarPorNome(String nome);

    List<UsuarioResponseDTO> buscarPorEndereco(String endereco);

    List<HistoricoAlteracao> listarHistorico();

    List<HistoricoAlteracao> listarHistorico(Long usuarioId);

    UsuarioResponseDTO recuperar(Long id);

    UsuarioResponseDTO criar(UsuarioPostPutRequestDTO usuarioPostPutRequestDTO);

    void remover(Long id, String codigoAcesso);

}
