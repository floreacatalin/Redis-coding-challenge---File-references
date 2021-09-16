package com.catalin.csdemo.graph;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Graph {

    private Set<File> nodes = new HashSet<>();

    public void addNode(File file) {
        this.nodes.add(file);
    }

    public void addEdge(File from, File to) {
        from.addChild(to);
    }

    public boolean hasCycle(File file) {
        file.setBeingVisited(true);
        for (File child : file.getChildren()) {
            if (child.isBeingVisited()) {
                return true;
            } else if (!child.isVisited() && hasCycle(child)) {
                return true;
            }
        }
        file.setBeingVisited(false);
        file.setVisited(true);

        return false;
    }

}