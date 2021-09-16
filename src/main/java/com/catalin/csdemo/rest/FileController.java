package com.catalin.csdemo.rest;

import com.catalin.csdemo.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // LE: This was supposed to return parents, but it actually returns children.
    @GetMapping("/references/{fileId}")
    public Set<String> getTaskReferences(@PathVariable String fileId) {
        return fileService.getReferences(fileId);
    }

    @GetMapping("/is-involved-in-cycle/{fileId}")
    public boolean isInvolvedInCycle(@PathVariable String fileId) {
        return fileService.isInvolvedInCycle(fileId);
    }

}
