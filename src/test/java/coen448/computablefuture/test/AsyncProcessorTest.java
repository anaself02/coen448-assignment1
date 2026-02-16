package coen448.computablefuture.test;

import org.junit.jupiter.api.Test;

import main.java.coen448.computablefuture.test.AsyncProcessor;

import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.*;

public class AsyncProcessorTest {
    private static final long TIMEOUT_SECONDS = 2;
    
    @RepeatedTest(5)
    public void testNondeterminism() throws Exception {
        Microservice s1 = new Microservice("A");
        Microservice s2 = new Microservice("B");
        AsyncProcessor processor = new AsyncProcessor();
        List<String> order = processor.processAsyncCompletionOrder(
            List.of(s1, s2), "test").get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals(2, order.size());
        System.out.println("Order: " + order);
    }
    
    // ============================================================================
    // FAIL-FAST TESTS (Task A)
    // ============================================================================

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
    public void testFailFast_NoDeadlock() {
        Microservice s1 = new Microservice("S1");
        Microservice s2 = new Microservice("S2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailFast(
            List.of(s1, s2), List.of("msg1", "msg2"));
        assertDoesNotThrow(() -> future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }
    // TODO: Add tests for Fail-Partial policy (Task B)

    // ============================================================================
    // FAIL-PARTIAL TESTS (Task B)
    // ============================================================================

    @Test
    public void testFailPartial_AllServicesSucceed() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice s2 = new Microservice("S2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<List<String>> future = processor.processAsyncFailPartial(
            List.of(s1, s2), List.of("msg1", "msg2"));
        List<String> results = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.contains("S1:MSG1")));
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
    public void testFailPartial_NoExceptionEscapes() {
        Microservice failing = new FailingMicroservice("FAIL");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<List<String>> future = processor.processAsyncFailPartial(
            List.of(failing), List.of("msg1"));
        assertDoesNotThrow(() -> future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }
    // ============================================================================
    // FAIL-SOFT TESTS (Task C)
    // ============================================================================

    @Test
    public void testFailSoft_AllServicesSucceed() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice s2 = new Microservice("S2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailSoft(
            List.of(s1, s2), List.of("msg1", "msg2"), "FALLBACK");
        String result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(result.contains("S1:MSG1"));
        assertTrue(result.contains("S2:MSG2"));
        assertFalse(result.contains("FALLBACK"));
    }

    @Test
    public void testFailSoft_OneServiceFails_UsesFallback() throws Exception {
        Microservice s1 = new Microservice("S1");
        Microservice failing = new FailingMicroservice("FAIL");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailSoft(
            List.of(s1, failing), List.of("msg1", "msg2"), "FALLBACK");
        String result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(result.contains("S1:MSG1"));
        assertTrue(result.contains("FALLBACK"));
    }

    @Test
    public void testFailSoft_AlwaysCompletesNormally() {
        Microservice failing1 = new FailingMicroservice("FAIL1");
        Microservice failing2 = new FailingMicroservice("FAIL2");
        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> future = processor.processAsyncFailSoft(
            List.of(failing1, failing2), List.of("msg1", "msg2"), "FALLBACK");
        assertDoesNotThrow(() -> future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

}