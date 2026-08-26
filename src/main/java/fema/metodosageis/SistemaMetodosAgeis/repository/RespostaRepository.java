package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.UsuarioCriadoDTO;
import fema.metodosageis.SistemaMetodosAgeis.model.Prova;
import fema.metodosageis.SistemaMetodosAgeis.model.Resposta;
import fema.metodosageis.SistemaMetodosAgeis.model.TentativaProva;
import fema.metodosageis.SistemaMetodosAgeis.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    long countByTentativaProvaAndCorretaIsTrue(TentativaProva tentativaProva);
}
