package com.catalin.csdemo.service;

import com.catalin.csdemo.redis.RedisConnectionFactory;
import com.catalin.csdemo.repository.TaskRepository;
import com.catalin.csdemo.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    @Value("${task.queue}")
    private String taskQueue;

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public UUID startTask(String fileId) {
        UUID taskId = UUID.randomUUID();
        Task task = new Task(taskId, fileId);
        taskRepository.save(task);
        try (Jedis jedis = RedisConnectionFactory.getConnection()) {
            jedis.lpush(taskQueue, objectMapper.writeValueAsString(task));
        } catch (JsonProcessingException e) {
            String message = "Could not deserialize task with ID " + taskId;
            log.error(message, e);
            throw new RuntimeException(message);
        }

        return taskId;
    }

    public Task getTask(UUID taskId) {
        return taskRepository.getTask(taskId);
    }

}
