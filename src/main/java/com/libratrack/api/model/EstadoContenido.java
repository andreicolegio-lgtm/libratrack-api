package com.libratrack.api.model;

/** Define la procedencia y nivel de verificación del contenido. */
public enum EstadoContenido {
  /** Contenido verificado y añadido por administradores. Es la fuente de verdad principal. */
  OFICIAL,

  /**
   * Contenido sugerido o creado por la comunidad. Puede estar sujeto a revisión o ser menos
   * confiable.
   */
  COMUNITARIO
}
