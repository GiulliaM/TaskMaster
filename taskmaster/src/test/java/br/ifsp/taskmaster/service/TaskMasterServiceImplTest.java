package br.ifsp.taskmaster.service;

import br.ifsp.taskmaster.domain.model.Task;
import br.ifsp.taskmaster.dto.TaskMasterRequestDTO;
import br.ifsp.taskmaster.dto.TaskMasterResponseDTO;
import br.ifsp.taskmaster.exception.ApiException;
import br.ifsp.taskmaster.repository.TaskMasterRepository;
import br.ifsp.taskmaster.service.impl.TaskMasterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskMasterServiceImplTest {

    @Mock
    private TaskMasterRepository repository;

    @InjectMocks
    private TaskMasterServiceImpl service;

    private Task taskExistente;
    private TaskMasterRequestDTO requestValido;

    @BeforeEach
    void configurar() {
        taskExistente = new Task();
        taskExistente.setId(1L);
        taskExistente.setTitulo("Estudar Spring Boot");
        taskExistente.setDescricao("Revisar os conceitos de REST");
        taskExistente.setCategoria("Estudo");
        taskExistente.setDataLimite(LocalDate.now().plusDays(7));
        taskExistente.setPrioridade(3);

        requestValido = new TaskMasterRequestDTO();
        requestValido.setTitulo("Estudar Spring Boot");
        requestValido.setDescricao("Revisar os conceitos de REST");
        requestValido.setCategoria("Estudo");
        requestValido.setDataLimite(LocalDate.now().plusDays(7));
        requestValido.setPrioridade(3);
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("Deve criar e retornar a tarefa quando os dados são válidos")
    void deveCriarTarefaComSucesso() {
        // Arrange: o repositório vai simular que salvou e devolveu a tarefa com ID
        when(repository.save(any(Task.class))).thenReturn(taskExistente);

        // Act
        TaskMasterResponseDTO resposta = service.create(requestValido);

        // Assert: a resposta deve conter os dados da tarefa criada
        assertThat(resposta.getId()).isEqualTo(1L);
        assertThat(resposta.getTitulo()).isEqualTo("Estudar Spring Boot");
        assertThat(resposta.getCategoria()).isEqualTo("Estudo");
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao criar tarefa com data limite no passado")
    void deveLancarExcecaoAoCriarTarefaComDataNoPassado() {
        // Arrange: data no passado
        requestValido.setDataLimite(LocalDate.now().minusDays(1));

        // Act & Assert
        assertThatThrownBy(() -> service.create(requestValido))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("passado");
    }

    // ==================== FIND ALL ====================

    @Test
    @DisplayName("Deve retornar uma página com as tarefas cadastradas")
    void deveListarTodasAsTarefasPaginadas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> paginaSimulada = new PageImpl<>(List.of(taskExistente));
        when(repository.findAll(pageable)).thenReturn(paginaSimulada);

        // Act
        Page<TaskMasterResponseDTO> resultado = service.findAll(pageable);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getTitulo()).isEqualTo("Estudar Spring Boot");
    }

    // ==================== FIND BY ID ====================

    @Test
    @DisplayName("Deve retornar a tarefa quando o ID existe")
    void deveBuscarTarefaPorIdComSucesso() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(taskExistente));

        // Act
        TaskMasterResponseDTO resposta = service.findById(1L);

        // Assert
        assertThat(resposta.getId()).isEqualTo(1L);
        assertThat(resposta.getTitulo()).isEqualTo("Estudar Spring Boot");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existe")
    void deveLancarExcecaoAoBuscarTarefaInexistente() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: espera-se que a exceção seja lançada com mensagem adequada
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("99");
    }

    // ==================== FIND BY CATEGORIA ====================

    @Test
    @DisplayName("Deve retornar apenas as tarefas da categoria solicitada")
    void deveFiltrarTarefasPorCategoria() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> paginaSimulada = new PageImpl<>(List.of(taskExistente));
        when(repository.findByCategoria("Estudo", pageable)).thenReturn(paginaSimulada);

        // Act
        Page<TaskMasterResponseDTO> resultado = service.findByCategoria("Estudo", pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getCategoria()).isEqualTo("Estudo");
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("Deve atualizar e retornar a tarefa quando o ID existe")
    void deveAtualizarTarefaComSucesso() {
        // Arrange
        TaskMasterRequestDTO requestAtualizado = new TaskMasterRequestDTO();
        requestAtualizado.setTitulo("Estudar Testes");
        requestAtualizado.setDescricao("Aprender Mockito e MockMvc");
        requestAtualizado.setCategoria("Estudo");
        requestAtualizado.setDataLimite(LocalDate.now().plusDays(10));
        requestAtualizado.setPrioridade(5);

        Task taskAtualizada = new Task();
        taskAtualizada.setId(1L);
        taskAtualizada.setTitulo("Estudar Testes");
        taskAtualizada.setDescricao("Aprender Mockito e MockMvc");
        taskAtualizada.setCategoria("Estudo");
        taskAtualizada.setDataLimite(LocalDate.now().plusDays(10));
        taskAtualizada.setPrioridade(5);

        when(repository.findById(1L)).thenReturn(Optional.of(taskExistente));
        when(repository.save(any(Task.class))).thenReturn(taskAtualizada);

        // Act
        TaskMasterResponseDTO resposta = service.update(1L, requestAtualizado);

        // Assert
        assertThat(resposta.getTitulo()).isEqualTo("Estudar Testes");
        assertThat(resposta.getPrioridade()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar tarefa inexistente")
    void deveLancarExcecaoAoAtualizarTarefaInexistente() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.update(99L, requestValido))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("99");
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("Deve excluir a tarefa sem erros quando o ID existe")
    void deveExcluirTarefaComSucesso() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);

        // Act
        service.delete(1L);

        // Assert: verifica que o método de exclusão foi chamado exatamente uma vez
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar excluir tarefa inexistente")
    void deveLancarExcecaoAoExcluirTarefaInexistente() {
        // Arrange
        when(repository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }
}
