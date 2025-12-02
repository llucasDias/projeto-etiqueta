package com.lucas.projetoid.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade responsável por armazenar as etiquetas que estão com erros para geração.
 *.
 **/

@Entity
@Table (name = "etiquetaerro")
public class EtiquetaErroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checkid", nullable = false)
    private Integer checkid;

    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @Column(name = "data_erro", nullable = false)
    private LocalDateTime dataErro;


    // GETTERS e SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCheckid() {
        return checkid;
    }

    public void setCheckid(Integer checkid) {
        this.checkid = checkid;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataErro() {
        return dataErro;
    }

    public void setDataErro(LocalDateTime dataErro) {
        this.dataErro = dataErro;
    }
}
