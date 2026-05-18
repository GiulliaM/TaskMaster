package br.ifsp.taskmaster.controller;

import br.ifsp.taskmaster.dto.TaskMasterRequestDTO;
import br.ifsp.taskmaster.dto.TaskMasterResponseDTO;
import br.ifsp.taskmaster.service.TaskMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Gerenciamento de tarefas")
public class TaskMasterController {

    private final TaskMasterService service;

    public TaskMasterController(TaskMasterService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma nova tarefa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campo obrigatório ausente")
    })
    @PostMapping
    public ResponseEntity<TaskMasterResponseDTO> create(@Valid @RequestBody TaskMasterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Listar tarefas com paginação e filtro opcional por categoria")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<TaskMasterResponseDTO>> findAll(
            @RequestParam(required = false) String categoria,
            Pageable pageable) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(service.findByCategoria(categoria, pageable));
        }
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Buscar uma tarefa pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskMasterResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Atualizar uma tarefa existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskMasterResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskMasterRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Excluir uma tarefa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
