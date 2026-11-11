package com.sgai.vista;

import org.junit.jupiter.api.Test;

class RedisenioTest {

  @Test
  void buscaPersonasConIndiceBinario() {
    VerificacionesRedisenio.busqueda();
  }

  @Test
  void validaEnterosYConfirmacion() {
    VerificacionesRedisenio.notasYClaves();
  }

  @Test
  void conservaIdentidadYNotasRojas() throws Exception {
    VerificacionesRedisenio.apariencia();
  }
}
