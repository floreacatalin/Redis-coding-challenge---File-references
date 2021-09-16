package com.catalin.csdemo.repository;

import com.catalin.csdemo.converter.TaskConverter;
import com.catalin.csdemo.exception.ResourceNotFoundException;
import com.catalin.csdemo.redis.RedisConnectionFactory;
import com.catalin.csdemo.task.Task;
import com.catalin.csdemo.task.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

    private static final String TASK_ID_PREFIX = "task:";

    private final TaskConverter taskConverter;
    private final FileReferenceRepository fileReferenceRepository;

    public void save(Task task) {
        try (Jedis jedis = RedisConnectionFactory.getConnection()) {
            Transaction transaction = jedis.multi();
            transaction.hmset(getRedisKey(task), taskConverter.toHash(task));
            if (TaskStatus.COMPLETED.equals(task.getStatus())) {
                fileReferenceRepository.saveFileReferences(task.getFileId(), task.getResults(), transaction);
            }
            transaction.exec();
        }
    }

    public Task getTask(UUID taskId) {
        try (Jedis jedis = RedisConnectionFactory.getConnection()) {
            Map<String, String> hash = jedis.hgetAll(getRedisKey(taskId));
            if (hash == null) {
                throw new ResourceNotFoundException("Task with ID " + taskId + " was not found.");
            }
            return taskConverter.toTask(hash);
        }
    }


    private String getRedisKey(Task task) {
        return getRedisKey(task.getId());
    }

    private String getRedisKey(UUID taskId) {
        return TASK_ID_PREFIX + taskId;
    }

}
