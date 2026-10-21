package fema.metodosageis.SistemaMetodosAgeis.model;

import fema.metodosageis.SistemaMetodosAgeis.enums.Escolaridade;
import fema.metodosageis.SistemaMetodosAgeis.enums.Experiencia;
import fema.metodosageis.SistemaMetodosAgeis.enums.Sexo;
import fema.metodosageis.SistemaMetodosAgeis.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3)
    private String nome;

    @Min(10)
    @NotNull
    private int idade;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Escolaridade escolaridade;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Experiencia experiencia;

    private String senha;

    @OneToMany(mappedBy = "usuario")
    private List<TentativaProva> tentativasProva;

    @OneToMany(mappedBy = "usuario")
    private List<Resposta> respostas;
}
