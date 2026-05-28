package fema.metodosageis.SistemaMetodosAgeis.controller;

import fema.metodosageis.SistemaMetodosAgeis.dto.prova.IniciarProvaDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.prova.ResultadoProvaDTO;
import fema.metodosageis.SistemaMetodosAgeis.service.TentativaProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tentativas")
@RequiredArgsConstructor
public class TentativaProvaController {

    private final TentativaProvaService tentativaProvaService;

    @PostMapping(path = "/iniciar")
    public ResponseEntity<Long> iniciarProva (@RequestBody IniciarProvaDTO iniciar){
        Long id = tentativaProvaService.iniciarProva(iniciar);
        return ResponseEntity.ok(id);
    }

    @PostMapping(path = "/salvar")
    public ResponseEntity<Void> salvarResposta (@RequestParam Long tentativaID, @RequestParam Long perguntaID, @RequestParam Long alternativaID){
        tentativaProvaService.salvarResposta(tentativaID,perguntaID, alternativaID);
        return ResponseEntity.ok().build();
    }


    @PostMapping(path = "{id}/finalizar")
    public ResponseEntity<ResultadoProvaDTO> finalizarProva (@PathVariable Long id){
        ResultadoProvaDTO resultado = tentativaProvaService.finalizarProva(id);
        return ResponseEntity.ok(resultado);
    }

}
