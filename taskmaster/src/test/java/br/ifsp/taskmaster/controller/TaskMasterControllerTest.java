package br.ifsp.taskmaster.controller;

import br.ifsp.taskmaster.domain.model.Task;
import br.ifsp.taskmaster.repository.TaskMasterRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskMasterControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMasterRepository repository;

    private Task taskSalva;

    @BeforeEach
    void limparEPrepararBanco() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll();

        Task task = new Task();
        task.setTitulo("Estudar Spring Boot");
        task.setDescricao("Revisar os conceitos de REST");
        task.setCategoria("Estudo");
        task.setDataLimite(LocalDate.now().plusDays(7));
        task.setPrioridade(3);

        taskSalva = repository.save(task);
    }

    // ==================== POST /tasks ====================

    @Test
    @DisplayName("POST /tasks deve retornar 201 e os dados da tarefa criada quando o JSON é válido")
    void deveCriarTarefaERetornar201() throws Exception {
        // Arrange: monta o JSON com dados válidos
        Map<String, Object> novaTask = Map.of(
                "titulo", "Fazer exercícios",
                "descricao", "30 minutos de caminhada",
                "categoria", "Saúde",
                "dataLimite", LocalDate.now().plusDays(1).toString(),
                "prioridade", 2
        );

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Fazer exercícios"))
                .andExpect(jsonPath("$.categoria").value("Saúde"));
    }

    @Test
    @DisplayName("POST /tasks deve retornar 400 quando o campo 'titulo' está ausente")
    void deveRetornar400QuandoTituloAusente() throws Exception {
        // Arrange: JSON sem o campo obrigatório 'titulo'
        Map<String, Object> taskSemTitulo = Map.of(
                "descricao", "Sem título",
                "categoria", "Estudo",
                "dataLimite", LocalDate.now().plusDays(1).toString(),
                "prioridade", 1
        );

        // Act & Assert: a API deve rejeitar e informar qual campo está inválido
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskSemTitulo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes.titulo").exists());
    }

    @Test
    @DisplayName("POST /tasks deve retornar 400 quando 'prioridade' está fora do intervalo permitido (1-5)")
    void deveRetornar400QuandoPrioridadeInvalida() throws Exception {
        // Arrange: prioridade com valor 10, que excede o máximo permitido (5)
        Map<String, Object> taskComPrioridadeInvalida = Map.of(
                "titulo", "Tarefa inválida",
                "descricao", "Prioridade errada",
                "categoria", "Teste",
                "dataLimite", LocalDate.now().plusDays(1).toString(),
                "prioridade", 10
        );

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskComPrioridadeInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes.prioridade").exists());
    }

    // ==================== GET /tasks ====================

    @Test
    @DisplayName("GET /tasks deve retornar 200 com metadados de paginação")
    void deveListarTarefasComMetadadosDePaginacao() throws Exception {
        // Act & Assert: verifica que a resposta inclui os metadados de paginação do Spring
        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /tasks?categoria=Estudo deve retornar somente as tarefas da categoria informada")
    void deveFiltrarTarefasPorCategoria() throws Exception {
        // Arrange: salva uma tarefa de categoria diferente para garantir o filtro
        Task outraTask = new Task();
        outraTask.setTitulo("Reunião de trabalho");
        outraTask.setDescricao("Alinhamento semanal");
        outraTask.setCategoria("Trabalho");
        outraTask.setDataLimite(LocalDate.now().plusDays(3));
        outraTask.setPrioridade(4);
        repository.save(outraTask);

        // Act & Assert: apenas a tarefa de "Estudo" deve aparecer
        mockMvc.perform(get("/tasks").param("categoria", "Estudo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].categoria").value("Estudo"));
    }

    @Test
    @DisplayName("GET /tasks?categoria=Inexistente deve retornar 200 com lista vazia")
    void deveRetornarListaVaziaParaCategoriaInexistente() throws Exception {
        mockMvc.perform(get("/tasks").param("categoria", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ==================== GET /tasks/{id} ====================

    @Test
    @DisplayName("GET /tasks/{id} deve retornar 200 e os dados da tarefa quando o ID existe")
    void deveBuscarTarefaPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/tasks/{id}", taskSalva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskSalva.getId()))
                .andExpect(jsonPath("$.titulo").value("Estudar Spring Boot"));
    }

    @Test
    @DisplayName("GET /tasks/{id} deve retornar 404 quando o ID não existe")
    void deveRetornar404AoBuscarIdInexistente() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ==================== PUT /tasks/{id} ====================

    @Test
    @DisplayName("PUT /tasks/{id} deve retornar 200 e a tarefa atualizada quando o ID existe")
    void deveAtualizarTarefaComSucesso() throws Exception {
        // Arrange: novos dados para substituir os da tarefa existente
        Map<String, Object> dadosAtualizados = Map.of(
                "titulo", "Estudar Testes Automatizados",
                "descricao", "Mockito e MockMvc",
                "categoria", "Estudo",
                "dataLimite", LocalDate.now().plusDays(14).toString(),
                "prioridade", 5
        );

        // Act & Assert
        mockMvc.perform(put("/tasks/{id}", taskSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Estudar Testes Automatizados"))
                .andExpect(jsonPath("$.prioridade").value(5));
    }

    @Test
    @DisplayName("PUT /tasks/{id} deve retornar 404 quando o ID não existe")
    void deveRetornar404AoAtualizarIdInexistente() throws Exception {
        Map<String, Object> dadosAtualizados = Map.of(
                "titulo", "Qualquer coisa",
                "descricao", "Qualquer coisa",
                "categoria", "Estudo",
                "dataLimite", LocalDate.now().plusDays(1).toString(),
                "prioridade", 1
        );

        mockMvc.perform(put("/tasks/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizados)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ==================== DELETE /tasks/{id} ====================

    @Test
    @DisplayName("DELETE /tasks/{id} deve retornar 204 sem corpo quando o ID existe")
    void deveExcluirTarefaERetornar204() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", taskSalva.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /tasks/{id} deve retornar 404 quando o ID não existe")
    void deveRetornar404AoExcluirIdInexistente() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
