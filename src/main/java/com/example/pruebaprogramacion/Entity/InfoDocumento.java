package com.example.pruebaprogramacion.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"infoDocumentos\"")
@Getter
@Setter
@JsonIgnoreProperties({"documento"})
public class InfoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idDocumento\"", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "autor", nullable = false)
    private String autor;

    @Column(name = "area", nullable = false)
    private String area;

    @Column(name = "estado", nullable = false)
    private int estado;

    @Column(name = "empresa", nullable = false)
    private String empresa;

    @Column(name = "\"fechaSubida\"", nullable = false)
    private OffsetDateTime fechaSubida;

    @Column(name = "\"fechaActualizacion\"")
    private OffsetDateTime fechaActualizacion;

    @Column(name = "\"fechaEliminacion\"")
    private OffsetDateTime fechaEliminacion;

    @Column(name = "documento", nullable = false)
    private byte[] documento;

    @Column(name = "url",nullable = false)
    private String url;

    public InfoDocumento(String nombre, String autor, String area, String empresa,int estado, OffsetDateTime fechaSubida, String url) {
        this.nombre = nombre;
        this.autor = autor;
        this.area = area;
        this.fechaSubida = fechaSubida;
        this.empresa = empresa;
        this.estado = estado;
        this.url = url;
    }

    public InfoDocumento() {
    }
}