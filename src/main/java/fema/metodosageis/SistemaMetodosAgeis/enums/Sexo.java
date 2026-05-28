package fema.metodosageis.SistemaMetodosAgeis.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Sexo {
   MASCULINO ("Masculino"),
    FEMININO ("Feminino"),
    OUTRO ("Outro");

   private final String sexo;
}
