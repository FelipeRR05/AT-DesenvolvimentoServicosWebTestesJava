package br.com.taskmanager.service;

import br.com.taskmanager.dto.TaskCreateDTO;
import br.com.taskmanager.model.Task;
import br.com.taskmanager.repository.TaskRepository;
import io.javalin.http.NotFoundResponse;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Tarefa não encontrada com o ID: " + id));
    }

    public Task createTask(TaskCreateDTO taskDTO) {
        Task newTask = new Task();
        newTask.setId(UUID.randomUUID());
        newTask.setTitulo(taskDTO.getTitulo());
        newTask.setDescricao(taskDTO.getDescricao());
        newTask.setConcluida(false);
        newTask.setDataCriacao(ZonedDateTime.now());

        return taskRepository.save(newTask);
    }
}