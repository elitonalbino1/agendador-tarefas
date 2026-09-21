package com.javanauta.agendadortarefas.controller;

import com.javanauta.agendadortarefas.business.TarefasService;
import com.javanauta.agendadortarefas.business.dto.TarefasDTORecord;
import com.javanauta.agendadortarefas.infrastructure.enums.StatusNotificacaoEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefasController {

    private final TarefasService tarefasService;

    @PostMapping
    public ResponseEntity<TarefasDTORecord> gravaTarefas(
            @Valid @RequestBody TarefasDTORecord dto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tarefasService.gravarTarefa(token, dto));
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<TarefasDTORecord>> buscaListaDeTarefasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFinal) {
        return ResponseEntity.ok(tarefasService.buscaTarefasAgendadasPorPeriodo(dataInicial, dataFinal));
    }

    @GetMapping
    public ResponseEntity<List<TarefasDTORecord>> buscarTarefasPorEmail(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(tarefasService.buscaTarefasPorEmail(token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletaTarefasPorId(@PathVariable("id") String id) {
        tarefasService.deletaTarefasPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TarefasDTORecord> alteraStatusNotificacao(
            @RequestParam("status") StatusNotificacaoEnum status,
            @PathVariable("id") String id) {
        return ResponseEntity.ok(tarefasService.alteraStatus(status, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefasDTORecord> updateTarefas(
            @Valid @RequestBody TarefasDTORecord dto,
            @PathVariable("id") String id) {
        return ResponseEntity.ok(tarefasService.updateTarefas(dto, id));
    }
}
