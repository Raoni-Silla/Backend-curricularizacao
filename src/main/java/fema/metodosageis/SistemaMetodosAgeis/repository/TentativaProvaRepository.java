package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import fema.metodosageis.SistemaMetodosAgeis.enums.TipoUsuario;
import fema.metodosageis.SistemaMetodosAgeis.model.Prova;
import fema.metodosageis.SistemaMetodosAgeis.model.TentativaProva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TentativaProvaRepository extends JpaRepository<TentativaProva, Long> {
    // Taxa de Abandono
    Long countByStatus(StatusProva status); // StatusProva.EXPIRADA
    List<TentativaProva> findByProvaAndUsuario_TipoUsuario(Prova prova, TipoUsuario tipoUsuario);
    List<TentativaProva> findByProvaAndStatus(Prova prova, StatusProva status);
}
