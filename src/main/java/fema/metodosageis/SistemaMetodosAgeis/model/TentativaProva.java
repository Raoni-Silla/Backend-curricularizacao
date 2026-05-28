package fema.metodosageis.SistemaMetodosAgeis.model;

import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
public class TentativaProva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    @ManyToOne
    @JoinColumn(name = "prova_ID")
    private Prova prova;

    @ManyToOne
    @JoinColumn(name = "usuario_ID")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusProva status;

}
