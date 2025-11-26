package com.libratrack.api.model;

/** Define el estado actual de una propuesta de contenido enviada por un usuario. */
public enum EstadoPropuesta {
  /** La propuesta ha sido enviada y espera revisión por parte de un moderador. */
  PENDIENTE,

  /** La propuesta ha sido aceptada y convertida en un Elemento (Oficial o Comunitario). */
  APROBADO,

  /** La propuesta ha sido denegada (ej. duplicada, inapropiada, datos incorrectos). */
  RECHAZADO
}
