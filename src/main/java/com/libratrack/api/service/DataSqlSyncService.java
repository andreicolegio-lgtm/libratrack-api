package com.libratrack.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de utilidad para desarrollo. Sincroniza dinámicamente los nuevos géneros creados en
 * tiempo de ejecución con el archivo 'data.sql' para que persistan entre reinicios de la aplicación
 * (útil en entornos H2/Dev).
 *
 * <p>ADVERTENCIA: Este servicio manipula archivos del código fuente. En un entorno de producción
 * real desplegado (ej. Docker, JAR), esto no funcionará ni debería usarse.
 */
@Service
public class DataSqlSyncService {

  private static final Logger logger = LoggerFactory.getLogger(DataSqlSyncService.class);

  // Ruta relativa al proyecto. En producción esto fallaría, pero es aceptable para desarrollo
  // local.
  private static final String DATA_SQL_PATH =
      System.getProperty("user.dir") + "/src/main/resources/data.sql";

  /** Añade sentencias SQL INSERT al archivo data.sql si no existen previamente. */
  public void appendGenreLink(String typeName, String genreName) {
    try {
      Path path = Paths.get(DATA_SQL_PATH);

      if (!Files.exists(path)) {
        logger.warn(
            "Archivo data.sql no encontrado en: {}. Saltando sincronización.", DATA_SQL_PATH);
        return;
      }

      // Sentencias SQL a inyectar
      String genreInsert =
          String.format("INSERT IGNORE INTO generos (nombre) VALUES ('%s');", genreName);
      String typeGenreInsert =
          String.format(
              "INSERT IGNORE INTO tipo_genero (tipo_id, genero_id) "
                  + "SELECT t.id, g.id FROM tipos t, generos g WHERE t.nombre = '%s' AND g.nombre = '%s';",
              typeName, genreName);

      List<String> existingLines = Files.readAllLines(path);

      // Escribir solo si no existe ya
      if (!existingLines.contains(genreInsert)) {
        Files.write(
            path, (genreInsert + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        logger.debug("Sincronizado nuevo género en data.sql: {}", genreName);
      }

      if (!existingLines.contains(typeGenreInsert)) {
        Files.write(
            path, (typeGenreInsert + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        logger.debug(
            "Sincronizada relación Tipo-Género en data.sql: {} -> {}", typeName, genreName);
      }

    } catch (IOException e) {
      logger.error("Error crítico al intentar escribir en data.sql. La sincronización falló.", e);
    }
  }
}
