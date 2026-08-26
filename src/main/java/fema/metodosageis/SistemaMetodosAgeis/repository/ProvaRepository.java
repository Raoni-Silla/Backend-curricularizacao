package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.model.Pergunta;
import fema.metodosageis.SistemaMetodosAgeis.model.Prova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvaRepository extends JpaRepository<Prova, Long> {
    boolean existsByPerguntas(List<Pergunta> perguntas);
}
