package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import fema.metodosageis.SistemaMetodosAgeis.model.TentativaProva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TentativaProvaRepository extends JpaRepository<TentativaProva, Long> {
    // Taxa de Abandono
    Long countByStatus(StatusProva status); // StatusProva.EXPIRADA
}
