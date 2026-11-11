package com.sgai.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

// Rojo y negrita para notas <60, incluso cuando la fila está seleccionada.
public final class RenderizadorNotas extends DefaultTableCellRenderer {

  private static final long serialVersionUID = 1L;

  @Override
  public Component getTableCellRendererComponent(
    JTable tabla,
    Object valor,
    boolean seleccionado,
    boolean foco,
    int fila,
    int columna
  ) {
    Component celda = super.getTableCellRendererComponent(
      tabla,
      valor,
      seleccionado,
      foco,
      fila,
      columna
    );
    boolean reprobada =
      valor instanceof Number nota &&
      tabla
        .getColumnName(columna)
        .toLowerCase(java.util.Locale.ROOT)
        .matches(".*(nota|promedio).*") &&
      nota.doubleValue() < 60;
    celda.setForeground(reprobada ? new Color(170, 15, 30) : Estilo.TEXTO);
    celda.setBackground(
      seleccionado
        ? new Color(205, 226, 255)
        : fila % 2 == 0
          ? Color.WHITE
          : new Color(235, 243, 255)
    );
    celda.setFont(
      tabla.getFont().deriveFont(reprobada ? Font.BOLD : Font.PLAIN)
    );
    setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12));
    return celda;
  }
}
