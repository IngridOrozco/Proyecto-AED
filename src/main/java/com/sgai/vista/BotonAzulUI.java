package com.sgai.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

// También se aplica a los botones de JOptionPane para mantener los avisos coherentes.
public final class BotonAzulUI extends BasicButtonUI {

  private java.beans.PropertyChangeListener cambios;

  public static ComponentUI createUI(JComponent componente) {
    return new BotonAzulUI();
  }

  @Override
  public void installUI(JComponent componente) {
    super.installUI(componente);
    AbstractButton boton = (AbstractButton) componente;
    boton.setOpaque(false);
    boton.setContentAreaFilled(false);
    boton.setRolloverEnabled(true);
    boton.setBorderPainted(false);
    boton.setFocusPainted(false);
    cambios = evento -> {
      if ("text".equals(evento.getPropertyName())) Estilo.aplicarAccion(boton);
    };
    boton.addPropertyChangeListener(cambios);
    Estilo.aplicarAccion(boton);
  }

  @Override
  public void uninstallUI(JComponent componente) {
    if (cambios != null) componente.removePropertyChangeListener(cambios);
    super.uninstallUI(componente);
  }

  @Override
  protected void paintText(
    Graphics grafico,
    AbstractButton boton,
    Rectangle rectangulo,
    String texto
  ) {
    grafico.setFont(boton.getFont());
    grafico.setColor(
      boton.isEnabled()
        ? Estilo.ROJO.equals(boton.getForeground()) &&
          boton.getModel().isRollover()
          ? Color.WHITE
          : boton.getForeground()
        : new Color(75, 88, 105)
    );
    FontMetrics medidas = grafico.getFontMetrics();
    grafico.drawString(texto, rectangulo.x, rectangulo.y + medidas.getAscent());
  }

  @Override
  public void paint(Graphics grafico, JComponent componente) {
    AbstractButton boton = (AbstractButton) componente;
    Graphics2D copia = (Graphics2D) grafico.create();
    try {
      copia.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      );
      boolean sobre = boton.isEnabled() && boton.getModel().isRollover();
      boolean bordeRojo = Estilo.ROJO.equals(boton.getForeground());
      Color fondo = boton.isEnabled()
        ? boton.getBackground()
        : new Color(210, 220, 234);
      if (sobre) {
        if (bordeRojo) fondo = Estilo.ROJO;
        else if (!Color.WHITE.equals(fondo)) fondo = Estilo.AZUL_HOVER;
      }
      // Mantener rellenos sólidos también durante el hover.
      copia.setColor(fondo);
      copia.fillRoundRect(
        1,
        1,
        componente.getWidth() - 3,
        componente.getHeight() - 3,
        16,
        16
      );
      copia.setColor(
        bordeRojo
          ? Estilo.ROJO
          : boton.hasFocus()
            ? Estilo.AZUL
            : fondo.darker()
      );
      copia.drawRoundRect(
        1,
        1,
        componente.getWidth() - 3,
        componente.getHeight() - 3,
        16,
        16
      );
    } finally {
      copia.dispose();
    }
    super.paint(grafico, componente);
  }
}
