package com.javanauta.agendadortarefas.business;

import com.javanauta.agendadortarefas.business.dto.TarefasDTO;
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

    public TarefasDTO gravarTarefa(String token, TarefasDTO dto) {
        // ✅ CORRIGIDO: extrai o EMAIL do token (antes pegava o token inteiro)
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        log.info("Gravando tarefa '{}' para usuário: {}", dto.getNomeTarefa(), email);

        dto.setEmailUsuario(email);
        dto.setDataCriacao(LocalDateTime.now());
        dto.setStatusNotificacaoEnum(StatusNotificacaoEnum.PENDENTE);

        TarefasEntity entity = tarefaConverter.paraTarefaEntity(dto);
        TarefasEntity salva = tarefasRepository.save(entity);

        log.info("Tarefa salva com ID: {}", salva.getId());
        return tarefaConverter.paraTarefaDTO(salva);
    }

    public List<TarefasDTO> buscaTarefasAgendadasPorPeriodo(LocalDateTime dataInicial,
                                                            LocalDateTime dataFinal) {
        log.info("Buscando tarefas PENDENTES entre {} e {}", dataInicial, dataFinal);
        List<TarefasEntity> tarefas = tarefasRepository
                .findByDataEventoBetweenAndStatusNotificacaoEnum(
                        dataInicial, dataFinal, StatusNotificacaoEnum.PENDENTE);
        log.info("Encontradas {} tarefas", tarefas.size());
        return tarefaConverter.paraListaTarefasDTO(tarefas);
    }

    public List<TarefasDTO> buscaTarefasPorEmail(String token) {
        // ✅ CORRIGIDO: extrai o EMAIL do token
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        log.info("Buscando tarefas do usuário: {}", email);

        List<TarefasEntity> listaTarefas = tarefasRepository.findByEmailUsuario(email);
        return tarefaConverter.paraListaTarefasDTO(listaTarefas);
    }

    public void deletaTarefasPorId(String id) {
        log.info("Deletando tarefa ID: {}", id);
        // ✅ CORRIGIDO: verifica existência (deleteById não lança exception)
        if (!tarefasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada: " + id);
        }
        tarefasRepository.deleteById(id);
        log.info("Tarefa deletada com sucesso");
    }

    public TarefasDTO alteraStatus(StatusNotificacaoEnum status, String id) {
        log.info("Alterando status da tarefa {} para {}", id, status);
        // ✅ CORRIGIDO: removido try/catch inútil
        TarefasEntity entity = tarefasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));

        entity.setStatusNotificacaoEnum(status);
        entity.setDataAlteracao(LocalDateTime.now()); // ✅ agora preenche dataAlteracao
        return tarefaConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }

    public TarefasDTO updateTarefas(TarefasDTO dto, String id) {
        log.info("Atualizando tarefa ID: {}", id);
        TarefasEntity entity = tarefasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));

        tarefaUpdateConverter.updateTarefas(dto, entity);
        entity.setDataAlteracao(LocalDateTime.now()); // ✅ agora preenche dataAlteracao
        return tarefaConverter.paraTarefaDTO(tarefasRepository.save(entity));
    }
}