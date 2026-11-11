package com.sgai.util;

import java.util.regex.Pattern;

// Validaciones centralizadas; las consultas preparadas son la barrera SQL.
public final class Validacion {

  private Validacion() {}

  public static String texto(String valor, String campo, int maximo) {
    if (
      valor == null ||
      valor.strip().isEmpty() ||
      valor.strip().length() > maximo ||
      !Pattern.matches("[\\p{L}\\p{N} .,'’()@_+\\-]+", valor.strip())
    ) throw new ExcepcionNegocio(
      campo + ": texto inválido (máximo " + maximo + ")."
    );
    return valor.strip();
  }

  public static String codigo(String valor) {
    if (
      valor == null || !valor.matches("[A-Za-z0-9_-]{2,24}")
    ) throw new ExcepcionNegocio(
      "Código: use de 2 a 24 letras, números, guion o guion bajo."
    );
    return valor.toUpperCase(java.util.Locale.ROOT);
  }

  public static String correo(String valor) {
    if (
      valor == null ||
      valor.length() > 150 ||
      !valor.matches(
        "[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+"
      )
    ) throw new ExcepcionNegocio("Correo electrónico inválido.");
    return valor;
  }

  public static int rango(int valor, int minimo, int maximo, String campo) {
    if (valor < minimo || valor > maximo) throw new ExcepcionNegocio(
      campo + " fuera de rango."
    );
    return valor;
  }

  public static long identificador(long valor) {
    if (valor <= 0) throw new ExcepcionNegocio(
      "Seleccione un registro válido."
    );
    return valor;
  }

  public static int nota(int valor) {
    return rango(valor, 0, 100, "Nota entera");
  }

  public static int nota(String texto) {
    if (
      texto == null || !texto.matches("[0-9]{1,3}")
    ) throw new ExcepcionNegocio(
      "Ingrese una nota entera entre 0 y 100, sin decimales."
    );
    return nota(Integer.parseInt(texto));
  }
}
