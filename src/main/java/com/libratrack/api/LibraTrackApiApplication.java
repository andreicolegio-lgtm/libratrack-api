package com.libratrack.api;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada principal de la aplicación LibraTrack API. Habilita la configuración automática
 * de Spring Boot y la programación de tareas.
 */
@SpringBootApplication
@EnableScheduling
public class LibraTrackApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(LibraTrackApiApplication.class, args);
  }

  /**
   * Configura la zona horaria de la aplicación a UTC por defecto. Esto asegura consistencia en las
   * fechas (tokens, timestamps) independientemente de la zona horaria del servidor donde se
   * despliegue.
   */
  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
