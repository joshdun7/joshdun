package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.HistoricoAlteracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoAlteracaoRepository extends JpaRepository<HistoricoAlteracao, Long> {

    List<HistoricoAlteracao> findByUsuarioIdOrderByDataHoraAscIdAsc(Long usuarioId);

    List<HistoricoAlteracao> findAllByOrderByDataHoraAscIdAsc();
}
