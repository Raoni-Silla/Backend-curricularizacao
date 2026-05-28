package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.model.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {
}
