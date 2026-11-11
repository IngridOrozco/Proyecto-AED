package com.sgai.estructuras;

import com.sgai.modelo.Nota;

// Lista simple propia: inserción O(1), búsqueda/eliminación O(n), copia O(n).
public final class ListaEnlazadaNotas {

  private NodoNota cabeza, cola;
  private int cantidad;

  public synchronized void agregar(Nota dato) {
    NodoNota nuevo = new NodoNota(dato);
    if (cola == null) cabeza = nuevo;
    else cola.siguiente = nuevo;
    cola = nuevo;
    cantidad++;
  }

  public synchronized Nota buscar(long id) {
    for (NodoNota n = cabeza; n != null; n = n.siguiente) if (
      n.dato.id() == id
    ) return n.dato;
    return null;
  }

  public synchronized boolean eliminar(long id) {
    NodoNota anterior = null,
      actual = cabeza;
    while (actual != null) {
      if (actual.dato.id() == id) {
        if (anterior == null) cabeza = actual.siguiente;
        else anterior.siguiente = actual.siguiente;
        if (cola == actual) cola = anterior;
        cantidad--;
        return true;
      }
      anterior = actual;
      actual = actual.siguiente;
    }
    return false;
  }

  public synchronized int getCantidad() {
    return cantidad;
  }

  public synchronized Nota[] aArreglo() {
    Nota[] datos = new Nota[cantidad];
    int i = 0;
    for (NodoNota n = cabeza; n != null; n = n.siguiente) datos[i++] = n.dato;
    return datos;
  }
}
