package com.javanauta.agendadortarefas.business;

import com.javanauta.agendadortarefas.business.dto.TarefasDTORecord;
import com.javanauta.agendadortarefas.business.mapper.TarefaUpdateConverter;
import com.javanauta.agendadortarefas.business.mapper.TarefasConverter;
import com.javanauta.agendadortarefas.infrastructure.entity.TarefasEntity;
import com.javanauta.agendadortarefas.infrastructure.enums.StatusNotificacaoEnum;
import com.javanauta.agendadortarefas.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.agendadortarefas.infrastructure.repository.TarefasRepository;
import com.javanauta.agendadortarefas.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final TarefasConverter tarefaConverter;
    private final TarefaUpdateConverter tarefaUpdateConverter;
    private final JwtUtil jwtUtil;

    public TarefasDTORecord gravarTarefa(String token, TarefasDTORecord dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        log.info("Gravando tarefa '{}' para usuário: {}", dto.nomeTarefa(), email);

        // dtoFinal com valores ajustados
        TarefasDTORecord dtoFinal = new TarefasDTORecord(
                dto.id(),
                dto.nomeTarefa(),
                dto.descricaoTarefa(),
                LocalDateTime.now(), // dataCriacao
                dto.dataEvento(),
                email, // emailUsuario
                null, // dataAlteracao
                StatusNotificacaoEnum.PENDENTE
        );

        TarefasEntity entity = tarefaConverter.paraTarefaEntity(dtoFinal);
        TarefasEntity salva = tarefasRepository.save(entity);

        log.info("Tarefa salva com ID: {}", salva.getId());
        return tarefaConverter.paraTarefaDTO(salva);
    }

    public List<TarefasDTORecord> buscaTarefasAgendadasPorPeriodo(LocalDateTime dataInicial,
                                                                  LocalDateTime dataFinal) {
        log.info("Buscando tarefas PENDENTES entre {} e {}", dataInicial, dataFinal);
        List<TarefasEntity> tarefas = tarefasRepository
                .findByDataEventoBetweenAndStatusNotificacaoEnum(
                        dataInicial, dataFinal, StatusNotificacaoEnum.PENDENTE);
        log.info("Encontradas {} tarefas", tarefas.size());
        return tarefaConverter.paraListaTarefasDTORecord(tarefas);
    }

    public List<TarefasDTORecord> buscaTarefasPorEmail(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        log.info("Buscando tarefas do usuário: {}", email);

        List<TarefasEntity> listaTarefas = tarefasRepository.findByEmailUsuario(email);
        return tarefaConverter.paraListaTarefasDTORecord(listaTarefas);
    }

    public void deletaTarefasPorId(String id) {
        log.info("Deletando tarefa ID: {}", id);
        if (!tarefasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada: " + id);
        }
        tarefasRepository.deleteById(id);
        log.info("Tarefa deletada com sucesso");
    }

    public TarefasDTORecord alteraStatus(StatusNotificacaoEnum status, String id) {
        log.info("Alterando status da tarefa {} para {}", id, status);
        TarefasEntity entity = tarefasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));

        entity.setStatusNotificacaoEnum(status);
        entity.setDataAlteracao(LocalDateTime.now());
        return tarefaConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }

    public TarefasDTORecord updateTarefas(TarefasDTORecord dto, String id) {
        log.info("Atualizando tarefa ID: {}", id);
        TarefasEntity entity = tarefasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));

        // dtoFinal para manter consistência
        TarefasDTORecord dtoFinal = new TarefasDTORecord(
                dto.id(),
                dto.nomeTarefa(),
                dto.descricaoTarefa(),
                entity.getDataCriacao(), // mantém data original
                dto.dataEvento(),
                dto.emailUsuario(),
                LocalDateTime.now(), // dataAlteracao atualizada
                dto.statusNotificacaoEnum()
        );

        tarefaUpdateConverter.updateTarefas(dtoFinal, entity);
        entity.setDataAlteracao(LocalDateTime.now());
        return tarefaConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }
}
