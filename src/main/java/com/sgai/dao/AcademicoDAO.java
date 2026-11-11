package com.sgai.dao;

import com.sgai.modelo.*;
import com.sgai.util.ExcepcionNegocio;
import java.sql.*;
import java.util.*;

// DAO JDBC. SQL estructural fijo; todo dato externo se enlaza como parámetro.
public final class AcademicoDAO {

  public List<Estudiante> estudiantes(Connection conexion) throws SQLException {
    List<Estudiante> datos = new ArrayList<>();
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT * FROM estudiantes ORDER BY id"
      );
      ResultSet filas = consulta.executeQuery()
    ) {
      while (filas.next())
        datos.add(
          new Estudiante(
            filas.getLong("id"),
            filas.getString("codigo"),
            filas.getString("nombres"),
            filas.getString("apellidos"),
            filas.getString("correo"),
            filas.getInt("grado")
          )
        );
    }
    return datos;
  }

  public List<Docente> docentes(Connection conexion) throws SQLException {
    List<Docente> datos = new ArrayList<>();
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT * FROM docentes ORDER BY id"
      );
      ResultSet filas = consulta.executeQuery()
    ) {
      while (filas.next())
        datos.add(
          new Docente(
            filas.getLong("id"),
            filas.getString("codigo"),
            filas.getString("nombres"),
            filas.getString("apellidos"),
            filas.getString("correo"),
            filas.getString("especialidad")
          )
        );
    }
    return datos;
  }

  public List<Curso> cursos(Connection conexion) throws SQLException {
    List<Curso> datos = new ArrayList<>();
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT * FROM cursos ORDER BY id"
      );
      ResultSet filas = consulta.executeQuery()
    ) {
      while (filas.next())
        datos.add(
          new Curso(
            filas.getLong("id"),
            filas.getString("codigo"),
            filas.getString("nombre"),
            filas.getInt("grado")
          )
        );
    }
    return datos;
  }

  public List<Asignacion> asignaciones(Connection conexion)
    throws SQLException {
    List<Asignacion> datos = new ArrayList<>();
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT * FROM asignacion_docente_curso"
      );
      ResultSet filas = consulta.executeQuery()
    ) {
      while (filas.next())
        datos.add(
          new Asignacion(filas.getLong("docente_id"), filas.getLong("curso_id"))
        );
    }
    return datos;
  }

  // Filtrado de notas también en SQL: no se entrega información ajena a la sesión.
  public List<Nota> notas(Connection conexion, Usuario usuario)
    throws SQLException {
    String sql = switch (usuario.rol()) {
      case ADMINISTRADOR -> "SELECT * FROM notas";
      case ESTUDIANTE -> "SELECT * FROM notas WHERE estudiante_id=?";
      case DOCENTE -> "SELECT n.* FROM notas n JOIN asignacion_docente_curso a ON a.curso_id=n.curso_id WHERE a.docente_id=?";
    };
    List<Nota> datos = new ArrayList<>();
    try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
      if (usuario.rol() != Rol.ADMINISTRADOR) consulta.setLong(
        1,
        usuario.rol() == Rol.ESTUDIANTE
          ? usuario.estudianteId()
          : usuario.docenteId()
      );
      try (ResultSet filas = consulta.executeQuery()) {
        while (filas.next())
          datos.add(
            new Nota(
              filas.getLong("id"),
              filas.getLong("estudiante_id"),
              filas.getLong("curso_id"),
              filas.getLong("docente_id"),
              filas.getInt("periodo_parcial"),
              leerNota(filas)
            )
          );
      }
    }
    return datos;
  }

  // Código generado en la misma transacción: no se reutilizan códigos de bajas.
  private String siguienteCodigo(Connection conexion, String prefijo)
    throws SQLException {
    // FOR UPDATE reserva la secuencia hasta terminar la transacción, evitando códigos repetidos
    // cuando dos administradores registran datos al mismo tiempo.
    long siguiente;
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT siguiente FROM secuencias_codigo WHERE tipo=? FOR UPDATE"
      )
    ) {
      consulta.setString(1, prefijo);
      try (ResultSet filas = consulta.executeQuery()) {
        if (!filas.next()) throw new ExcepcionNegocio(
          "Aplique la migración de matrícula y códigos antes de continuar."
        );
        siguiente = filas.getLong(1);
      }
    }
    if (
      siguiente <= 0 || siguiente == Long.MAX_VALUE
    ) throw new ExcepcionNegocio("Se agotó la secuencia de códigos.");
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "UPDATE secuencias_codigo SET siguiente=? WHERE tipo=?"
      )
    ) {
      consulta.setLong(1, siguiente + 1);
      consulta.setString(2, prefijo);
      comprobar(consulta.executeUpdate());
    }
    return prefijo + siguiente;
  }

  public void guardarPersona(Connection conexion, Persona persona)
    throws SQLException {
    boolean estudiante = persona instanceof Estudiante,
      nuevo = persona.getId() == 0;
    String tabla = estudiante ? "estudiantes" : "docentes",
      campo = estudiante ? "grado" : "especialidad";
    String sql = nuevo
      ? "INSERT INTO " +
        tabla +
        "(nombres,apellidos,correo," +
        campo +
        ",codigo) VALUES(?,?,?,?,?)"
      : "UPDATE " +
        tabla +
        " SET nombres=?,apellidos=?,correo=?," +
        campo +
        "=? WHERE id=?";
    try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
      consulta.setString(1, persona.getNombres());
      consulta.setString(2, persona.getApellidos());
      consulta.setString(3, persona.getCorreo());
      consulta.setObject(
        4,
        estudiante
          ? ((Estudiante) persona).getGrado()
          : ((Docente) persona).getEspecialidad()
      );
      if (nuevo) consulta.setString(
        5,
        siguienteCodigo(conexion, estudiante ? "ESTU" : "PROF")
      );
      else consulta.setLong(5, persona.getId());
      comprobar(consulta.executeUpdate());
    }
  }

  public void guardarCurso(Connection conexion, Curso curso)
    throws SQLException {
    boolean nuevo = curso.id() == 0;
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        nuevo
          ? "INSERT INTO cursos(nombre,grado,codigo) VALUES(?,?,?)"
          : "UPDATE cursos SET nombre=?,grado=? WHERE id=?"
      )
    ) {
      consulta.setString(1, curso.nombre());
      consulta.setInt(2, curso.grado());
      if (nuevo) consulta.setString(3, siguienteCodigo(conexion, "CURS"));
      else consulta.setLong(3, curso.id());
      comprobar(consulta.executeUpdate());
    }
  }

  public List<Matricula> matriculas(Connection conexion) throws SQLException {
    List<Matricula> resultado = new ArrayList<>();
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT estudiante_id,curso_id FROM matriculas"
      );
      ResultSet filas = consulta.executeQuery()
    ) {
      while (filas.next())
        resultado.add(new Matricula(filas.getLong(1), filas.getLong(2)));
    }
    return resultado;
  }

  // Inserción idempotente; no retira matrículas existentes ni borra historial.
  public void matricular(Connection conexion, Matricula matricula)
    throws SQLException {
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "INSERT INTO matriculas(estudiante_id,curso_id) VALUES(?,?) ON DUPLICATE KEY UPDATE estudiante_id=estudiante_id"
      )
    ) {
      consulta.setLong(1, matricula.estudianteId());
      consulta.setLong(2, matricula.cursoId());
      consulta.executeUpdate();
    }
  }

  // Se comprueba el historial antes de borrar: nunca se retiran solo algunos cursos.
  public void eliminarMatricula(Connection conexion, long estudianteId)
    throws SQLException {
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "SELECT 1 FROM notas WHERE estudiante_id=? LIMIT 1"
      )
    ) {
      consulta.setLong(1, estudianteId);
      try (ResultSet filas = consulta.executeQuery()) {
        if (filas.next()) throw new ExcepcionNegocio(
          "No se puede eliminar la matrícula: el estudiante tiene notas registradas."
        );
      }
    }
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        "DELETE FROM matriculas WHERE estudiante_id=?"
      )
    ) {
      consulta.setLong(1, estudianteId);
      if (consulta.executeUpdate() == 0) throw new ExcepcionNegocio(
        "El estudiante no tiene cursos matriculados."
      );
    }
  }

  // No truncamos notas antiguas: la migración debe resolver primero cualquier fracción.
  private int leerNota(ResultSet filas) throws SQLException {
    String valor = filas.getString("nota");
    try {
      return com.sgai.util.Validacion.nota(
        new java.math.BigDecimal(valor).intValueExact()
      );
    } catch (ArithmeticException | NumberFormatException error) {
      throw new ExcepcionNegocio(
        "Existen notas decimales. Revise la migración 003 antes de continuar.",
        error
      );
    }
  }

  public void asignar(
    Connection conexion,
    Asignacion asignacion,
    boolean eliminar
  ) throws SQLException {
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        eliminar
          ? "DELETE FROM asignacion_docente_curso WHERE docente_id=? AND curso_id=?"
          : "INSERT INTO asignacion_docente_curso(docente_id,curso_id) VALUES(?,?)"
      )
    ) {
      consulta.setLong(1, asignacion.docenteId());
      consulta.setLong(2, asignacion.cursoId());
      comprobar(consulta.executeUpdate());
    }
  }

  public void guardarNota(Connection conexion, Nota nota) throws SQLException {
    try (
      PreparedStatement consulta = conexion.prepareStatement(
        nota.id() == 0
          ? "INSERT INTO notas(estudiante_id,curso_id,docente_id,periodo_parcial,nota) VALUES(?,?,?,?,?)"
          : "UPDATE notas SET estudiante_id=?,curso_id=?,docente_id=?,periodo_parcial=?,nota=? WHERE id=?"
      )
    ) {
      consulta.setLong(1, nota.estudianteId());
      consulta.setLong(2, nota.cursoId());
      consulta.setLong(3, nota.docenteId());
      consulta.setInt(4, nota.periodoParcial());
      consulta.setInt(5, nota.nota());
      if (nota.id() != 0) consulta.setLong(6, nota.id());
      comprobar(consulta.executeUpdate());
    }
  }

  public enum Tabla {
    ESTUDIANTES,
    DOCENTES,
    CURSOS,
    NOTAS,
  }

  public void eliminar(Connection conexion, Tabla tabla, long id)
    throws SQLException {
    String sql = switch (tabla) {
      case ESTUDIANTES -> "DELETE FROM estudiantes WHERE id=?";
      case DOCENTES -> "DELETE FROM docentes WHERE id=?";
      case CURSOS -> "DELETE FROM cursos WHERE id=?";
      case NOTAS -> "DELETE FROM notas WHERE id=?";
    };
    try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
      consulta.setLong(1, id);
      comprobar(consulta.executeUpdate());
    }
  }

  private void comprobar(int filas) {
    if (filas != 1) throw new ExcepcionNegocio(
      "El registro ya no existe. Actualice la pantalla."
    );
  }
}
