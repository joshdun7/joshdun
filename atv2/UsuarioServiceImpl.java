package com.ufcg.psoft.project.service.usuario;

import com.ufcg.psoft.project.exception.UsuarioNaoExisteException;
import com.ufcg.psoft.project.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.project.model.HistoricoAlteracao;
import com.ufcg.psoft.project.model.Usuario;
import com.ufcg.psoft.project.repository.HistoricoAlteracaoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import com.ufcg.psoft.project.dto.UsuarioPostPutRequestDTO;
import com.ufcg.psoft.project.dto.UsuarioResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    HistoricoAlteracaoRepository historicoAlteracaoRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public UsuarioResponseDTO alterar(Long id, String codigoAcesso, UsuarioPostPutRequestDTO usuarioPostPutRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(UsuarioNaoExisteException::new);
        if (!usuario.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        String codigoAnterior = usuario.getCodigo();
        String enderecoAnterior = usuario.getEndereco();

        modelMapper.map(usuarioPostPutRequestDTO, usuario);
        usuarioRepository.save(usuario);

        registrarAlteracao(usuario, "codigo", codigoAnterior, usuario.getCodigo());
        registrarAlteracao(usuario, "endereco", enderecoAnterior, usuario.getEndereco());

        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    private void registrarAlteracao(Usuario usuario, String campo, String valorAnterior, String novoValor) {
        if (!Objects.equals(valorAnterior, novoValor)) {
            historicoAlteracaoRepository.save(HistoricoAlteracao.builder()
                    .usuario(usuario)
                    .campo(campo)
                    .valorAnterior(valorAnterior)
                    .novoValor(novoValor)
                    .dataHora(LocalDateTime.now())
                    .build());
        }
    }

    @Override
    public UsuarioResponseDTO criar(UsuarioPostPutRequestDTO usuarioPostPutRequestDTO) {
        Usuario usuario = modelMapper.map(usuarioPostPutRequestDTO, Usuario.class);
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codigoAcesso) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(UsuarioNaoExisteException::new);
        if (!usuario.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
        usuarioRepository.delete(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return converterParaDTO(usuarios);
    }

    @Override
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        List<Usuario> usuarios = usuarioRepository
                .findByNomeContainingIgnoreCaseOrderByNomeAsc(nome);
        return converterParaDTO(usuarios);
    }

    @Override
    public List<UsuarioResponseDTO> buscarPorEndereco(String endereco) {
        List<Usuario> usuarios = usuarioRepository
                .findByEnderecoContainingIgnoreCaseOrderByNomeAsc(endereco);
        return converterParaDTO(usuarios);
    }

    @Override
    public List<HistoricoAlteracao> listarHistorico() {
        return historicoAlteracaoRepository.findAllByOrderByDataHoraAscIdAsc();
    }

    @Override
    public List<HistoricoAlteracao> listarHistorico(Long usuarioId) {
        return historicoAlteracaoRepository.findByUsuarioIdOrderByDataHoraAscIdAsc(usuarioId);
    }

    private List<UsuarioResponseDTO> converterParaDTO(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO recuperar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(UsuarioNaoExisteException::new);
        return new UsuarioResponseDTO(usuario);
    }
}
