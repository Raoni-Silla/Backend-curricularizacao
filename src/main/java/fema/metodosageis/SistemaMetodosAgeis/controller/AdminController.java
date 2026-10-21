package fema.metodosageis.SistemaMetodosAgeis.controller;

import fema.metodosageis.SistemaMetodosAgeis.dto.Admin.LoginAdminRequest;
import fema.metodosageis.SistemaMetodosAgeis.dto.Admin.LoginAdminResponseDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.PrincipalDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.service.AdminService;
import fema.metodosageis.SistemaMetodosAgeis.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping(path = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final RelatorioService relatorioService;

    @PostMapping(path = "/autenticar")
    public ResponseEntity<LoginAdminResponseDTO> autenticar (@RequestBody LoginAdminRequest dto){
        LoginAdminResponseDTO respondeDto = adminService.autenticar(dto);
        return ResponseEntity.ok(respondeDto);
    }


    @GetMapping(path = "/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrincipalDashBoardDTO> getDashboard(){
        PrincipalDashBoardDTO dto = adminService.carregarDashboard();
        return ResponseEntity.ok(dto);
    }

    @PatchMapping(path = "/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrincipalDashBoardDTO> ativarProva (){
        PrincipalDashBoardDTO dto = adminService.ativarProva();
        return ResponseEntity.ok(dto);
    }


    @PatchMapping(path = "/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrincipalDashBoardDTO> desativarProva (){
        PrincipalDashBoardDTO dto = adminService.desativarProva();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/relatorio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> gerarRelatorio() {
        byte[] pdf = relatorioService.gerarRelatorioProva();
        String nomeArquivo = "relatorio-prova-" + LocalDate.now(ZoneId.of("America/Sao_Paulo")) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nomeArquivo).build().toString())
                .body(pdf);
    }
}
