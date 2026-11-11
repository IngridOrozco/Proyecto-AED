package com.sgai.vista;

import com.sgai.controlador.ControladorAcademico;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

// Acceso común a los tres roles, sin funciones de instalación ni diagnóstico.
public final class VentanaLogin extends VentanaBase {

  private static final long serialVersionUID = 1L;
  private final JTextField usuario = Formulario.texto("", 40);
  private final JPasswordField clave = Formulario.clave();

  public VentanaLogin(ControladorAcademico controlador) {
    super(controlador, "SGAI · Colegio Antonio Soldano");
    JPanel contenido = Estilo.panel();
    JPanel encabezado = new JPanel(new GridLayout(0, 1, 0, 8));
    encabezado.add(Estilo.titulo("Colegio Antonio Soldano"));
    encabezado.add(new JLabel("Sistema de Gestión Académico Integral"));
    contenido.add(
      RecursosGraficos.cabecera(encabezado, 80, 80),
      BorderLayout.NORTH
    );
    RecursosGraficos.aplicarIcono(this);
    Formulario formulario = new Formulario();
    usuario.setToolTipText(
      "Escriba el usuario exacto. Los códigos de persona usan letras mayúsculas."
    );
    clave.setToolTipText(
      "La contraseña distingue entre mayúsculas y minúsculas."
    );
    formulario.campo("Usuario", usuario);
    formulario.campo("Contraseña", clave);
    contenido.add(formulario, BorderLayout.CENTER);
    JPanel pie = new JPanel(new GridLayout(0, 1, 0, 8));
    JButton entrar = Estilo.boton("Iniciar sesión", () -> accion(this::entrar));

    pie.add(entrar);
    pie.add(new JLabel("Si olvida su contraseña, contacte al administrador."));
    pie.add(estado);
    contenido.add(pie, BorderLayout.SOUTH);
    setContentPane(contenido);
    getRootPane().setDefaultButton(entrar);
    pack();
    setMinimumSize(new Dimension(540, 310));
    setLocationRelativeTo(null);
  }

  private void entrar() {
    char[] secreto = clave.getPassword();
    clave.setText("");
    try {
      ejecutar(controlador.autenticar(usuario.getText(), secreto), sesion -> {
        VentanaPrincipal ventana = new VentanaPrincipal(controlador);
        ventana.setVisible(true);
        dispose();
      });
    } finally {
      Arrays.fill(secreto, '\0');
    }
  }
}
