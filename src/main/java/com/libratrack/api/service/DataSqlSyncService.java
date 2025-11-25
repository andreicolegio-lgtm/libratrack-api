package com.libratrack.api.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DataSqlSyncService {

    private static final Logger LOGGER = Logger.getLogger(DataSqlSyncService.class.getName());
    private static final String DATA_SQL_PATH = System.getProperty("user.dir") + "/src/main/resources/data.sql";

    public void appendGenreLink(String typeName, String genreName) {
        try {
            Path path = Paths.get(DATA_SQL_PATH);

            if (!Files.exists(path)) {
                LOGGER.log(Level.WARNING, "data.sql file not found at: " + DATA_SQL_PATH);
                return;
            }

            String genreInsert = "INSERT IGNORE INTO generos (nombre) VALUES ('" + genreName + "');";
            String typeGenreInsert = "INSERT IGNORE INTO tipo_genero (tipo_id, genero_id) " +
                    "SELECT t.id, g.id FROM tipos t, generos g WHERE t.nombre = '" + typeName +
                    "' AND g.nombre = '" + genreName + "';";

            List<String> existingLines = Files.readAllLines(path);

            if (!existingLines.contains(genreInsert)) {
                Files.write(path, (genreInsert + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }

            if (!existingLines.contains(typeGenreInsert)) {
                Files.write(path, (typeGenreInsert + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to append to data.sql file", e);
        }
    }
}