package com.ufcg.psoft.project.repository;

import com.ufcg.psoft.project.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Usuario> findByEnderecoContainingIgnoreCaseOrderByNomeAsc(String endereco);
}
