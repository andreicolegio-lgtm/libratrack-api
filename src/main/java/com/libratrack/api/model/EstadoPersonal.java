package com.libratrack.api.model;

/** Representa el estado de progreso de un usuario respecto a un elemento específico. */
public enum EstadoPersonal {
  /** El usuario tiene planeado consumir este contenido pero aún no ha empezado. */
  PENDIENTE,

  /** El usuario está actualmente leyendo, viendo o jugando este contenido. */
  EN_PROGRESO,

  /** El usuario ha completado el contenido. */
  TERMINADO,

  /** El usuario ha dejado de consumir el contenido sin terminarlo. */
  ABANDONADO
}
