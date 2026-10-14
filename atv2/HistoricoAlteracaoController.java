package com.ufcg.psoft.project.controller;

import com.ufcg.psoft.project.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historico")
public class HistoricoAlteracaoController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> listarHistorico() {
        return ResponseEntity.ok(usuarioService.listarHistorico());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarHistoricoDoUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(usuarioService.listarHistorico(usuarioId));
    }
}
