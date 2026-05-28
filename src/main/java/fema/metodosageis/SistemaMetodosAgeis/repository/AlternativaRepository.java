package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.model.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {
}
