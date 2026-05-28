package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    Optional<Usuario> findByNome(String nome);

}
