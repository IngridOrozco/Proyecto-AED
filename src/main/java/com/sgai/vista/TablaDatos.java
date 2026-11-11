package com.sgai.vista;

import java.util.*;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

// JTable tipada, de solo lectura; la selección siempre se convierte al índice de modelo.
public final class TablaDatos<T> extends JTable {

  private static final long serialVersionUID = 1L;
  private final Modelo<T> modelo;

  public TablaDatos(String[] columnas, Function<T, Object[]> valores) {
    modelo = new Modelo<>(columnas, valores);
    setModel(modelo);
    setRowHeight(38);
    setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
    setShowGrid(false);
    setIntercellSpacing(new java.awt.Dimension(0, 0));
    getTableHeader().setPreferredSize(new java.awt.Dimension(0, 42));
    getTableHeader().setDefaultRenderer(
      (tabla, valor, seleccionado, foco, fila, columna) -> {
        JLabel titulo = new JLabel(String.valueOf(valor));
        titulo.setOpaque(true);
        titulo.setBackground(Estilo.AZUL);
        titulo.setForeground(java.awt.Color.WHITE);
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        titulo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return titulo;
      }
    );
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    getTableHeader().setReorderingAllowed(false);
    setFillsViewportHeight(true);
    setAutoCreateRowSorter(false);
    setDefaultRenderer(Object.class, new RenderizadorNotas());
  }

  public void mostrar(Collection<T> filas) {
    modelo.filas = List.copyOf(filas);
    modelo.fireTableDataChanged();
  }

  public T seleccionado() {
    int fila = getSelectedRow();
    if (fila < 0) throw new com.sgai.util.ExcepcionNegocio(
      "Seleccione una fila de la tabla."
    );
    return modelo.filas.get(convertRowIndexToModel(fila));
  }

  private static final class Modelo<T> extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private final String[] columnas;
    private final Function<T, Object[]> valores;
    private List<T> filas = List.of();

    Modelo(String[] columnas, Function<T, Object[]> valores) {
      this.columnas = columnas.clone();
      this.valores = valores;
    }

    @Override
    public int getRowCount() {
      return filas.size();
    }

    @Override
    public int getColumnCount() {
      return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
      return columnas[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
      return valores.apply(filas.get(fila))[columna];
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
      return false;
    }
  }
}
