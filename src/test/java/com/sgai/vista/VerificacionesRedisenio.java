package com.sgai.vista;

import com.sgai.controlador.*;
import com.sgai.modelo.*;
import com.sgai.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

// Pruebas de datos y componentes reales sin requerir un servidor ni una pantalla.
public final class VerificacionesRedisenio {

  private VerificacionesRedisenio() {}

  private static void comprobar(boolean condicion) {
    if (!condicion) throw new AssertionError(
      "Regresión de identidad visual o cuentas"
    );
  }

  private static void rechazar(Runnable operacion) {
    try {
      operacion.run();
      throw new AssertionError("Se aceptó un dato inválido");
    } catch (ExcepcionNegocio esperado) {}
  }

  public static void busqueda() {
    Estudiante ana = new Estudiante(
      1,
      "ESTU1",
      "Ana María",
      "Pérez López",
      "ana@colegio.edu.ni",
      7
    );
    Estudiante otra = new Estudiante(
      2,
      "ESTU10",
      "Ana",
      "Díaz",
      "otra@colegio.edu.ni",
      8
    );
    Docente docente = new Docente(
      1,
      "PROF1",
      "Ana",
      "Pérez",
      "docente@colegio.edu.ni",
      "Matemáticas"
    );
    IndicePersonas indice = new IndicePersonas(List.of(ana, otra, docente));
    comprobar(indice.buscar("").size() == 3);
    comprobar(indice.buscar("ESTU1").size() == 2);
    comprobar(indice.buscar("prof1").equals(List.of(docente)));
    comprobar(indice.buscar("ANA PEREZ DOCENTE").equals(List.of(docente)));
    comprobar(indice.buscar("  marí pérez estu ").equals(List.of(ana)));
    comprobar(indice.buscar("estudiante").size() == 2);
    comprobar(indice.buscar("LÓPEZ estudiante").equals(List.of(ana)));
    comprobar(indice.buscar("NO EXISTE").isEmpty());
    comprobar(new IndicePersonas(List.of()).buscar("ana").isEmpty());
  }

  public static void notasYClaves() {
    for (int nota : new int[] { 0, 59, 60, 100 })
      comprobar(new Nota(1, 1, 1, 1, 1, nota).nota() == nota);
    rechazar(() -> Validacion.nota(-1));
    rechazar(() -> Validacion.nota(101));
    for (String nota : new String[] { "59.5", "60.00", "-1", "101", "abc", "" })
      rechazar(() -> Validacion.nota(nota));
    char[] clave = "Clave-Prueba-2026".toCharArray();
    try {
      Formulario.validarClaves(clave, clave.clone());
      rechazar(() ->
        Formulario.validarClaves(clave, "Otra-Clave-2026".toCharArray())
      );
      rechazar(() -> Formulario.validarClaves(new char[11], new char[11]));
    } finally {
      Arrays.fill(clave, '\0');
    }
  }

  public static void apariencia() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      Estilo.instalar();
      JButton boton = Estilo.boton("Buscar", () -> {});
      comprobar(boton.getUI() instanceof BotonAzulUI);
      comprobar(new JButton("Aceptar").getUI() instanceof BotonAzulUI);
      comprobar(boton.getIcon() != null);
      boton.setSize(150, 48);
      BufferedImage superficie = new BufferedImage(
        150,
        48,
        BufferedImage.TYPE_INT_RGB
      );
      Graphics2D grafico = superficie.createGraphics();
      try {
        boton.paint(grafico);
      } finally {
        grafico.dispose();
      }
      comprobar((superficie.getRGB(80, 5) & 0xffffff) != 0);
      JTable tabla = new JTable(
        new Object[][] { { 59, 1 } },
        new String[] { "Nota", "Grado" }
      );
      RenderizadorNotas renderizador = new RenderizadorNotas();
      comprobar(
        renderizador
          .getTableCellRendererComponent(tabla, 59, true, false, 0, 0)
          .getForeground()
          .equals(new Color(170, 15, 30))
      );
      comprobar(
        !renderizador
          .getTableCellRendererComponent(tabla, 1, false, false, 0, 1)
          .getForeground()
          .equals(new Color(170, 15, 30))
      );
      Estudiante alumno = new Estudiante(
        1,
        "ESTU1",
        "Ana",
        "Pérez",
        "ana@colegio.edu.ni",
        7
      );
      ReporteAcademico reporte = new ReporteAcademico(
        alumno,
        List.of(new Curso(1, "CURS1", "Matemáticas", 7)),
        List.of(new Nota(1, 1, 1, 1, 1, 59), new Nota(2, 1, 1, 1, 2, 60)),
        0
      );
      DocumentoReporte documento = new DocumentoReporte(reporte);
      BufferedImage hoja = new BufferedImage(
        612,
        792,
        BufferedImage.TYPE_INT_RGB
      );
      Graphics2D salida = hoja.createGraphics();
      try {
        documento.print(salida, DocumentoReporte.formatoCarta(), 0);
      } finally {
        salida.dispose();
      }
      boolean rojo = false;
      for (int x = 251; x < 314; x++) for (int y = 164; y < 186; y++) {
        Color color = new Color(hoja.getRGB(x, y));
        if (
          color.getRed() > 120 && color.getGreen() < 60 && color.getBlue() < 80
        ) rojo = true;
      }
      comprobar(rojo);
      comprobar(reporte.filas().get(0).primero() == 59);
      comprobar(reporte.filas().get(0).tercero() == null);
    });
  }

  public static void main(String[] argumentos) throws Exception {
    busqueda();
    notasYClaves();
    apariencia();
  }
}
