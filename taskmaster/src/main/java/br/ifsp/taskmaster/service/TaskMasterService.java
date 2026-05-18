package br.ifsp.taskmaster.service;

import br.ifsp.taskmaster.dto.TaskMasterRequestDTO;
import br.ifsp.taskmaster.dto.TaskMasterResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskMasterService {

    TaskMasterResponseDTO create(TaskMasterRequestDTO dto);

    Page<TaskMasterResponseDTO> findAll(Pageable pageable);

    TaskMasterResponseDTO findById(Long id);

    Page<TaskMasterResponseDTO> findByCategoria(String categoria, Pageable pageable);

    TaskMasterResponseDTO update(Long id, TaskMasterRequestDTO dto);

    void delete(Long id);
}