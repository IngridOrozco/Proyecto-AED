package com.sgai;

import com.sgai.controlador.*;
import com.sgai.modelo.*;
import java.math.BigDecimal;
import java.util.List;

// El filtro de un parcial no debe cambiar el promedio del curso ni el promedio global.
public final class VerificacionesPromedioCurso {

  private VerificacionesPromedioCurso() {}

  private static void comprobar(boolean valor) {
    if (!valor) throw new AssertionError("Regresión del promedio por curso");
  }

  public static void verificar() {
    Estudiante alumno = new Estudiante(
      1,
      "ESTU1",
      "Ana",
      "Pérez",
      "ana@colegio.edu.ni",
      7
    );
    Curso completo = new Curso(1, "CURS1", "Matemáticas", 7);
    Curso incompleto = new Curso(2, "CURS2", "Lengua y Literatura", 7);
    List<Nota> notas = List.of(
      new Nota(1, 1, 1, 1, 1, 50),
      new Nota(2, 1, 1, 1, 2, 60),
      new Nota(3, 1, 1, 1, 3, 70),
      new Nota(4, 1, 1, 1, 4, 81),
      new Nota(5, 1, 2, 1, 1, 90),
      new Nota(6, 2, 1, 1, 1, 100)
    );
    EstadoAcademico estado = new EstadoAcademico(
      List.of(alumno),
      List.of(),
      List.of(completo, incompleto),
      List.of(),
      notas
    );
    comprobar(new BigDecimal("65.25").equals(estado.promedioCurso(1, 1)));
    comprobar(estado.promedioCurso(1, 2) == null);
    comprobar(estado.promedioCurso(2, 1) == null);
    comprobar(estado.promedioCurso(99, 99) == null);
    for (int parcial = 0; parcial <= 4; parcial++) {
      ReporteAcademico reporte = new ReporteAcademico(
        alumno,
        List.of(completo, incompleto),
        notas,
        parcial
      );
      comprobar(new BigDecimal("70.20").equals(reporte.promedio()));
      for (ReporteAcademico.Fila fila : reporte.filas()) {
        comprobar(
          java.util.Objects.equals(
            estado.promedioCurso(1, fila.curso().id()),
            fila.promedioFinal()
          )
        );
      }
      estado.filtrarNotas("ESTU1", 7, parcial, "");
      comprobar(new BigDecimal("65.25").equals(estado.promedioCurso(1, 1)));
    }
  }

  public static void main(String[] argumentos) {
    verificar();
  }
}
