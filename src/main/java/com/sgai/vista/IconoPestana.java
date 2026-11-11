package com.sgai.vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.*;

// Siluetas Font Awesome Solid de la referencia; máscaras de alta resolución para HiDPI.
final class IconoPestana implements Icon {

  private static final Map<String, String> ARCHIVOS = Map.of(
    "Inicio",
    "house",
    "Estudiantes",
    "user-group",
    "Matrícula",
    "graduation-cap",
    "Docentes",
    "book-open",
    "Cursos",
    "graduation-cap",
    "Asignaciones",
    "gear",
    "Notas",
    "star",
    "Reportes",
    "file-lines",
    "Cuentas",
    "chart-column"
  );
  private final Image imagen;

  IconoPestana(String titulo) {
    imagen = new ImageIcon(
      java.util.Objects.requireNonNull(
        IconoPestana.class.getResource(
          "/iconos/" + ARCHIVOS.get(titulo) + ".png"
        )
      )
    ).getImage();
  }

  @Override
  public int getIconWidth() {
    return 20;
  }

  @Override
  public int getIconHeight() {
    return 20;
  }

  @Override
  public void paintIcon(Component componente, Graphics grafico, int x, int y) {
    BufferedImage mascara = new BufferedImage(
      80,
      80,
      BufferedImage.TYPE_INT_ARGB
    );
    Graphics2D copia = mascara.createGraphics();
    try {
      copia.drawImage(imagen, 0, 0, null);
      copia.setComposite(AlphaComposite.SrcIn);
      copia.setColor(
        componente.isEnabled() ? componente.getForeground() : Color.GRAY
      );
      copia.fillRect(0, 0, 80, 80);
    } finally {
      copia.dispose();
    }
    Graphics2D destino = (Graphics2D) grafico.create();
    try {
      destino.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BICUBIC
      );
      destino.drawImage(mascara, x, y, 20, 20, null);
    } finally {
      destino.dispose();
    }
  }
}
