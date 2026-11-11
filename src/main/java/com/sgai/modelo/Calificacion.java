package com.sgai.modelo;

import com.sgai.util.Validacion;

// Nota entera de un estudiante, curso y período parcial.
public record Calificacion(
  long id,
  long estudianteId,
  long cursoId,
  long docenteId,
  int periodoParcial,
  int nota
) {
  public Calificacion {
    if (id < 0) throw new IllegalArgumentException("Identidad negativa");
    Validacion.identificador(estudianteId);
    Validacion.identificador(cursoId);
    Validacion.identificador(docenteId);
    Validacion.rango(periodoParcial, 1, 4, "Período parcial");
    nota = Validacion.nota(nota);
  }
}
