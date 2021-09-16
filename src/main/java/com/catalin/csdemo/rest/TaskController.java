package com.catalin.csdemo.rest;

import com.catalin.csdemo.service.TaskService;
import com.catalin.csdemo.task.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/start-task-for-file-id/{fileId}")
    public UUID startTask(@PathVariable String fileId) {
        return taskService.startTask(fileId);
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable UUID taskId) {
        return taskService.getTask(taskId);
    }

}
