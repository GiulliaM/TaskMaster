package br.ifsp.taskmaster.repository;

import br.ifsp.taskmaster.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMasterRepository extends JpaRepository<Task, Long> {

    Page<Task> findByCategoria(String categoria, Pageable pageable);
}
