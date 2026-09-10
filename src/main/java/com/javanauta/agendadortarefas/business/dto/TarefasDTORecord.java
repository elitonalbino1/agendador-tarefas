package com.javanauta.agendadortarefas.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javanauta.agendadortarefas.infrastructure.enums.StatusNotificacaoEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TarefasDTORecord(
        String id,

        @NotBlank(message = "Nome da tarefa é obrigatório")
        String nomeTarefa,

        String descricaoTarefa,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dataCriacao,

        @NotNull(message = "Data do evento é obrigatória")
        @Future(message = "Data do evento deve ser futura")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dataEvento,

        String emailUsuario,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime dataAlteracao,

        StatusNotificacaoEnum statusNotificacaoEnum
) {

}
