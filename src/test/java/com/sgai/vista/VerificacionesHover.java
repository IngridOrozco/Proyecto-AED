package com.sgai.vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

// Comprobar el color pintado sin alterar los colores base ni la acción del botón.
public final class VerificacionesHover {

  private VerificacionesHover() {}

  private static void comprobar(boolean valor) {
    if (!valor) throw new AssertionError("Regresión de hover o iconos");
  }

  private static BufferedImage pintar(JButton boton) {
    boton.setSize(230, 50);
    BufferedImage imagen = new BufferedImage(
      230,
      50,
      BufferedImage.TYPE_INT_RGB
    );
    Graphics2D grafico = imagen.createGraphics();
    try {
      boton.paint(grafico);
    } finally {
      grafico.dispose();
    }
    return imagen;
  }

  public static void verificar() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Estilo.instalar();
      for (String texto : List.of(
        "Buscar",
        "Sí",
        "No",
        "Aceptar",
        "Cancelar",
        "Eliminar",
        "Cerrar sesión"
      )) {
        JButton boton = Estilo.boton(texto, () -> {});
        boolean rojo = Estilo.ROJO.equals(boton.getForeground());
        comprobar(boton.isRolloverEnabled());
        Color fondo = boton.getBackground(),
          frente = boton.getForeground();
        comprobar(pintar(boton).getRGB(115, 5) == fondo.getRGB());
        boton.getModel().setRollover(true);
        comprobar(
          pintar(boton).getRGB(115, 5) ==
            (rojo ? Estilo.ROJO : Estilo.AZUL_HOVER).getRGB()
        );
        comprobar(
          boton.getBackground().equals(fondo) &&
            boton.getForeground().equals(frente)
        );
        if (rojo) {
          BufferedImage icono = new BufferedImage(
            20,
            20,
            BufferedImage.TYPE_INT_ARGB
          );
          Graphics2D grafico = icono.createGraphics();
          try {
            boton.getIcon().paintIcon(boton, grafico, 0, 0);
          } finally {
            grafico.dispose();
          }
          boolean blanco = false;
          for (int x = 0; x < 20; x++) for (int y = 0; y < 20; y++) blanco |=
            icono.getRGB(x, y) == Color.WHITE.getRGB();
          comprobar(blanco);
        }
        boton.getModel().setRollover(false);
        comprobar(pintar(boton).getRGB(115, 5) == fondo.getRGB());
      }
      for (String titulo : List.of(
        "Inicio",
        "Estudiantes",
        "Matrícula",
        "Docentes",
        "Cursos",
        "Asignaciones",
        "Notas",
        "Reportes",
        "Cuentas"
      )) {
        Icon icono = new IconoPestana(titulo);
        for (Color color : List.of(Estilo.AZUL, Color.WHITE)) {
          JLabel etiqueta = new JLabel();
          etiqueta.setForeground(color);
          BufferedImage imagen = new BufferedImage(
            20,
            20,
            BufferedImage.TYPE_INT_ARGB
          );
          Graphics2D grafico = imagen.createGraphics();
          try {
            icono.paintIcon(etiqueta, grafico, 0, 0);
          } finally {
            grafico.dispose();
          }
          boolean visible = false;
          for (int x = 0; x < 20; x++) for (int y = 0; y < 20; y++) visible |=
            imagen.getRGB(x, y) == color.getRGB();
          comprobar(visible);
        }
      }
    });
  }

  public static void main(String[] argumentos) throws Exception {
    verificar();
  }
}
