package com.catalin.csdemo.repository;

import com.catalin.csdemo.redis.RedisConnectionFactory;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Set;

@Repository
public class FileReferenceRepository {

    private static final String FILE_CHILDREN_KEY_PREFIX = "children:";
    private static final String FILE_PARENTS_KEY_PREFIX = "parents:";

    public void saveFileReferences(String parentId, Set<String> children, Transaction jedis) {
        String childrenKey = makeChildrenKey(parentId);
        jedis.del(childrenKey);
        children.forEach(child -> {
            jedis.sadd(childrenKey, child);
            jedis.sadd(makeParentsKey(child), parentId);
        });
    }

    // LE: this method is meant to retrieve the CHILDREN of a file
    public Set<String> findReferences(String fileId) {
        try (Jedis jedis = RedisConnectionFactory.getConnection()) {
            return jedis.smembers(makeChildrenKey(fileId));
        }
    }

    private String makeChildrenKey(String fileId) {
        return FILE_CHILDREN_KEY_PREFIX + fileId;
    }

    private String makeParentsKey(String fileId) {
        return FILE_PARENTS_KEY_PREFIX + fileId;
    }
}
