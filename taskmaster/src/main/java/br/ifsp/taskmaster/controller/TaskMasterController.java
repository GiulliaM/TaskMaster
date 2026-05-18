package br.ifsp.taskmaster.controller;

import br.ifsp.taskmaster.dto.TaskMasterRequestDTO;
import br.ifsp.taskmaster.dto.TaskMasterResponseDTO;
import br.ifsp.taskmaster.service.TaskMasterService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskMasterController {

    private final TaskMasterService service;

    public TaskMasterController(TaskMasterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskMasterResponseDTO> create(@Valid @RequestBody TaskMasterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<TaskMasterResponseDTO>> findAll(
            @RequestParam(required = false) String categoria,
            Pageable pageable) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(service.findByCategoria(categoria, pageable));
        }
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskMasterResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskMasterResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskMasterRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}