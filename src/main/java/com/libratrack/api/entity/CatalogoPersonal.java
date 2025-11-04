package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPersonal; // Importa tu nuevo Enum
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_personal",
    // ¡Buena práctica! Añadimos una restricción a nivel de tabla
    // para asegurar que un usuario NO pueda añadir el mismo elemento
    // dos veces a su catálogo.
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "elemento_id"})
    }
)
public class CatalogoPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relación 1: El Usuario ---
    // (Muchas entradas del catálogo pertenecen a Un Usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // La columna FK
    @NotNull
    private Usuario usuario;

    // --- Relación 2: El Elemento ---
    // (Muchas entradas del catálogo apuntan a Un Elemento)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false) // La columna FK
    @NotNull
    private Elemento elemento;

    // --- DATOS EXTRA DE LA RELACIÓN ---

    // (Requisito RF06)
    @Enumerated(EnumType.STRING) // Guarda "EN_PROGRESO" en lugar de un número
    @Column(nullable = false)
    @NotNull
    private EstadoPersonal estadoPersonal = EstadoPersonal.PENDIENTE; // Valor por defecto

    // (Requisito RF07)
    @Size(max = 100)
    @Column(length = 100)
    private String progresoEspecifico; // Ej: "T4:E3" o "Cap. 7"

    @Column(nullable = false, updatable = false)
    private LocalDateTime agregadoEn; // Fecha en que se añadió

    // --- Métodos de ciclo de vida ---
    @PrePersist // Esta anotación hace que el método se ejecute ANTES de guardar
    protected void onCrear() {
        this.agregadoEn = LocalDateTime.now();
    }

    // --- Constructores ---
    public CatalogoPersonal() {
    }

    // --- Getters y Setters ---
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public EstadoPersonal getEstadoPersonal() {
        return estadoPersonal;
    }

    public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
        this.estadoPersonal = estadoPersonal;
    }

    public String getProgresoEspecifico() {
        return progresoEspecifico;
    }

    public void setProgresoEspecifico(String progresoEspecifico) {
        this.progresoEspecifico = progresoEspecifico;
    }

    public LocalDateTime getAgregadoEn() {
        return agregadoEn;
    }

    public void setAgregadoEn(LocalDateTime agregadoEn) {
        this.agregadoEn = agregadoEn;
    }
}