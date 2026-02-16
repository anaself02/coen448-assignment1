package coen448.computablefuture.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.*;

public class AsyncProcessorTest {
    private static final long TIMEOUT_SECONDS = 2;
    
    @Test
    public void testFailFast_AllServicesSucceed() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice s2 = new Microservice("S2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailFast(
            List.of(s1, s2), List.of("msg1", "msg2"));
        String result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(result.contains("S1:MSG1"));
        assertTrue(result.contains("S2:MSG2"));
    }
    
    @Test
    public void testFailFast_OneServiceFails() {
        Microservice s1 = new Microservice("S1");
        Microservice failing = new FailingMicroservice("FAIL");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailFast(
            List.of(s1, failing), List.of("msg1", "msg2"));
        assertThrows(ExecutionException.class, 
            () -> future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }
    
    @Test
    public void testFailPartial_AllServicesSucceed() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice s2 = new Microservice("S2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<List<String>> future = processor.processAsyncFailPartial(
            List.of(s1, s2), List.of("msg1", "msg2"));
        List<String> results = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals(2, results.size());
    }
    
    @Test
    public void testFailPartial_OneServiceFails() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice failing = new FailingMicroservice("FAIL");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<List<String>> future = processor.processAsyncFailPartial(
            List.of(s1, failing), List.of("msg1", "msg2"));
        List<String> results = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("S1:MSG1"));
    }
    
    @Test
    public void testFailSoft_AllServicesSucceed() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice s2 = new Microservice("S2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailSoft(
            List.of(s1, s2), List.of("msg1", "msg2"), "FALLBACK");
        String result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(result.contains("S1:MSG1"));
        assertFalse(result.contains("FALLBACK"));
    }
    
    @Test
    public void testFailSoft_OneServiceFails() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice failing = new FailingMicroservice("FAIL");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailSoft(
            List.of(s1, failing), List.of("msg1", "msg2"), "FALLBACK");
        String result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(result.contains("S1:MSG1"));
        assertTrue(result.contains("FALLBACK"));
    }
    
    @RepeatedTest(5)
    public void testNondeterminism() throws Exception {
        Microservice s1 = new Microservice("A");
        Microservice s2 = new Microservice("B");
        AsyncProcessor processor = new AsyncProcessor();
        List<String> order = processor.processAsyncCompletionOrder(
            List.of(s1, s2), "test").get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals(2, order.size());
    }
}