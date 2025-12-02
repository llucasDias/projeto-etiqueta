package com.lucas.projetoid.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


/**
 * Entidade responsável por armazenar todas as etiquetas geradas para cada ordem de produção.
 * Observações importantes:
 * - O código da etiqueta (ET0000001) NÃO é gerado aqui. Ele é gerado somente no service
 *   devido à necessidade de primeiro salvar para obter o ID sequencial.
 * - O campo checkid é o RECNO do Protheus, usado para controle de importação.
 **/


@Entity
@Table(name = "etiquetamatriz")

public class EtiquetaMatrizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    Integer id;


    @Column(name = "codigo_etiqueta", unique = true)
    private String codigoEtiqueta;

    @Column(name = "op")
    String op;

    @Column(name = "cliente")
    String cliente;

    @Column(name = "pedido")
    String pedido;

    @Column(name = "item")
    String item;

    @Column(name = "quantidade")
    Double qtd;

    @Column(name = "data")
    LocalDateTime data;


    /**
     * Indica se a etiqueta já foi processada:
     * false = ainda não utilizada
     * true  = já utilizada
     **/

    @Column(name = "status")
    boolean status;


    /**
     * Identificador RECNO do Protheus.
     * Usado exclusivamente para impedir reimportação de ordens já lidas.
     **/

    @Column(name = "checkid")
    Integer checkid;


    /**
     * Identificador de Etiqueta possui erro, como falta do pedido de venda;
     */

    @Column(name = "error")
    boolean status_error;




    // ----------------------------------
    // Getters e Setters
    // ----------------------------------


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOp() { return op; }
    public void setOp(String op) { this.op = op; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getPedido() { return pedido; }
    public void setPedido(String pedido) { this.pedido = pedido; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public Double getQtd() { return qtd; }
    public void setQtd(Double qtd) { this.qtd = qtd; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public Integer getCheckid() {return checkid;}

    public void setCheckid(Integer checkid) {this.checkid = checkid;}

    public String getCodigoEtiqueta() {return codigoEtiqueta;}

    public void setCodigoEtiqueta(String codigoEtiqueta) {this.codigoEtiqueta = codigoEtiqueta;}

    public boolean isStatus_error() {
        return status_error;
    }

    public void setStatus_error(boolean status_error) {
        this.status_error = status_error;
    }
}


