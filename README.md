# COEN448 Assignment 1 - Exception-Handling Policies in Concurrent Systems

## 📋 Project Overview

This project implements and tests three exception-handling policies for concurrent microservice systems using Java's `CompletableFuture` API:

1. **Fail-Fast (Atomic Policy)** - All or nothing
2. **Fail-Partial (Best-Effort Policy)** - Return successful results only
3. **Fail-Soft (Fallback Policy)** - Replace failures with fallback values

**Course:** COEN448/6761 - Software Testing and Validation  
**Instructor:** Yan Liu  
**Student:** Anas El Fali (40312567)  
**Date:** February 15, 2026

---

## 🎯 Key Features

- ✅ **3 failure policies** implemented with correct concurrency semantics
- ✅ **14 comprehensive unit tests** (no Mockito, all with timeouts)
- ✅ **100% test success rate** - BUILD SUCCESS
- ✅ **Professional Git workflow** - feature branches, PRs, code reviews
- ✅ **Complete documentation** - implementation details, use cases, trade-offs

---

## 📁 Project Structure

```
coen448-assignment1/
├── src/
│   ├── main/java/coen448/computablefuture/test/
│   │   ├── AsyncProcessor.java          # Main implementation
│   │   └── MicroService.java            # Service classes
│   └── test/java/coen448/computablefuture/test/
│       └── AsyncProcessorTest.java      # 14 comprehensive tests
├── docs/
│   └── failure-semantics.md             # Detailed documentation
├── pom.xml                               # Maven configuration
├── .gitignore                            # Git ignore rules
└── README.md                             # This file
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Clone the Repository
```bash
git clone https://github.com/anaself02/coen448-assignment1.git
cd coen448-assignment1
```

### Build the Project
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

**Expected Output:**
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 🧪 Testing Strategy

### Test Coverage (14 Tests)

| Policy | Tests | What's Tested |
|--------|-------|---------------|
| **Fail-Fast** | 3 tests | Success, exception propagation, liveness |
| **Fail-Partial** | 3 tests | Full success, partial success, no exceptions |
| **Fail-Soft** | 3 tests | Success, fallback usage, always completes |
| **Liveness** | 4 tests | Timeout enforcement, no deadlocks |
| **Nondeterminism** | 1 test | Completion order observation (5 repetitions) |

### Key Testing Principles

✅ **No Mockito** - Uses real `Microservice` and `FailingMicroservice` classes  
✅ **Timeout enforcement** - Every test uses `get(TIMEOUT_SECONDS, TimeUnit.SECONDS)`  
✅ **Exception assertions** - Proper use of `assertThrows(ExecutionException.class)`  
✅ **Liveness testing** - Verifies no deadlocks with `assertDoesNotThrow()`

### Example Test
```java
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
```

---

## 🔧 Git Workflow

This project follows professional feature-branch workflow:

```
main
 ├── feature/fail-fast      → PR #4 (merged)
 ├── feature/fail-partial   → PR #5 (merged)
 └── feature/fail-soft      → PR #6 (merged)
```

### Workflow Steps:
1. Create GitHub issue for each task
2. Create feature branch from `main`
3. Implement and commit changes
4. Push branch and create pull request
5. Code review with constructive comments
6. Merge PR (auto-closes issue)

---

## 📖 Documentation

Complete documentation available in [`docs/failure-semantics.md`](docs/failure-semantics.md):

- Detailed explanation of each policy
- Real-world use cases and examples
- Trade-offs and risk analysis
- Implementation strategies
- When to use each approach

---

## 🤖 AI Usage Disclosure

**Tool Used:** Claude Sonnet 4.5 by Anthropic  
**Interface:** Claude.ai web chat

**AI Assisted With:**
- Code structure suggestions for the three policies
- Testing strategy (avoiding Mockito)
- Documentation structure
- Git workflow guidance

**My Contributions:**
- Reviewed and understood all code
- Ran all tests and verified correctness
- Created GitHub repository and workflow
- Made all implementation decisions
- Wrote code review comments

---

## 📈 Test Results

```bash
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running coen448.computablefuture.test.AsyncProcessorTest
Order: [B:TEST, A:TEST]
Order: [A:TEST, B:TEST]
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 💡 Key Insights

> **"Concurrency is not challenging because tasks run in parallel; it is challenging when failure semantics are undefined."**

Each policy represents a deliberate design choice:
- **Fail-Fast** prioritizes correctness
- **Fail-Partial** balances pragmatism and reliability
- **Fail-Soft** maximizes availability

---

## 📝 License

This project is submitted as coursework for COEN448 at Concordia University.

---

## 👤 Author

**Anas El Fali**  
Student ID: 40312567  
Email: elfali.anas@gmail.com  
Course: COEN448 - Software Testing and Validation  
Concordia University - Winter 2026

---

## 🙏 Acknowledgments

- **Instructor:** Yan Liu
- **Course:** COEN448/6761 - Software Testing and Validation
- **Institution:** Concordia University, Gina Cody School of Engineering and Computer Science
