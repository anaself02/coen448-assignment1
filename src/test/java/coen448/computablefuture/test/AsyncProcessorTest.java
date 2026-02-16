package coen448.computablefuture.test;

import org.junit.jupiter.api.Test;
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
    
    // TODO: Add tests for Fail-Fast policy (Task A)
    // TODO: Add tests for Fail-Partial policy (Task B)
    // TODO: Add tests for Fail-Soft policy (Task C)
}