package br.ifsp.taskmaster.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class TaskMasterRequestDTO {

    @NotBlank(message = "O titulo deve ser obrigatório")
    private String titulo;
    @NotBlank(message = "A descrição deve ser obrigatória")
    private String descricao;
    @NotBlank(message = "A categoria deve ser obrigatória")
    private String categoria;
    @NotNull(message = "A data limite deve ser obrigatória")
    @FutureOrPresent(message = "A data limite não deve ser no passado")
    private LocalDate dataLimite;
    @NotNull(message = "A prioridade deve ser obrigatória")
    @Min(value = 1, message = "Prioridade mínima é 1")
    @Max(value = 5, message = "Prioridade máxima é 5")
    private Integer prioridade;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }
}
