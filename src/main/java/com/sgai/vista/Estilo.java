package com.sgai.vista;

import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Identidad compartida: azul institucional, superficies claras y controles redondeados.
public final class Estilo {

  public static final Color AZUL = new Color(0, 69, 164),
    PRIMARIO = new Color(0, 94, 222),
    AZUL_HOVER = new Color(35, 125, 245),
    PESTANA_HOVER = new Color(228, 240, 255),
    FONDO = new Color(242, 247, 255),
    TEXTO = new Color(19, 42, 79),
    ROJO = new Color(190, 30, 45),
    VERDE = new Color(20, 115, 65);

  private Estilo() {}

  public static void instalar() {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (
      ReflectiveOperationException
      | UnsupportedLookAndFeelException error
    ) {
      // Los componentes propios también funcionan con la apariencia disponible.
    }
    UIManager.put("control", FONDO);
    UIManager.put("nimbusBase", AZUL);
    UIManager.put("nimbusBlueGrey", new Color(211, 226, 247));
    UIManager.put("nimbusSelectionBackground", PRIMARIO);
    UIManager.put("text", TEXTO);
    UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
    UIManager.put("Panel.background", FONDO);
    UIManager.put("OptionPane.background", FONDO);
    UIManager.put("OptionPane.messageForeground", TEXTO);
    UIManager.put(
      "OptionPane.messageFont",
      new Font("Segoe UI", Font.PLAIN, 14)
    );
    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
    UIManager.put("OptionPane.cancelButtonText", "Cancelar");
    UIManager.put("ScrollBarUI", BarraAzulUI.class.getName());
    UIManager.put("OptionPane.noButtonText", "No");
    UIManager.put("OptionPane.yesButtonText", "Sí");
    UIManager.put("OptionPane.okButtonText", "Aceptar");
    UIManager.put("ButtonUI", BotonAzulUI.class.getName());
    UIManager.put("Button.background", PRIMARIO);
    UIManager.put("Button.foreground", Color.WHITE);
    UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
    UIManager.put("Button.margin", new Insets(10, 18, 10, 18));
    UIManager.put(
      "Button.border",
      BorderFactory.createEmptyBorder(10, 18, 10, 18)
    );
    UIManager.put("TextField.background", Color.WHITE);
    UIManager.put("PasswordField.background", Color.WHITE);
    UIManager.put("TextField.foreground", TEXTO);
    UIManager.put("Table.selectionBackground", new Color(205, 226, 255));
    UIManager.put("Table.selectionForeground", TEXTO);
    UIManager.put("Table.gridColor", new Color(225, 234, 247));
    UIManager.put("TableHeader.background", AZUL);
    UIManager.put("TableHeader.foreground", Color.WHITE);
    UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 14));
    UIManager.put(
      "OptionPane.errorIcon",
      new IconoAviso(new Color(184, 35, 53), "!")
    );
    UIManager.put(
      "OptionPane.warningIcon",
      new IconoAviso(new Color(164, 101, 0), "!")
    );
    UIManager.put("OptionPane.informationIcon", new IconoAviso(PRIMARIO, "i"));
    UIManager.put("OptionPane.questionIcon", new IconoAviso(PRIMARIO, "?"));
  }

  public static JPanel panel() {
    JPanel panel = new JPanel(new BorderLayout(12, 12)) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        Graphics2D copia = (Graphics2D) grafico.create();
        try {
          copia.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
          );
          copia.setPaint(
            new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), FONDO)
          );
          copia.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
          // Las curvas se dibujan al fondo; no desplazan ni cubren los controles.
          Path2D curva = new Path2D.Double();
          curva.moveTo(getWidth(), 0);
          curva.lineTo(getWidth(), Math.min(150, getHeight()));
          curva.curveTo(
            getWidth() - 40,
            100,
            getWidth() - 80,
            75,
            getWidth() - 180,
            0
          );
          curva.closePath();
          copia.setColor(new Color(217, 235, 255));
          copia.fill(curva);
        } finally {
          copia.dispose();
        }
      }
    };
    panel.setBorder(new EmptyBorder(18, 18, 18, 18));
    panel.setBackground(FONDO);
    return panel;
  }

  public static JButton boton(String texto, Runnable accion) {
    JButton boton = new JButton(texto);
    boton.setUI(new BotonAzulUI());
    boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    boton.setMargin(new Insets(10, 18, 10, 18));
    boton.setBorder(new EmptyBorder(10, 18, 10, 18));
    boton.setBackground(PRIMARIO);
    boton.setForeground(Color.WHITE);
    boton.setIcon(new IconoAccion(texto));
    boton.setIconTextGap(8);
    if (
      java.util.Set.of("si", "no", "aceptar", "ok", "cancelar").contains(
        com.sgai.util.Busquedas.normalizar(boton.getText())
      )
    ) boton.setIcon(null);
    boton.addActionListener(evento -> accion.run());
    aplicarAccion(boton);
    return boton;
  }

  public static JButton colorear(JButton boton, Color color) {
    boton.setUI(new BotonAzulUI());
    boton.setBackground(color);
    boton.setForeground(color.equals(Color.WHITE) ? AZUL : Color.WHITE);
    aplicarAccion(boton);
    return boton;
  }

  // Una sola regla para botones normales y los que JOptionPane crea internamente.
  public static void aplicarAccion(AbstractButton boton) {
    String texto = com.sgai.util.Busquedas.normalizar(boton.getText());
    if (texto.equals("cancel") || texto.equals("calcelar")) {
      boton.setText("Cancelar");
      texto = "cancelar";
    }
    if (
      java.util.Set.of("si", "no", "aceptar", "ok", "cancelar").contains(texto)
    ) {
      boton.setBackground(PRIMARIO);
      boton.setForeground(Color.WHITE);
      boton.setIcon(null);
      return;
    }
    if (
      texto.startsWith("eliminar") ||
      texto.equals("retirar asignacion") ||
      texto.equals("cerrar sesion") ||
      ROJO.equals(boton.getBackground())
    ) {
      boton.setBackground(Color.WHITE);
      boton.setForeground(ROJO);
      boton.setIcon(new IconoAccion(boton.getText(), ROJO));
    } else if (
      texto.equals("crear cuenta con rol") ||
      texto.equals("restablecer contrasena")
    ) {
      boton.setBackground(PRIMARIO);
      boton.setForeground(Color.WHITE);
    }
    if (boton.getIcon() == null && !texto.isEmpty()) {
      boton.setIcon(new IconoAccion(boton.getText()));
      boton.setIconTextGap(8);
    }
  }

  public static JTabbedPane pestanas() {
    JTabbedPane panel = new JTabbedPane(
      JTabbedPane.TOP,
      JTabbedPane.SCROLL_TAB_LAYOUT
    );
    panel.setUI(
      new javax.swing.plaf.basic.BasicTabbedPaneUI() {
        @Override
        protected void paintTabBackground(
          Graphics grafico,
          int posicion,
          int indice,
          int x,
          int y,
          int ancho,
          int alto,
          boolean seleccionada
        ) {
          grafico.setColor(
            seleccionada
              ? PRIMARIO
              : getRolloverTab() == indice
                ? PESTANA_HOVER
                : Color.WHITE
          );
          grafico.fillRect(x, y, ancho, alto);
        }

        @Override
        protected void paintTabBorder(
          Graphics grafico,
          int posicion,
          int indice,
          int x,
          int y,
          int ancho,
          int alto,
          boolean seleccionada
        ) {
          grafico.setColor(new Color(210, 224, 244));
          grafico.drawRect(x, y, ancho, alto);
        }

        @Override
        protected void paintFocusIndicator(
          Graphics grafico,
          int posicion,
          Rectangle[] limites,
          int indice,
          Rectangle icono,
          Rectangle texto,
          boolean seleccionada
        ) {
          if (seleccionada && tabPane.hasFocus()) {
            Rectangle limite = limites[indice];
            grafico.setColor(Color.WHITE);
            grafico.drawRect(
              limite.x + 3,
              limite.y + 3,
              limite.width - 6,
              limite.height - 6
            );
          }
        }
      }
    );
    return panel;
  }

  public static JLabel titulo(String texto) {
    JLabel etiqueta = new JLabel(texto);
    etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 26));
    etiqueta.setForeground(AZUL);
    return etiqueta;
  }

  private record IconoAviso(Color color, String simbolo) implements Icon {
    @Override
    public int getIconWidth() {
      return 36;
    }

    @Override
    public int getIconHeight() {
      return 36;
    }

    @Override
    public void paintIcon(
      Component componente,
      Graphics grafico,
      int x,
      int y
    ) {
      Graphics2D copia = (Graphics2D) grafico.create();
      try {
        copia.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        copia.setColor(color);
        copia.fillOval(x + 2, y + 2, 32, 32);
        copia.setColor(Color.WHITE);
        copia.setFont(new Font("Segoe UI", Font.BOLD, 24));
        copia.drawString(
          simbolo,
          x + 18 - copia.getFontMetrics().stringWidth(simbolo) / 2,
          y + 27
        );
      } finally {
        copia.dispose();
      }
    }
  }
}
