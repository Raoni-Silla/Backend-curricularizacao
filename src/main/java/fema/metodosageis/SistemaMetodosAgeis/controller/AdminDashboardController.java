package fema.metodosageis.SistemaMetodosAgeis.controller;

import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.DashboardDTO;
import fema.metodosageis.SistemaMetodosAgeis.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin") // A "tranca" do Security tá aqui!
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> obterDadosDashboard() {
        DashboardDTO dados = dashboardService.calcularDadosGerais();
        return ResponseEntity.ok(dados);
    }
}