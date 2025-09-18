package com.ks.taskmanager.controller;

import com.ks.taskmanager.entity.Task;
import com.ks.taskmanager.entity.User;
import com.ks.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Get tasks of the current user
    @GetMapping
    public List<Task> getTasks(){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findByUser(currentUser);
    }

    //Get task by id
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    // Create a task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        task.setUser(currentUser);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    // UPDATE task by id
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))
                .map(task -> {
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setCompleted(updatedTask.isCompleted());
                    return ResponseEntity.ok(taskRepository.save(task));
                }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.noContent().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

}
