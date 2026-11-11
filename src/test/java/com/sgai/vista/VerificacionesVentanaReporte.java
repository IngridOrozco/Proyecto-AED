package com.sgai.vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.util.List;
import javax.swing.*;

// Las cuentas de tamaño y el ajuste de hoja se prueban sin abrir ventanas.
public final class VerificacionesVentanaReporte {

  private VerificacionesVentanaReporte() {}

  private static void comprobar(boolean condicion) {
    if (!condicion) throw new AssertionError(
      "La vista previa no cabe en el área disponible"
    );
  }

  public static void dimensiones() {
    for (Rectangle pantalla : List.of(
      new Rectangle(0, 0, 1920, 1080),
      new Rectangle(0, 0, 1366, 768),
      new Rectangle(0, 0, 800, 600),
      new Rectangle(0, 0, 640, 480),
      new Rectangle(-1920, 0, 1920, 1080),
      new Rectangle(1920, -200, 2560, 1440)
    )) {
      Rectangle area = DimensionesReporte.areaDisponible(
        pantalla,
        new Insets(0, 0, 48, 0)
      );
      Rectangle ventana = DimensionesReporte.inicial(area);
      comprobar(area.contains(ventana));
      comprobar(ventana.width <= 900 && ventana.height <= 850);
      comprobar(ventana.width >= Math.min(600, area.width));
      comprobar(ventana.height >= Math.min(550, area.height));
      comprobar(Math.abs(ventana.getCenterX() - area.getCenterX()) <= 1);
      comprobar(Math.abs(ventana.getCenterY() - area.getCenterY()) <= 1);
      Rectangle manual = DimensionesReporte.ajustar(
        new Rectangle(-5000, -5000, 5000, 5000),
        area
      );
      comprobar(area.contains(manual));
      comprobar(manual.width <= 900 && manual.height <= 850);
    }
    comprobar(
      DimensionesReporte.inicial(new Rectangle(0, 0, 1366, 768))
        .getSize()
        .equals(new Dimension(720, 720))
    );
  }

  public static void hojaCompleta() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      DocumentoImprimible documento = new DocumentoImprimible() {
        @Override
        public int getPaginas() {
          return 2;
        }

        @Override
        public int print(Graphics g, PageFormat formato, int pagina) {
          return PAGE_EXISTS;
        }

        @Override
        public void dibujar(Graphics2D g, int pagina, int x, int y, int ancho) {
          g.setColor(Color.RED);
          g.fillRect(x, y, ancho, 12);
          g.setColor(Color.BLUE);
          g.fillRect(x, y + 700, ancho, 12);
        }
      };
      VistaHoja hoja = new VistaHoja(documento);
      JScrollPane desplazamiento = new JScrollPane(hoja);
      for (Dimension tamano : List.of(
        new Dimension(540, 430),
        new Dimension(850, 720)
      )) {
        desplazamiento.setSize(tamano);
        desplazamiento.doLayout();
        desplazamiento.getViewport().doLayout();
        comprobar(
          hoja.getSize().equals(desplazamiento.getViewport().getExtentSize())
        );
        BufferedImage imagen = new BufferedImage(
          hoja.getWidth(),
          hoja.getHeight(),
          BufferedImage.TYPE_INT_RGB
        );
        Graphics2D grafico = imagen.createGraphics();
        try {
          hoja.paint(grafico);
        } finally {
          grafico.dispose();
        }
        boolean arriba = false,
          abajo = false;
        for (int y = 0; y < imagen.getHeight(); y++) {
          int color = imagen.getRGB(imagen.getWidth() / 2, y);
          arriba |= color == Color.RED.getRGB();
          abajo |= color == Color.BLUE.getRGB();
        }
        comprobar(arriba && abajo);
      }
    });
  }

  public static void main(String[] argumentos) throws Exception {
    dimensiones();
    hojaCompleta();
  }
}
