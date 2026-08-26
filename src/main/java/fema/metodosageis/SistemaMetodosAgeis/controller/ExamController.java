package fema.metodosageis.SistemaMetodosAgeis.controller;

import fema.metodosageis.SistemaMetodosAgeis.dto.prova.FinalizarProvaDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.prova.ProvaDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.CriarUsuarioDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.UsuarioCriadoDTO;
import fema.metodosageis.SistemaMetodosAgeis.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService service;

    @PostMapping(path = "/iniciarProva")
    public ResponseEntity<UsuarioCriadoDTO>iniciarProva(@Valid @RequestBody CriarUsuarioDTO dto){
        UsuarioCriadoDTO responseDto = service.iniciarProva(dto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @PreAuthorize("hasRole('ALUNO')")
    @GetMapping(path = "/carregarProva/{idTentativa}")
    public ResponseEntity<ProvaDTO> carregarProva(@PathVariable(name = "idTentativa") Long idTentativa) {
        ProvaDTO dto = service.carregarProva(idTentativa);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/provaAtiva")
    public ResponseEntity<Boolean> isProvaAtiva (){
        boolean isProvaAtiva = service.isProvaAtiva();
        return ResponseEntity.ok(isProvaAtiva);
    }

    @PreAuthorize("hasRole('ALUNO')")
    @PatchMapping(path = "/finalizarProva")
    public ResponseEntity<Long> finalizarProva (@Valid @RequestBody  FinalizarProvaDTO dto){
        Long acertos = service.finalizarProva(dto);
        return ResponseEntity.ok(acertos);
    }

}
