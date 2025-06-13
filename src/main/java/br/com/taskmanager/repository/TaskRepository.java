package br.com.taskmanager.repository;

import br.com.taskmanager.model.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {
    private final Map<UUID, Task> taskDatabase = new ConcurrentHashMap<>();

    public Task save(Task task) {
        taskDatabase.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(taskDatabase.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(taskDatabase.values());
    }
}