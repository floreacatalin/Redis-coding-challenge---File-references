package com.catalin.csdemo.worker;

import com.catalin.csdemo.exception.ResourceNotFoundException;
import com.catalin.csdemo.repository.TaskRepository;
import com.catalin.csdemo.task.Task;
import com.catalin.csdemo.task.TaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class Worker implements Runnable {

    private static final String UUID_REGEX = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b";
    private static final int BLOCKING_POP_TIMEOUT = 300;

    @Value("${task.queue}")
    private String taskQueue;

    @Value("${input.folder.path}")
    private String inputFolderPath;

    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run() {
        Jedis jedis = new Jedis();
        while (true) {
            List<String> popResult = jedis.blpop(BLOCKING_POP_TIMEOUT, taskQueue);
            if (popResult == null || popResult.isEmpty() || popResult.get(1) == null) {
                continue;
            }
            String message = popResult.get(1);
            log.info("Received message.");
            Task task = deserializeTask(message);
            processTask(task);
        }
    }

    private Task deserializeTask(String message) {
        try {
            return objectMapper.readValue(message, Task.class);
        } catch (JsonProcessingException e) {
            String errMsg = "Could not deserialize task.";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    private void processTask(Task task) {
        String filePath = inputFolderPath + "/" + task.getFileId();
        startTask(task);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while (line != null) {
                Arrays.stream(line.split(" "))
                        .filter(word -> word.matches(UUID_REGEX))
                        .forEach(task.getResults()::add);
                line = br.readLine();
            }
            completeTask(task);
        } catch (IOException ex) {
            handleProcesingException(ex, task);
        }
    }

    private void completeTask(Task task) {
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
        log.info("Finished processing file with ID " + task.getFileId());
    }

    private void startTask(Task task) {
        task.setStatus(TaskStatus.STARTED);
        taskRepository.save(task);
        log.info("Started processing file with ID " + task.getFileId());
    }


    private void handleProcesingException(IOException ex, Task task) {
        task.setStatus(TaskStatus.FAILED);
        taskRepository.save(task);
        if (ex instanceof FileNotFoundException) {
            String message = "File with ID " + task.getFileId() + " was not found.";
            log.error(message, ex);
            throw new ResourceNotFoundException(message);
        } else if (ex instanceof IOException) {
            String message = "File with ID " + task.getFileId() + " could not be parsed.";
            log.error(message, ex);
            throw new RuntimeException(message);
        } else {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }
}
