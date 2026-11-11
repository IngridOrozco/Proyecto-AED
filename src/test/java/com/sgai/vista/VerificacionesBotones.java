package com.sgai.vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

// Verificar colores reales y navegación sin abrir ventanas ni conectar MySQL.
public final class VerificacionesBotones {

  private VerificacionesBotones() {}

  private static void comprobar(boolean condicion) {
    if (!condicion) throw new AssertionError(
      "Regresión de botones o navegación"
    );
  }

  public static void verificar() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Estilo.instalar();
      for (String texto : List.of(
        "Eliminar",
        "Eliminar matrícula",
        "Eliminar cuenta",
        "Retirar asignación",
        "Cerrar sesión"
      )) {
        JButton boton = Estilo.boton(texto, () -> {});
        comprobar(boton.getBackground().equals(Color.WHITE));
        comprobar(boton.getForeground().equals(Estilo.ROJO));
        comprobar(boton.getIcon() != null);
        boton.setSize(230, 50);
        for (boolean pulsado : new boolean[] { false, true }) {
          boton.getModel().setArmed(pulsado);
          boton.getModel().setPressed(pulsado);
          boton.getModel().setRollover(true);
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
          comprobar(imagen.getRGB(115, 1) == Estilo.ROJO.getRGB());
          comprobar(imagen.getRGB(115, 5) == Estilo.ROJO.getRGB());
          comprobar(imagen.getRGB(115, 44) == Estilo.ROJO.getRGB());
        }
      }
      for (String texto : List.of(
        "Crear cuenta con rol",
        "Restablecer contraseña",
        "Sí",
        "No",
        "Aceptar",
        "Cancelar",
        "Cancel"
      )) {
        JButton boton = Estilo.boton(texto, () -> {});
        comprobar(boton.getBackground().equals(Estilo.PRIMARIO));
        comprobar(boton.getForeground().equals(Color.WHITE));
      }
      JButton salir = Estilo.boton("Cerrar sesión", () -> {});
      comprobar(salir.getBackground().equals(Color.WHITE));
      BufferedImage icono = new BufferedImage(
        20,
        20,
        BufferedImage.TYPE_INT_ARGB
      );
      Graphics2D grafico = icono.createGraphics();
      try {
        salir.getIcon().paintIcon(salir, grafico, 0, 0);
      } finally {
        grafico.dispose();
      }
      boolean rojo = false;
      for (int x = 0; x < 20; x++) for (int y = 0; y < 20; y++) {
        if (icono.getRGB(x, y) == Estilo.ROJO.getRGB()) rojo = true;
      }
      comprobar(rojo);
      JOptionPane aviso = new JOptionPane(
        "Confirmación",
        JOptionPane.QUESTION_MESSAGE,
        JOptionPane.YES_NO_CANCEL_OPTION
      );
      List<JButton> botones = new ArrayList<>();
      recopilar(aviso, botones);
      comprobar(botones.stream().anyMatch(b -> b.getText().equals("Cancelar")));
      for (JButton boton : botones) {
        if (
          boton.getText().equals("No") || boton.getText().equals("Cancelar")
        ) {
          comprobar(boton.getBackground().equals(Estilo.PRIMARIO));
          comprobar(boton.getForeground().equals(Color.WHITE));
          comprobar(boton.getIcon() == null);
        }
      }
      JTabbedPane pestanas = Estilo.pestanas();
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
        pestanas.addTab(titulo, new JPanel());
      }
      comprobar(pestanas.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);
      for (int ancho : new int[] { 600, 900, 1200 }) {
        pestanas.setSize(ancho, 600);
        pestanas.doLayout();
        int altura = pestanas.getBoundsAt(0).y;
        for (int indice = 1; indice < pestanas.getTabCount(); indice++) {
          comprobar(pestanas.getBoundsAt(indice).y == altura);
        }
        pestanas.setSelectedIndex(8);
        pestanas.doLayout();
      }
    });
  }

  private static void recopilar(Container contenedor, List<JButton> botones) {
    for (Component hijo : contenedor.getComponents()) {
      if (hijo instanceof JButton boton) botones.add(boton);
      if (hijo instanceof Container grupo) recopilar(grupo, botones);
    }
  }

  public static void main(String[] argumentos) throws Exception {
    verificar();
  }
}
