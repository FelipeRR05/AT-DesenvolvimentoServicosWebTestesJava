package br.com.taskmanager;

import br.com.taskmanager.controller.TaskController;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.service.TaskService;
import io.javalin.Javalin;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        var app = Javalin.create().start(7000);

        System.out.println("Servidor rodando em http://localhost:7000");

        var taskService = instantiateServices();

        new TaskController(taskService).registerRoutes(app);
        registerBasicEndpoints(app);
    }

    private static TaskService instantiateServices() {
        TaskRepository taskRepository = new TaskRepository();
        return new TaskService(taskRepository);
    }

    private static void registerBasicEndpoints(Javalin app) {
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