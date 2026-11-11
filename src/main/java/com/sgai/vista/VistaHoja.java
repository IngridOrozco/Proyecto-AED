package com.sgai.vista;

import java.awt.*;
import javax.swing.*;

// Ajustar una hoja carta completa al viewport, sin estirarla ni modificar la impresión.
final class VistaHoja extends JPanel implements Scrollable {

  private static final long serialVersionUID = 1L;
  private final DocumentoImprimible documento;
  private int pagina;
  private double zoom = 1;
  private double escalaInicial = 1;
  private static final double ZOOM_MINIMO = 0.5,
    ZOOM_MAXIMO = 4;

  VistaHoja(DocumentoImprimible documento) {
    this.documento = java.util.Objects.requireNonNull(documento);
    setBackground(Estilo.FONDO);
    setToolTipText(
      "Ctrl + rueda: ampliar o reducir. Rueda sin Ctrl: desplazar."
    );
  }

  void mostrarPagina(int pagina) {
    this.pagina = pagina;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics grafico) {
    super.paintComponent(grafico);
    Graphics2D copia = (Graphics2D) grafico.create();
    try {
      double escala = escalaActual();
      if (escala <= 0) return;
      copia.translate(
        (getWidth() - 612 * escala) / 2,
        (getHeight() - 792 * escala) / 2
      );
      copia.scale(escala, escala);
      copia.setColor(Color.WHITE);
      copia.fillRect(0, 0, 612, 792);
      documento.dibujar(copia, pagina, 36, 36, 540);
    } finally {
      copia.dispose();
    }
  }

  private double escalaActual() {
    return zoom == 1
      ? Math.min(getWidth() / 612.0, getHeight() / 792.0)
      : escalaInicial * zoom;
  }

  double getZoom() {
    return zoom;
  }

  void ampliar(double giro, Point cursor) {
    if (!(getParent() instanceof JViewport visor) || giro == 0) return;
    double nuevoZoom = Math.max(
      ZOOM_MINIMO,
      Math.min(ZOOM_MAXIMO, zoom * Math.pow(1.12, -giro))
    );
    if (Math.abs(nuevoZoom - 1) < 0.00000001) nuevoZoom = 1;
    if (nuevoZoom == zoom) return;
    double anterior = escalaActual();
    if (anterior <= 0) return;
    if (zoom == 1) escalaInicial = anterior;
    // Conservar el punto de la hoja que el usuario está mirando, no saltar al inicio.
    Point posicion = visor.getViewPosition();
    double puntoX = (cursor.x - (getWidth() - 612 * anterior) / 2) / anterior;
    double puntoY = (cursor.y - (getHeight() - 792 * anterior) / 2) / anterior;
    Point ancla = new Point(cursor.x - posicion.x, cursor.y - posicion.y);
    zoom = nuevoZoom;
    revalidate();
    if (visor.getParent() instanceof JScrollPane panel) {
      panel.revalidate();
      panel.doLayout();
    }
    visor.doLayout();
    double escala = escalaActual();
    int x = (int) Math.round(
      puntoX * escala + (getWidth() - 612 * escala) / 2 - ancla.x
    );
    int y = (int) Math.round(
      puntoY * escala + (getHeight() - 792 * escala) / 2 - ancla.y
    );
    visor.setViewPosition(
      new Point(
        Math.max(0, Math.min(x, getWidth() - visor.getWidth())),
        Math.max(0, Math.min(y, getHeight() - visor.getHeight()))
      )
    );
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    if (zoom == 1) return new Dimension(612, 792);
    return new Dimension(
      (int) Math.ceil(612 * escalaInicial * zoom),
      (int) Math.ceil(792 * escalaInicial * zoom)
    );
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(
    Rectangle visible,
    int orientacion,
    int direccion
  ) {
    return 24;
  }

  @Override
  public int getScrollableBlockIncrement(
    Rectangle visible,
    int orientacion,
    int direccion
  ) {
    return Math.max(
      24,
      (orientacion == SwingConstants.VERTICAL
        ? visible.height
        : visible.width) - 24
    );
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return (
      zoom == 1 ||
      (getParent() instanceof JViewport visor &&
        getPreferredSize().width <= visor.getWidth())
    );
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return (
      zoom == 1 ||
      (getParent() instanceof JViewport visor &&
        getPreferredSize().height <= visor.getHeight())
    );
  }
}
