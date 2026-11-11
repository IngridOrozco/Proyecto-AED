package com.sgai.estructuras;

import com.sgai.modelo.Nota;

// Nodo privado al paquete; no expone enlaces a consumidores.
public final class NodoNota {

  final Nota dato;
  NodoNota siguiente;

  NodoNota(Nota dato) {
    this.dato = java.util.Objects.requireNonNull(dato);
  }
}
