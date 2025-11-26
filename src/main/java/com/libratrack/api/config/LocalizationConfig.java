package com.libratrack.api.config;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuración para la internacionalización (i18n) de la API. Permite devolver mensajes de error y
 * éxito en el idioma del usuario basándose en la cabecera 'Accept-Language'.
 */
@Configuration
public class LocalizationConfig {

  /** Configura la fuente de mensajes (archivos .properties). */
  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    // Nombre base de los archivos de recursos (src/main/resources/messages)
    messageSource.setBasename("messages");
    messageSource.setDefaultEncoding("UTF-8");
    // Si no encuentra un mensaje, devuelve la clave en lugar de lanzar excepción
    messageSource.setUseCodeAsDefaultMessage(true);
    return messageSource;
  }

  /** Configura el resolver de idioma basado en la cabecera HTTP. */
  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    // Idioma por defecto si el cliente no envía 'Accept-Language'
    localeResolver.setDefaultLocale(Locale.ENGLISH);
    return localeResolver;
  }
}
