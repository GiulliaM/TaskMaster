package br.ifsp.taskmaster.service.impl;

import br.ifsp.taskmaster.domain.model.Task;
import br.ifsp.taskmaster.dto.TaskMasterRequestDTO;
import br.ifsp.taskmaster.dto.TaskMasterResponseDTO;
import br.ifsp.taskmaster.exception.ResourceNotFoundException;
import br.ifsp.taskmaster.repository.TaskMasterRepository;
import br.ifsp.taskmaster.service.TaskMasterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskMasterServiceImpl implements TaskMasterService {

    private final TaskMasterRepository repository;

    public TaskMasterServiceImpl(TaskMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public TaskMasterResponseDTO create(TaskMasterRequestDTO dto) {
        Task task = toEntity(dto);
        return toResponse(repository.save(task));
    }

    @Override
    public Page<TaskMasterResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public TaskMasterResponseDTO findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada com id: " + id));
        return toResponse(task);
    }

    @Override
    public Page<TaskMasterResponseDTO> findByCategoria(String categoria, Pageable pageable) {
        return repository.findByCategoria(categoria, pageable).map(this::toResponse);
    }

    @Override
    public TaskMasterResponseDTO update(Long id, TaskMasterRequestDTO dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada com id: " + id));
        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        task.setCategoria(dto.getCategoria());
        task.setDataLimite(dto.getDataLimite());
        task.setPrioridade(dto.getPrioridade());
        return toResponse(repository.save(task));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Task não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }

    private Task toEntity(TaskMasterRequestDTO dto) {
        Task task = new Task();
        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        task.setCategoria(dto.getCategoria());
        task.setDataLimite(dto.getDataLimite());
        task.setPrioridade(dto.getPrioridade());
        return task;
    }

    private TaskMasterResponseDTO toResponse(Task task) {
        TaskMasterResponseDTO dto = new TaskMasterResponseDTO();
        dto.setId(task.getId());
        dto.setTitulo(task.getTitulo());
        dto.setDescricao(task.getDescricao());
        dto.setCategoria(task.getCategoria());
        dto.setDataLimite(task.getDataLimite());
        dto.setPrioridade(task.getPrioridade());
        return dto;
    }
}