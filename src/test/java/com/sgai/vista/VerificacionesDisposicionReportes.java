package com.sgai.vista;

import java.awt.*;
import javax.swing.*;

public final class VerificacionesDisposicionReportes {

  private VerificacionesDisposicionReportes() {}

  private static void comprobar(boolean valor) {
    if (!valor) throw new AssertionError("Distribución de reportes incorrecta");
  }

  public static void verificar() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Estilo.instalar();
      JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
      for (String texto : new String[] {
        "Buscar",
        "Ver todos",
        "Generar reportes",
      })
        acciones.add(Estilo.boton(texto, () -> {}));
      JTextField estudiante = Formulario.texto("", 100),
        curso = Formulario.texto("", 80);
      JSpinner grado = Formulario.numero(0, 0, 11),
        parcial = Formulario.numero(0, 0, 4);
      JPanel filtros = DisposicionReportes.filtros(
        estudiante,
        curso,
        grado,
        parcial,
        acciones
      );
      TablaDatos<String> alumnos = new TablaDatos<>(
        new String[] { "Estudiante" },
        v -> new Object[] { v }
      );
      TablaDatos<String> notas = new TablaDatos<>(
        new String[] { "Nota" },
        v -> new Object[] { v }
      );
      JSplitPane tablas = DisposicionReportes.tablas(alumnos, notas);
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(filtros, BorderLayout.NORTH);
      panel.add(tablas, BorderLayout.CENTER);
      panel.setSize(864, 480);
      distribuir(panel);
      comprobar(estudiante.getParent().getX() < curso.getParent().getX());
      comprobar(curso.getParent().getX() < grado.getParent().getX());
      comprobar(grado.getParent().getX() < parcial.getParent().getX());
      comprobar(acciones.getY() >= filtros.getComponent(0).getHeight());
      for (Component componente : tablas.getComponents()) {
        if (componente instanceof JScrollPane scroll) {
          comprobar(scroll.getViewport().getExtentSize().height >= 4 * 30);
          comprobar(
            scroll.getVerticalScrollBar().getUI() instanceof BarraAzulUI
          );
        }
      }
      for (String texto : new String[] { "Sí", "No", "Aceptar", "Cancelar" })
        comprobar(Estilo.boton(texto, () -> {}).getIcon() == null);
      comprobar(
        Estilo.boton("Cerrar sesión", () -> {})
          .getForeground()
          .equals(Estilo.ROJO)
      );
    });
  }

  private static void distribuir(Container componente) {
    componente.doLayout();
    for (Component hijo : componente.getComponents())
      if (hijo instanceof Container contenedor) distribuir(contenedor);
  }

  public static void main(String[] argumentos) throws Exception {
    verificar();
  }
}
