package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "resenas",
    // Restricción para asegurar que un Usuario solo puede reseñar UN Elemento una vez
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "elemento_id"})
    }
)
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relaciones ---

    // La reseña pertenece a un Usuario (RF12)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // La reseña es sobre un Elemento (RF12)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false)
    private Elemento elemento;

    // --- Atributos de la Reseña ---

    @Min(value = 1, message = "La valoración mínima es 1")
    @Max(value = 5, message = "La valoración máxima es 5")
    @Column(nullable = false)
    private Integer valoracion; // (RF12)

    @Size(max = 2000, message = "La reseña no puede exceder los 2000 caracteres")
    @Lob // Tipo de dato TEXT en la BD
    private String textoResena;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // --- Métodos de ciclo de vida ---
    @PrePersist // Se ejecuta antes de guardar por primera vez
    protected void onCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // --- Getters y Setters & Constructores ---
    // (Estos los puedes dejar que VS Code los autogenere o pegarlos si quieres)

    public Resena() {
    }

    // Getters y Setters...

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

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public String getTextoResena() {
        return textoResena;
    }

    public void setTextoResena(String textoResena) {
        this.textoResena = textoResena;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}