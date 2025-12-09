package com.br.fiap.ingestao_feedback.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import java.time.Instant;
import java.util.UUID;

@DynamoDbBean
public class Feedback {
    private String id;
    private String descricao;
    private Integer nota;
    private String dataCriacao;
    private String status;

    public Feedback() {}

    public Feedback(String descricao, Integer nota) {
        this.id = UUID.randomUUID().toString();
        this.descricao = descricao;
        this.nota = nota;
        this.dataCriacao = Instant.now().toString();
        this.status = "RECEBIDO";
    }

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}