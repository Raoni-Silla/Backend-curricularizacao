package fema.metodosageis.SistemaMetodosAgeis.repository;

import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.UsuarioResponseDTO;
import fema.metodosageis.SistemaMetodosAgeis.model.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    List<Resposta> findBytentativaProvaId(Long tentativaProva_id);

    Optional<Resposta> findByTentativaProvaIdAndPerguntaId(Long tentativaID, Long perguntaID);

    long countByCorreta(boolean valor);

    // Busca a Pergunta Vilã (a que tem mais erros)
    @Query(value = "SELECT p.enunciado FROM resposta r JOIN pergunta p ON r.pergunta_id = p.id WHERE r.correta = false GROUP BY p.enunciado ORDER BY COUNT(r) DESC LIMIT 1", nativeQuery = true)
    String buscarPerguntaVila();

    // Busca acertos agrupados por Escolaridade
    @Query("SELECT u.escolaridade, COUNT(r) FROM Resposta r JOIN r.usuario u WHERE r.correta = true GROUP BY u.escolaridade")
    List<Object[]> buscarAcertosPorEscolaridade();

    // Busca acertos agrupados por Sexo
    @Query("SELECT u.sexo, COUNT(r) FROM Resposta r JOIN r.usuario u WHERE r.correta = true GROUP BY u.sexo")
    List<Object[]> buscarAcertosPorSexo();

    // Ranking Top 5 Alunos (por quantidade de acertos)
    @Query("SELECT new fema.metodosageis.SistemaMetodosAgeis.dto.usuario.UsuarioResponseDTO(u.id, u.nome, CAST(COUNT(r) AS int)) " +
            "FROM Resposta r JOIN r.usuario u WHERE r.correta = true GROUP BY u.id, u.nome ORDER BY COUNT(r) DESC LIMIT 5")
    List<UsuarioResponseDTO> buscarTop5Alunos();
}
