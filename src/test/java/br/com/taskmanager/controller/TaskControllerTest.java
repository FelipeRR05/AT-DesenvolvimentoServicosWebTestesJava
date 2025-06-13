package br.com.taskmanager.controller;

import br.com.taskmanager.dto.TaskCreateDTO;
import br.com.taskmanager.model.Task;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.service.TaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TaskControllerTest {

    private Javalin app;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        TaskRepository taskRepository = new TaskRepository();
        TaskService taskService = new TaskService(taskRepository);
        TaskController taskController = new TaskController(taskService);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        app = Javalin.create();

        taskController.registerRoutes(app);
        registerBasicEndpoints(app);
    }

    @Test
    void testGetHelloEndpoint() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/hello");

            assertEquals(200, response.code());
            assertEquals("Hello, Javalin!", response.body().string());
        });
    }

    @Test
    void testPostTaskShouldReturn201() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "{\"titulo\": \"Testar endpoint de criacao\", \"descricao\": \"Verificar o status 201\"}";

            Response response = client.post("/tarefas", requestBody);

            assertEquals(201, response.code());

            assertNotNull(response.body());
        });
    }

    @Test
    void testGetTaskByIdAfterCreation() throws IOException {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "{\"titulo\": \"Buscar esta tarefa\", \"descricao\": \"Ela deve ser encontrada pelo ID\"}";
            Response createResponse = client.post("/tarefas", requestBody);
            assertEquals(201, createResponse.code());

            Task createdTask = objectMapper.readValue(createResponse.body().string(), Task.class);
            String newTaskId = createdTask.getId().toString();

            Response getResponse = client.get("/tarefas/" + newTaskId);

            assertEquals(200, getResponse.code());

            Task fetchedTask = objectMapper.readValue(getResponse.body().string(), Task.class);
            assertEquals(newTaskId, fetchedTask.getId().toString());
            assertEquals("Buscar esta tarefa", fetchedTask.getTitulo());
        });
    }

    @Test
    void testListTasksShouldReturnNonEmptyArrayAfterCreation() throws IOException {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "{\"titulo\": \"Listar esta tarefa\", \"descricao\": \"Ela deve aparecer na lista\"}";
            Response createResponse = client.post("/tarefas", requestBody);
            assertEquals(201, createResponse.code());

            Response listResponse = client.get("/tarefas");
            assertEquals(200, listResponse.code());

            List<Task> tasks = objectMapper.readValue(listResponse.body().string(), new TypeReference<List<Task>>() {});

            assertFalse(tasks.isEmpty());
            assertEquals(1, tasks.size());
            assertEquals("Listar esta tarefa", tasks.get(0).getTitulo());
        });
    }

    private void registerBasicEndpoints(Javalin app) {
        app.get("/hello", ctx -> ctx.result("Hello, Javalin!"));
        app.get("/status", ctx -> {
            Map<String, String> response = Map.of(
                    "status", "ok",
                    "timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            );
            ctx.json(response);
        });
        app.post("/echo", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            ctx.json(body);
        });
        app.get("/saudacao/{nome}", ctx -> {
            String nome = ctx.pathParam("nome");
            Map<String, String> response = Map.of("mensagem", "Olá, " + nome + "!");
            ctx.json(response);
        });
    }
}