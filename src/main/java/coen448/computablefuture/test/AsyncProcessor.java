package coen448.computablefuture.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {
    
    public CompletableFuture<String> processAsync(List<Microservice> microservices, String message) {
        List<CompletableFuture<String>> futures = microservices.stream()
            .map(client -> client.retrieveAsync(message))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" ")));
    }
    
    public CompletableFuture<List<String>> processAsyncCompletionOrder(
            List<Microservice> microservices, String message) {
        List<String> completionOrder = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = microservices.stream()
            .map(ms -> ms.retrieveAsync(message).thenAccept(completionOrder::add))
            .collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> completionOrder);
    }
    
    // TODO: Task A - Implement processAsyncFailFast (Fail-Fast policy)
        /**
     * Task A - Fail-Fast (Atomic Policy)
     * If any concurrent microservice invocation fails, the entire operation fails 
     * and no result is produced.
     */
    public CompletableFuture<String> processAsyncFailFast(
            List<Microservice> services, 
            List<String> messages) {
        
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            futures.add(services.get(i).retrieveAsync(messages.get(i)));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" ")));
    }
    // TODO: Task B - Implement processAsyncFailPartial (Fail-Partial policy)
    
    // TODO: Task C - Implement processAsyncFailSoft (Fail-Soft policy)
}