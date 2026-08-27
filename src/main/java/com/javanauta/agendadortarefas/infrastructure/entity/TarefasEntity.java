package com.javanauta.agendadortarefas.infrastructure.entity;

import com.javanauta.agendadortarefas.infrastructure.enums.StatusNotificacaoEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "tarefas")
@CompoundIndexes({
        @CompoundIndex(name = "data_status_idx", def = "{'dataEvento': 1, 'statusNotificacaoEnum': 1}")
})
public class TarefasEntity {

    @Id
    private String id;

    private String nomeTarefa;

    private String descricaoTarefa;

    private LocalDateTime dataCriacao;

    @Indexed
    private LocalDateTime dataEvento;

    @Indexed
    private String emailUsuario;

    private LocalDateTime dataAlteracao;

    private StatusNotificacaoEnum statusNotificacaoEnum;
}