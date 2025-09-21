package com.ks.taskmanager.service;

import com.ks.taskmanager.entity.Task;
import com.ks.taskmanager.entity.User;
import com.ks.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    public List<Task> getTasks(){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findByUser(currentUser);
    }

    public ResponseEntity<Task> getTaskById(Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    public ResponseEntity<Task> createTask(Task task){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        task.setUser(currentUser);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    public ResponseEntity<Task> updateTask(Long id, Task updatedTask){
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

    public ResponseEntity<Void> deleteTask(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.noContent().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
