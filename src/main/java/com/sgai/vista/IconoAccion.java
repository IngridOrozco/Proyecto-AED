package com.sgai.vista;

import java.awt.*;
import javax.swing.Icon;

// Iconos vectoriales que conservan nitidez sin depender de fuentes o archivos externos.
public final class IconoAccion implements Icon {

  private final String accion;
  private final Color color;

  public IconoAccion(String accion) {
    this(accion, null);
  }

  public IconoAccion(String accion, Color color) {
    this.accion = accion.toLowerCase(java.util.Locale.ROOT);
    this.color = color;
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
    Graphics2D copia = (Graphics2D) grafico.create();
    try {
      copia.translate(x, y);
      copia.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      );
      boolean sobreRojo =
        componente instanceof javax.swing.AbstractButton boton &&
        boton.getModel().isRollover() &&
        Estilo.ROJO.equals(boton.getForeground());
      copia.setColor(
        componente.isEnabled() && sobreRojo
          ? Color.WHITE
          : componente.isEnabled()
            ? color == null
              ? componente.getForeground()
              : color
            : Color.GRAY
      );
      copia.setStroke(
        new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
      );
      if (accion.equals("ver todos")) {
        java.awt.geom.Path2D embudo = new java.awt.geom.Path2D.Double();
        embudo.moveTo(2, 3);
        embudo.lineTo(18, 3);
        embudo.lineTo(12, 10);
        embudo.lineTo(12, 16);
        embudo.lineTo(8, 18);
        embudo.lineTo(8, 10);
        embudo.closePath();
        copia.fill(embudo);
      } else if (accion.contains("buscar")) {
        copia.drawOval(2, 2, 10, 10);
        copia.drawLine(11, 11, 18, 18);
      } else if (accion.contains("eliminar")) {
        copia.drawLine(3, 5, 17, 5);
        copia.drawLine(7, 2, 13, 2);
        copia.drawRect(5, 6, 10, 12);
        copia.drawLine(9, 9, 9, 15);
        copia.drawLine(12, 9, 12, 15);
      } else if (
        accion.contains("nuevo") ||
        accion.contains("crear") ||
        accion.equals("matricular")
      ) {
        copia.drawLine(10, 3, 10, 17);
        copia.drawLine(3, 10, 17, 10);
      } else if (accion.contains("actualizar")) {
        copia.drawArc(3, 3, 14, 14, 40, 290);
        copia.drawLine(17, 2, 17, 7);
        copia.drawLine(12, 7, 17, 7);
      } else if (accion.contains("sesión")) {
        copia.drawLine(10, 2, 10, 9);
        copia.drawArc(3, 3, 14, 14, 130, 280);
      } else if (accion.contains("editar")) {
        copia.drawLine(4, 16, 15, 5);
        copia.drawLine(7, 18, 18, 7);
        copia.drawLine(4, 16, 3, 19);
        copia.drawLine(3, 19, 7, 18);
      } else {
        copia.drawRoundRect(4, 2, 12, 16, 3, 3);
        copia.drawLine(7, 7, 13, 7);
        copia.drawLine(7, 11, 13, 11);
      }
    } finally {
      copia.dispose();
    }
  }
}
