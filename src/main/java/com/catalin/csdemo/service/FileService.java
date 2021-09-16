package com.catalin.csdemo.service;

import com.catalin.csdemo.graph.File;
import com.catalin.csdemo.graph.Graph;
import com.catalin.csdemo.repository.FileReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final FileReferenceRepository fileReferenceRepository;

    public Set<String> getReferences(String fileId) {
        return fileReferenceRepository.findReferences(fileId);
    }

    public boolean isInvolvedInCycle(String fileId) {
        Graph graph = new Graph();
        File rootFile = new File(fileId);
        populateGraph(graph, rootFile);

        return graph.hasCycle(rootFile);
    }

    private void populateGraph(Graph graph, File rootFile) {
        graph.addNode(rootFile);
        Set<File> childrenFiles = fileReferenceRepository.findReferences(rootFile.getFileId())
                .stream()
                .map(File::new)
                .collect(Collectors.toSet());
        for (File possiblyDuplicateChildFile : childrenFiles) {
            File childFile = graph.getNodes().stream()
                    .filter(node -> node.equals(possiblyDuplicateChildFile))
                    .findFirst().orElse(possiblyDuplicateChildFile);
            graph.addEdge(rootFile, childFile);
            if (!graph.getNodes().contains(childFile)) {
                populateGraph(graph, childFile);
            }
        }
    }
}
