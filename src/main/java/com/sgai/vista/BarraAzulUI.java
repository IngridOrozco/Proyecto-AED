package com.sgai.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

// Pintura plana para todos los JScrollPane, tanto verticales como horizontales.
public final class BarraAzulUI extends BasicScrollBarUI {

  public static ComponentUI createUI(JComponent componente) {
    return new BarraAzulUI();
  }

  @Override
  protected void paintTrack(
    Graphics grafico,
    JComponent componente,
    Rectangle area
  ) {
    grafico.setColor(new Color(232, 240, 251));
    grafico.fillRect(area.x, area.y, area.width, area.height);
  }

  @Override
  protected void paintThumb(
    Graphics grafico,
    JComponent componente,
    Rectangle area
  ) {
    if (area.isEmpty()) return;
    grafico.setColor(
      componente.isEnabled() ? Estilo.PRIMARIO : new Color(150, 174, 205)
    );
    grafico.fillRect(
      area.x + 2,
      area.y + 2,
      Math.max(1, area.width - 4),
      Math.max(1, area.height - 4)
    );
  }

  @Override
  protected JButton createDecreaseButton(int direccion) {
    return flecha(direccion);
  }

  @Override
  protected JButton createIncreaseButton(int direccion) {
    return flecha(direccion);
  }

  private JButton flecha(int direccion) {
    return new javax.swing.plaf.basic.BasicArrowButton(
      direccion,
      new Color(232, 240, 251),
      Estilo.PRIMARIO,
      Estilo.PRIMARIO,
      new Color(232, 240, 251)
    );
  }
}
