package com.catalin.csdemo.task;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Task {

    private UUID id;
    private Instant creationDate;
    private String fileId;
    private TaskStatus status;
    private Set<String> results = new HashSet<>();

    public Task(UUID id, String fileId) {
        this.id = id;
        this.fileId = fileId;
        this.creationDate = Instant.now();
        this.status = TaskStatus.CREATED;
    }

}

