package com.sgai.controlador;

import com.sgai.estructuras.Algoritmos;
import com.sgai.modelo.*;
import com.sgai.util.Busquedas;
import java.util.*;

// Índice ordenado por palabras de código, nombre y rol. No mezcla IDs de ambas tablas.
public final class IndicePersonas {

  private record Entrada(String palabra, Persona persona) {}

  private final Entrada[] entradas;
  private final Persona[] personas;
  private static final Comparator<Persona> POR_NOMBRE = Comparator.comparing(
    (Persona persona) -> Busquedas.normalizar(persona.getNombreCompleto())
  ).thenComparing(Persona::getCodigo);

  public IndicePersonas(Collection<? extends Persona> personas) {
    this.personas = personas.toArray(Persona[]::new);
    Algoritmos.ordenar(this.personas, POR_NOMBRE);
    List<Entrada> palabras = new ArrayList<>();
    for (Persona persona : this.personas) {
      String rol = persona instanceof Docente ? "docente" : "estudiante";
      String texto =
        persona.getCodigo() + " " + persona.getNombreCompleto() + " " + rol;
      for (String palabra : Busquedas.normalizar(texto).split("\\s+")) {
        palabras.add(new Entrada(palabra, persona));
      }
    }
    entradas = palabras.toArray(Entrada[]::new);
    Algoritmos.ordenar(entradas, Comparator.comparing(Entrada::palabra));
  }

  public List<Persona> buscar(String consulta) {
    String normalizada = Busquedas.normalizar(consulta);
    if (normalizada.isEmpty()) return List.of(personas);
    Set<Persona> coincidencias = null;
    for (String palabra : normalizada.split("\\s+")) {
      // Búsqueda binaria del primer término igual o mayor al prefijo solicitado.
      int inicio = 0,
        fin = entradas.length;
      while (inicio < fin) {
        int medio = inicio + (fin - inicio) / 2;
        if (entradas[medio].palabra().compareTo(palabra) < 0) inicio =
          medio + 1;
        else fin = medio;
      }
      Set<Persona> encontradas = new HashSet<>();
      for (
        int posicion = inicio;
        posicion < entradas.length &&
        entradas[posicion].palabra().startsWith(palabra);
        posicion++
      ) {
        encontradas.add(entradas[posicion].persona());
      }
      if (coincidencias == null) coincidencias = encontradas;
      else coincidencias.retainAll(encontradas);
      if (coincidencias.isEmpty()) return List.of();
    }
    Persona[] resultado = coincidencias.toArray(Persona[]::new);
    Algoritmos.ordenar(resultado, POR_NOMBRE);
    return List.of(resultado);
  }
}
