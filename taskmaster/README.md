# TaskMaster API

API RESTful para gerenciamento de tarefas, desenvolvida com Spring Boot.

## Tecnologias

- Java 25
- Spring Boot 4
- Spring Data JPA
- MySQL (produção) / H2 (testes)
- Bean Validation (Jakarta)
- Springdoc OpenAPI (Swagger)
- JUnit 6 + Mockito

## Pré-requisitos

- Java 25+
- Maven
- MySQL rodando localmente na porta `3306`

## Configuração do banco de dados

O banco `taskmaster` é criado automaticamente na primeira execução. Verifique as credenciais em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskmaster?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

## Como executar

```bash
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

## Documentação interativa (Swagger)

Após subir a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

## Endpoints

### Tarefas

| Método | URL | Descrição | Status de sucesso |
|--------|-----|-----------|-------------------|
| `POST` | `/tasks` | Criar nova tarefa | `201 Created` |
| `GET` | `/tasks` | Listar tarefas (paginado) | `200 OK` |
| `GET` | `/tasks?categoria={valor}` | Filtrar por categoria | `200 OK` |
| `GET` | `/tasks/{id}` | Buscar tarefa por ID | `200 OK` |
| `PUT` | `/tasks/{id}` | Atualizar tarefa | `200 OK` |
| `DELETE` | `/tasks/{id}` | Excluir tarefa | `204 No Content` |

### Paginação e ordenação

Os endpoints de listagem aceitam os parâmetros do Spring Pageable:

```
GET /tasks?page=0&size=10&sort=dataLimite,asc
```

### Exemplo de corpo para criação/atualização

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar os conceitos de REST e JPA",
  "categoria": "Estudo",
  "dataLimite": "2026-12-31",
  "prioridade": 3
}
```

> `prioridade` aceita valores de **1** (menor) a **5** (maior).

### Exemplo de resposta

```json
{
  "id": 1,
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar os conceitos de REST e JPA",
  "categoria": "Estudo",
  "dataLimite": "2026-12-31",
  "prioridade": 3
}
```

### Respostas de erro

```json
{
  "status": 404,
  "erro": "Recurso não encontrado",
  "detalhes": "Task não encontrada com id: 99",
  "timestamp": "2026-05-17T10:00:00"
}
```

| Status | Situação |
|--------|----------|
| `400` | Dados inválidos ou campo obrigatório ausente |
| `404` | Recurso não encontrado |
| `500` | Erro interno do servidor |

## Validações

| Campo | Regras |
|-------|--------|
| `titulo` | Obrigatório |
| `descricao` | Obrigatória |
| `categoria` | Obrigatória |
| `dataLimite` | Obrigatória, não pode ser no passado |
| `prioridade` | Obrigatória, entre 1 e 5 |

## Testes

Os testes usam H2 (banco em memória) e não exigem MySQL.

```bash
./mvnw test
```

### Estrutura dos testes

```
src/test/java/br/ifsp/taskmaster/
├── service/
│   └── TaskMasterServiceImplTest.java   # Testes unitários (Mockito)
└── controller/
    └── TaskMasterControllerTest.java    # Testes de integração (MockMvc + H2)
```

- **Unitários**: testam a lógica do serviço de forma isolada, simulando o repositório com Mockito
- **Integração**: testam os endpoints de ponta a ponta com contexto Spring completo e banco H2

## Estrutura do projeto

```
src/main/java/br/ifsp/taskmaster/
├── controller/        # Recebe requisições HTTP e delega ao serviço
├── service/           # Lógica de negócio
│   └── impl/
├── repository/        # Acesso ao banco de dados
├── domain/model/      # Entidade JPA
├── dto/               # Objetos de transferência de dados
└── exception/         # Tratamento centralizado de erros
```
