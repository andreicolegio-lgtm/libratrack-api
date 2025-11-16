package com.libratrack.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraTrackApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(LibraTrackApiApplication.class, args);
  }
}
