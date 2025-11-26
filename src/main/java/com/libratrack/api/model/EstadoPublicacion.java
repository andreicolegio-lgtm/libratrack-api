package com.libratrack.api.model;

/** Indica el estado de lanzamiento o publicación del contenido original. */
public enum EstadoPublicacion {
  /** La obra se está publicando actualmente (ej. Serie en emisión, Manga en publicación). */
  EN_EMISION,

  /** La obra ha concluido su publicación o emisión. */
  FINALIZADO,

  /**
   * La obra está disponible para su consumo (generalmente usado para Películas o Libros únicos).
   */
  DISPONIBLE,

  /** La obra fue cancelada antes de terminar su historia o producción. */
  CANCELADO
}
