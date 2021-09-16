package com.catalin.csdemo.graph;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class File {

    private String fileId;
    private boolean beingVisited;
    private boolean visited;
    private Set<File> children = new HashSet<>();

    public File(String fileId) {
        this.fileId = fileId;
    }

    public void addChild(File adjacent) {
        this.children.add(adjacent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(fileId, file.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }
}