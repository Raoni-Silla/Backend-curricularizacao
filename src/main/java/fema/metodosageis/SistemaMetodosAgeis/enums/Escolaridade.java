package fema.metodosageis.SistemaMetodosAgeis.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Escolaridade {
    FUNDAMENTAL ("Fundamental"),
    MEDIO ("Medio"),
    SUPERIOR ("Superior");
    private final String nivelEscolaridade;
}
