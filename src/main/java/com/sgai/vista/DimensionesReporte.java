package com.sgai.vista;

import java.awt.*;

// Cálculos separados de la ventana para comprobar también pantallas pequeñas sin escritorio.
final class DimensionesReporte {

  private DimensionesReporte() {}

  static Rectangle areaDisponible(Rectangle pantalla, Insets bordes) {
    int ancho = Math.max(1, pantalla.width - bordes.left - bordes.right);
    int alto = Math.max(1, pantalla.height - bordes.top - bordes.bottom);
    int margen = Math.min(16, Math.max(0, Math.min(ancho, alto) / 20));
    return new Rectangle(
      pantalla.x + bordes.left + margen,
      pantalla.y + bordes.top + margen,
      Math.max(1, ancho - margen * 2),
      Math.max(1, alto - margen * 2)
    );
  }

  static Dimension minimo(Rectangle area) {
    // Si el monitor es menor al mínimo recomendado, prima no dejar controles fuera de pantalla.
    return new Dimension(Math.min(600, area.width), Math.min(550, area.height));
  }

  static Dimension maximo(Rectangle area) {
    return new Dimension(Math.min(900, area.width), Math.min(850, area.height));
  }

  static Rectangle ajustar(Rectangle ventana, Rectangle area) {
    Dimension minimo = minimo(area),
      maximo = maximo(area);
    int ancho = Math.max(minimo.width, Math.min(maximo.width, ventana.width));
    int alto = Math.max(minimo.height, Math.min(maximo.height, ventana.height));
    return new Rectangle(
      Math.max(area.x, Math.min(ventana.x, area.x + area.width - ancho)),
      Math.max(area.y, Math.min(ventana.y, area.y + area.height - alto)),
      ancho,
      alto
    );
  }

  static Rectangle inicial(Rectangle area) {
    double escala = Math.min(area.width / 1366.0, area.height / 768.0);
    int referencia = (int) Math.round(720 * escala);
    Rectangle ventana = ajustar(
      new Rectangle(area.x, area.y, referencia, referencia),
      area
    );
    ventana.setLocation(
      area.x + (area.width - ventana.width) / 2,
      area.y + (area.height - ventana.height) / 2
    );
    return ventana;
  }
}
