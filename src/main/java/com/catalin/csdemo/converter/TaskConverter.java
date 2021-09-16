package com.catalin.csdemo.converter;

import com.catalin.csdemo.task.Task;
import com.catalin.csdemo.task.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class TaskConverter {

    public Task toTask(Map<String, String> hash) {
        String fileId = hash.get("fileId");
        UUID id = UUID.fromString(hash.get("id"));
        Task task = new Task(id, fileId);
        long creationDateMili = Long.valueOf(hash.get("creationDate"));
        task.setCreationDate(Instant.ofEpochMilli(creationDateMili));
        task.setStatus(TaskStatus.valueOf(hash.get("status")));
        task.setResults(new HashSet<>(Arrays.asList(hash.get("results").split(","))));

        return task;
    }

    public Map<String, String> toHash(Task task) {
        Map<String, String> hash = new HashMap<>();
        hash.put("id", task.getId().toString());
        hash.put("fileId", task.getFileId());
        hash.put("creationDate", String.valueOf(task.getCreationDate().toEpochMilli()));
        hash.put("status", task.getStatus().toString());
        hash.put("results", task.getResults().stream().map(s -> s + ",").reduce("", String::concat));

        return hash;
    }

}
