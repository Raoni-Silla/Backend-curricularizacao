package fema.metodosageis.SistemaMetodosAgeis.controller;

import fema.metodosageis.SistemaMetodosAgeis.dto.pergunta.PerguntaDTO;
import fema.metodosageis.SistemaMetodosAgeis.service.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provas")
@RequiredArgsConstructor
public class ProvaController {
    private final ProvaService provaService;

    @GetMapping(path = "/pegarperguntas/{idprova}")
    public ResponseEntity <List<PerguntaDTO>> obterProva (@PathVariable Long idprova){
        List<PerguntaDTO> perguntaDTOS = provaService.buscarProvaParaAluno(idprova);
        return ResponseEntity.ok(perguntaDTOS);
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativarProva (@PathVariable Long id){
        provaService.ativarProva(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarProva (@PathVariable Long id){
        provaService.desativarProva(id);
        return ResponseEntity.ok().build();
    }

}
