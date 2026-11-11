package com.sgai.vista;

import com.sgai.controlador.ReporteAcademico;
import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

// Vista previa y paginación carta: el mismo Printable dibuja en pantalla y en impresora.
public final class VentanaReporte extends JDialog {

  private static final long serialVersionUID = 1L;
  private final String nombreTrabajo;
  private final DocumentoImprimible documento;
  private final int paginas;
  private int pagina;
  private final JLabel contador = new JLabel();
  private final VistaHoja hoja;

  public VentanaReporte(Window padre, ReporteAcademico reporte) {
    this(
      padre,
      "Reporte académico · " + reporte.estudiante().getCodigo(),
      "SGAI - " + reporte.estudiante().getCodigo(),
      new DocumentoReporte(reporte)
    );
  }

  public VentanaReporte(
    Window padre,
    String titulo,
    String nombreTrabajo,
    DocumentoImprimible documento
  ) {
    super(padre, titulo, ModalityType.APPLICATION_MODAL);
    this.nombreTrabajo = nombreTrabajo;
    // Este diálogo sirve tanto para notas como para matrícula; cada documento aporta su contenido.
    this.documento = java.util.Objects.requireNonNull(documento);
    paginas = documento.getPaginas();
    hoja = new VistaHoja(documento);
    JPanel acciones = new JPanel(new GridLayout(1, 4, 8, 0));
    acciones.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    contador.setHorizontalAlignment(SwingConstants.CENTER);
    acciones.add(Estilo.boton("Anterior", () -> cambiarPagina(-1)));
    acciones.add(contador);
    acciones.add(Estilo.boton("Siguiente", () -> cambiarPagina(1)));
    acciones.add(Estilo.boton("Imprimir", this::imprimir));
    add(new DesplazamientoReporte(hoja), BorderLayout.CENTER);
    add(acciones, BorderLayout.SOUTH);
    cambiarPagina(0);
    adaptarVentana(true);
    addComponentListener(
      new java.awt.event.ComponentAdapter() {
        @Override
        public void componentResized(java.awt.event.ComponentEvent evento) {
          adaptarVentana(false);
        }

        @Override
        public void componentMoved(java.awt.event.ComponentEvent evento) {
          adaptarVentana(false);
        }
      }
    );
  }

  private void adaptarVentana(boolean centrar) {
    GraphicsConfiguration configuracion = getGraphicsConfiguration();
    Insets bordes = Toolkit.getDefaultToolkit().getScreenInsets(configuracion);
    Rectangle area = DimensionesReporte.areaDisponible(
      configuracion.getBounds(),
      bordes
    );
    setMinimumSize(DimensionesReporte.minimo(area));
    setMaximumSize(DimensionesReporte.maximo(area));
    Rectangle limites = centrar
      ? DimensionesReporte.inicial(area)
      : DimensionesReporte.ajustar(getBounds(), area);
    if (!getBounds().equals(limites)) setBounds(limites);
  }

  private void cambiarPagina(int cambio) {
    pagina = Math.max(0, Math.min(paginas - 1, pagina + cambio));
    contador.setText("Página " + (pagina + 1) + " de " + paginas);
    hoja.mostrarPagina(pagina);
  }

  private void imprimir() {
    PrinterJob trabajo = PrinterJob.getPrinterJob();
    trabajo.setJobName(nombreTrabajo);
    PageFormat formato = trabajo.validatePage(DocumentoReporte.formatoCarta());
    if (formato.getWidth() > formato.getHeight()) formato.setOrientation(
      PageFormat.PORTRAIT
    );
    trabajo.setPrintable(documento, formato);
    javax.print.attribute.PrintRequestAttributeSet atributos =
      new javax.print.attribute.HashPrintRequestAttributeSet();
    atributos.add(javax.print.attribute.standard.MediaSizeName.NA_LETTER);
    atributos.add(javax.print.attribute.standard.OrientationRequested.PORTRAIT);
    if (!trabajo.printDialog(atributos)) return;
    // El snapshot es inmutable: imprimir no lee Swing ni JDBC desde el trabajador.
    SwingWorker<Void, Void> tarea = new SwingWorker<>() {
      @Override
      protected Void doInBackground() throws PrinterException {
        trabajo.print(atributos);
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
        } catch (Exception error) {
          JOptionPane.showMessageDialog(
            VentanaReporte.this,
            "No se pudo imprimir. Revise la impresora y vuelva a intentar.",
            "Impresión",
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    };
    tarea.execute();
  }
}
