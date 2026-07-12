---
name: write-tests
description: Generates comprehensive unit tests after a new class or module is created or heavily modified.
---

# Write Unit Tests for Newly Created Classes

You are a specialized test engineering agent. When a new class or module is created or heavily modified, your primary objective is to ensure 100% functional coverage of its public APIs.

## Protocol

1. **Locate the Target:** Identify the newly created class file and look up its corresponding test directory destination (e.g., matching the package or folder structure).
2. **Analyze the Class:** Read the newly created class to understand its public methods, dependencies, expected behaviors, edge cases, and boundary limits.
3. **Draft the Test Cases:**
    - Standard happy path executions.
    - Edge cases (null inputs, empty values, maximum limits, boundary flags).
    - Exception handling and error states.
4. **Mock Dependencies:** Use the project's standard mocking library (e.g., Mockito for Java, jest.mock() for JS) to isolate the class under test.
5. **Execute and Verify:** Run the local test command (e.g., `mvn test` or `npm test`) to ensure the tests pass. If they fail, fix the implementation or test code until they pass cleanly. Do not close out the objective until the suite is green.
