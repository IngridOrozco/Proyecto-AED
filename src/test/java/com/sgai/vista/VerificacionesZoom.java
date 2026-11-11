package com.sgai.vista;

import com.sgai.controlador.ReporteAcademico;
import com.sgai.modelo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

// Eventos reales de rueda y documentos reales; el zoom no modifica el Printable.
public final class VerificacionesZoom {

  private VerificacionesZoom() {}

  private static void comprobar(boolean valor) {
    if (!valor) throw new AssertionError("Regresión del zoom de documentos");
  }

  private static void rueda(
    DesplazamientoReporte panel,
    boolean control,
    int giro
  ) {
    panel.dispatchEvent(
      new MouseWheelEvent(
        panel,
        MouseEvent.MOUSE_WHEEL,
        System.currentTimeMillis(),
        control ? InputEvent.CTRL_DOWN_MASK : 0,
        180,
        150,
        0,
        false,
        MouseWheelEvent.WHEEL_UNIT_SCROLL,
        3,
        giro
      )
    );
  }

  private static BufferedImage imprimir(DocumentoImprimible documento) {
    BufferedImage imagen = new BufferedImage(
      612,
      792,
      BufferedImage.TYPE_INT_RGB
    );
    Graphics2D grafico = imagen.createGraphics();
    try {
      documento.print(grafico, DocumentoReporte.formatoCarta(), 0);
    } catch (java.awt.print.PrinterException error) {
      throw new AssertionError(error);
    } finally {
      grafico.dispose();
    }
    return imagen;
  }

  public static void verificar() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Estudiante alumno = new Estudiante(
        1,
        "ESTU1",
        "Ana",
        "Pérez",
        "ana@colegio.edu.ni",
        7
      );
      List<Curso> cursos = List.of(new Curso(1, "CURS1", "Matemáticas", 7));
      for (DocumentoImprimible documento : List.of(
        new DocumentoReporte(
          new ReporteAcademico(
            alumno,
            cursos,
            List.of(new Nota(1, 1, 1, 1, 1, 59)),
            0
          )
        ),
        new DocumentoMatricula(alumno, cursos)
      )) {
        BufferedImage original = imprimir(documento);
        VistaHoja hoja = new VistaHoja(documento);
        DesplazamientoReporte panel = new DesplazamientoReporte(hoja);
        panel.setSize(600, 500);
        panel.doLayout();
        panel.getViewport().doLayout();
        comprobar(hoja.getZoom() == 1);
        rueda(panel, true, -4);
        comprobar(hoja.getZoom() > 1);
        comprobar(hoja.getHeight() > panel.getViewport().getHeight());
        double zoom = hoja.getZoom();
        Point posicion = panel.getViewport().getViewPosition();
        rueda(panel, false, 2);
        comprobar(hoja.getZoom() == zoom);
        comprobar(panel.getViewport().getViewPosition().y > posicion.y);
        hoja.mostrarPagina(0);
        comprobar(hoja.getZoom() == zoom);
        rueda(panel, true, 4);
        comprobar(hoja.getZoom() == 1);
        comprobar(hoja.getSize().equals(panel.getViewport().getExtentSize()));
        rueda(panel, true, -100);
        comprobar(hoja.getZoom() == 4);
        rueda(panel, true, -100);
        comprobar(hoja.getZoom() == 4);
        rueda(panel, true, 100);
        comprobar(hoja.getZoom() == 0.5);
        rueda(panel, true, 100);
        comprobar(hoja.getZoom() == 0.5);
        BufferedImage despues = imprimir(documento);
        for (int x = 0; x < 612; x++) for (int y = 0; y < 792; y++) comprobar(
          original.getRGB(x, y) == despues.getRGB(x, y)
        );
      }
    });
  }

  public static void main(String[] argumentos) throws Exception {
    verificar();
  }
}
