package com.sgai.vista;

import java.awt.*;
import javax.swing.*;

// Distribución compartida y comprobable sin depender de una sesión de base de datos.
final class DisposicionReportes {

  private DisposicionReportes() {}

  static JPanel filtros(
    JTextField estudiante,
    JTextField curso,
    JSpinner grado,
    JSpinner parcial,
    JPanel acciones
  ) {
    JPanel fila = new JPanel(new GridBagLayout());
    String[] etiquetas = {
      "Buscar estudiante (código/nombre/apellidos):",
      "Curso:",
      "Grado (0=todos):",
      "Período parcial (0=todos):",
    };
    JComponent[] entradas = { estudiante, curso, grado, parcial };
    estudiante.setColumns(12);
    curso.setColumns(9);
    for (int indice = 0; indice < entradas.length; indice++) {
      JPanel campo = new JPanel(new BorderLayout(0, 5));
      campo.add(new JLabel(etiquetas[indice]), BorderLayout.NORTH);
      campo.add(entradas[indice], BorderLayout.CENTER);
      GridBagConstraints posicion = new GridBagConstraints();
      posicion.gridx = indice;
      posicion.gridy = 0;
      posicion.weightx = indice < 2 ? 1 : 0;
      posicion.fill = GridBagConstraints.HORIZONTAL;
      posicion.insets = new Insets(4, 4, 4, 4);
      fila.add(campo, posicion);
    }
    JPanel cabecera = new JPanel(new BorderLayout(0, 4));
    cabecera.add(fila, BorderLayout.NORTH);
    cabecera.add(acciones, BorderLayout.SOUTH);
    return cabecera;
  }

  static JSplitPane tablas(JTable estudiantes, JTable notas) {
    JScrollPane superior = tabla(estudiantes),
      inferior = tabla(notas);
    JSplitPane division = new JSplitPane(
      JSplitPane.VERTICAL_SPLIT,
      superior,
      inferior
    );
    division.setResizeWeight(0.5);
    division.setContinuousLayout(true);
    return division;
  }

  private static JScrollPane tabla(JTable tabla) {
    tabla.setRowHeight(30);
    tabla.getTableHeader().setPreferredSize(new Dimension(0, 32));
    tabla.setPreferredScrollableViewportSize(
      new Dimension(450, 4 * tabla.getRowHeight())
    );
    JScrollPane panel = new JScrollPane(tabla);
    panel.setMinimumSize(new Dimension(100, 4 * tabla.getRowHeight() + 32 + 4));
    return panel;
  }
}
