package fema.metodosageis.SistemaMetodosAgeis.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_ID", "pergunta_ID"})
)
public class Resposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean correta;

    private LocalDateTime respondidoEm;

    @ManyToOne
    @JoinColumn(name = "usuario_ID")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pergunta_ID")
    private Pergunta pergunta;

    @ManyToOne
    @JoinColumn(name = "alternativa_id")
    private Alternativa alternativa;

    @ManyToOne
    @JoinColumn(name = "tentativa_ID")
    private TentativaProva tentativaProva;

}
