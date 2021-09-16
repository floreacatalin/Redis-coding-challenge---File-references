package com.catalin.csdemo.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncWorkerService {

    private static final int NR_OF_WORKERS = 10;

    private final ApplicationContext applicationContext;

    private ExecutorService executor = Executors.newFixedThreadPool(NR_OF_WORKERS);

    @PostConstruct
    public void executeWorkersAsynchronously() {
        IntStream.rangeClosed(1, NR_OF_WORKERS)
                .forEach(i -> executor.execute(applicationContext.getBean(Worker.class)));
        log.info("Started all " + NR_OF_WORKERS + " workers asynchronously.");
    }

}
