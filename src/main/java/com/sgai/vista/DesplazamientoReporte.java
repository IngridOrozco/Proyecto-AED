package com.sgai.vista;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import javax.swing.*;

// Interceptar Ctrl antes de que Swing procese la rueda como desplazamiento normal.
final class DesplazamientoReporte extends JScrollPane {

  private static final long serialVersionUID = 1L;
  private final VistaHoja hoja;

  DesplazamientoReporte(VistaHoja hoja) {
    super(hoja);
    this.hoja = hoja;
  }

  @Override
  protected void processMouseWheelEvent(MouseWheelEvent evento) {
    if (evento.isControlDown()) {
      Point cursor = SwingUtilities.convertPoint(this, evento.getPoint(), hoja);
      hoja.ampliar(evento.getPreciseWheelRotation(), cursor);
      evento.consume();
    } else super.processMouseWheelEvent(evento);
  }
}
