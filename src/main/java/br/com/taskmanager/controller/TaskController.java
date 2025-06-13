package br.com.taskmanager.controller;

import br.com.taskmanager.dto.TaskCreateDTO;
import br.com.taskmanager.service.TaskService;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

import java.util.UUID;

public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public void registerRoutes(Javalin app) {
        app.post("/tarefas", this::createTask);
        app.get("/tarefas", this::getAllTasks);
        app.get("/tarefas/{id}", this::getTaskById);
    }

    private void createTask(Context ctx) {
        TaskCreateDTO taskDTO = ctx.bodyValidator(TaskCreateDTO.class)
                .check(dto -> dto.getTitulo() != null && !dto.getTitulo().isBlank(), "O campo 'titulo' é obrigatório.")
                .get();

        var newTask = taskService.createTask(taskDTO);
        ctx.status(201).json(newTask);
    }

    private void getAllTasks(Context ctx) {
        ctx.json(taskService.getAllTasks());
    }

    private void getTaskById(Context ctx) {
        UUID taskId = parseUuid(ctx.pathParam("id"));
        ctx.json(taskService.getTaskById(taskId));
    }

    private UUID parseUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new BadRequestResponse("ID fornecido não é um UUID válido.");
        }
    }
}