package com.sgai.vista;

import org.junit.jupiter.api.Test;

class VentanaReporteTest {

  @Test
  void seAdaptaAlMonitor() {
    VerificacionesVentanaReporte.dimensiones();
  }

  @Test
  void muestraLaHojaCompleta() throws Exception {
    VerificacionesVentanaReporte.hojaCompleta();
  }
}
