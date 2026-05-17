package br.ifsp.taskmaster.exception;

import java.time.LocalDateTime;

public class ErrorResponseDTO {

    private int status;
    private String erro;
    private Object detalhes;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(int status, String erro, Object detalhes) {
        this.status = status;
        this.erro = erro;
        this.detalhes = detalhes;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public Object getDetalhes() { return detalhes; }
    public LocalDateTime getTimestamp() { return timestamp; }
}