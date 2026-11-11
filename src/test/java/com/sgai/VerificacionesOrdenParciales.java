package com.sgai;

import com.sgai.controlador.EstadoAcademico;
import com.sgai.modelo.*;
import java.util.List;

// Una carga desordenada debe verse del primero al cuarto parcial, también al filtrar.
public final class VerificacionesOrdenParciales {

  private VerificacionesOrdenParciales() {}

  public static void verificar() {
    Estudiante alumno = new Estudiante(
      1,
      "ESTU1",
      "Ana",
      "Pérez",
      "ana@colegio.edu.ni",
      7
    );
    Curso curso = new Curso(1, "CURS1", "Matemáticas", 7);
    EstadoAcademico estado = new EstadoAcademico(
      List.of(alumno),
      List.of(),
      List.of(curso),
      List.of(),
      List.of(
        new Nota(4, 1, 1, 1, 4, 60),
        new Nota(2, 1, 1, 1, 2, 90),
        new Nota(3, 1, 1, 1, 3, 45),
        new Nota(1, 1, 1, 1, 1, 75)
      )
    );
    for (String consulta : List.of("", "ESTU1", "Ana")) {
      List<Nota> notas = estado.filtrarNotas(consulta, 0, 0, "");
      if (notas.size() != 4) throw new AssertionError("Faltan notas");
      for (int i = 0; i < 4; i++) if (
        notas.get(i).periodoParcial() != i + 1
      ) throw new AssertionError("Parciales fuera de orden");
    }
    for (int parcial = 1; parcial <= 4; parcial++) {
      List<Nota> notas = estado.filtrarNotas("", 7, parcial, "Matemáticas");
      if (
        notas.size() != 1 || notas.get(0).periodoParcial() != parcial
      ) throw new AssertionError("Filtro incorrecto");
    }
    if (estado.notas()[0].periodoParcial() != 4) throw new AssertionError(
      "Se alteró el almacenamiento original"
    );
  }

  public static void main(String[] argumentos) {
    verificar();
  }
}
