package com.example;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.testing.GroupTestEventReporter;
import org.gradle.api.tasks.testing.TestEventReporter;
import org.gradle.api.tasks.testing.TestEventReporterFactory;
import org.gradle.api.tasks.testing.TestOutputEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom task that demonstrates the {@code TestEventReporter} API.
 */
public abstract class CustomTest extends DefaultTask {
    /**
     * CLI option to always generate a test failure when demonstrating the {@code TestEventReporter} API.
     *
     * @return property with value {@code true} if the task should demonstrate a test failure, {@code false} otherwise
     */
    @Input
    @Option(option="fail", description = "Tells the task to demonstrate failures.")
    public abstract Property<Boolean> getFail();

    /**
     * @return A property that adds some randomness to the test execution time.
     */
    @Input
    protected abstract Property<Integer> getWobble();

    /**
     * @return A property that specifies the name of the test suite target.
     */
    @Input
    protected abstract Property<String> getTestSuiteTargetName();

    /**
     * @return The directory where the binary test results are written.
     */
    @OutputDirectory
    protected abstract DirectoryProperty getBinaryResultsDirectory();

    /**
     * @return The directory where the HTML test report is written.
     */
    @OutputDirectory
    protected abstract DirectoryProperty getHtmlReportDirectory();

    @Inject
    protected abstract TestEventReporterFactory getTestEventReporterFactory();

    @TaskAction
    void runTests() throws IOException {
        // This task is a demonstration of generating the proper test events.
        // It simulates a variety of conditions and nesting levels

        // The API uses try-with-resources and AutoCloseable to enforce lifecycle checks
        // You can manually call close() on a reporter. Once closed or completed, a test/group cannot generate
        // more events
        try (GroupTestEventReporter root = getTestEventReporterFactory().createTestEventReporter(
            getName(),
            getBinaryResultsDirectory().get(),
            getHtmlReportDirectory().get()
        )) {
            root.started(Instant.now());

            String testSuiteTargetName = getTestSuiteTargetName().get();

            // Demonstrate attaching metadata properties to a group of tests (the root here)
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("testSuiteTargetName", testSuiteTargetName);
            metadata.put("wobble", getWobble().get());
            metadata.put("fail?", getFail().get());
            root.metadata(Instant.now(), metadata);

            // Demonstrate parallel execution
            GroupTestEventReporter worker1 = root.reportTestGroup("ParallelSuite1");
            GroupTestEventReporter worker2 = root.reportTestGroup("ParallelSuite2");
            GroupTestEventReporter worker3 = root.reportTestGroup("ParallelSuite3");

            try (worker1; worker2; worker3) {
                worker1.started(Instant.now());
                worker2.started(Instant.now());
                worker3.started(Instant.now());

                TestEventReporter test1 = worker1.reportTest("parallelTest1", "parallelTest1()");
                TestEventReporter test2 = worker2.reportTest("parallelTest2", "parallelTest2()");
                TestEventReporter test3 = worker2.reportTest("parallelTest3", "parallelTest3()");

                try (test1; test2; test3) {
                    test1.started(Instant.now());
                    test2.started(Instant.now());
                    test3.started(Instant.now());

                    // Simulate some activity
                    simulateWork(250);
                    test1.succeeded(Instant.now());

                    simulateWork(500);
                    test2.succeeded(Instant.now());

                    simulateWork(100);
                    test3.succeeded(Instant.now());
                }
                worker1.succeeded(Instant.now());
                worker2.succeeded(Instant.now());
                worker3.succeeded(Instant.now());
            }

            // If requested, demonstrate a failing test
            if (getFail().get()) {
                try (GroupTestEventReporter suite = root.reportTestGroup("FailingSuite")) {
                    suite.started(Instant.now());
                    try (TestEventReporter test = suite.reportTest("failingTest", "failingTest()")) {
                        test.started(Instant.now());
                        // Simulate some activity
                        simulateWork(300);

                        // Demonstrate attaching metadata to a failing test
                        test.metadata(Instant.now(), Collections.singletonMap("remoteService", URI.create("https://example.com")));

                        test.failed(Instant.now(), "This is a test failure");
                    }
                    suite.succeeded(Instant.now());
                }
            }

            // Demonstrate a test suite with multiple test outcomes
            // This has one level of nesting similar to JUnit
            try (GroupTestEventReporter suite = root.reportTestGroup("MyTestSuite")) {
                // Start a group of test cases
                suite.started(Instant.now());

                // Simulate 10 tests running
                for (int i=0; i<10; i++) {

                    try (TestEventReporter test = suite.reportTest("test" + i, "test(" + i + ")")) {
                        // Start an individual test case
                        test.started(Instant.now());

                        // Demonstrate attaching metadata to several tests
                        test.metadata(Instant.now(), Collections.singletonMap("index", i));

                        // Simulate some activity
                        simulateWork(250);

                        // Output must occur between started and terminal methods (succeeded, failed, skipped)
                        test.output(Instant.now(), TestOutputEvent.Destination.StdOut, "This is some standard output\n");
                        test.output(Instant.now(), TestOutputEvent.Destination.StdErr, "This is some standard error\n");

                        // Every 3 tests are considered skipped
                        if (i % 3 == 0) {
                            test.skipped(Instant.now());
                        } else {
                            test.succeeded(Instant.now());
                        }
                    }
                }
                // the suite needs to be completed after all tests
                suite.succeeded(Instant.now());
            }

            // Demonstrate a test that's specific to this test task
            // This makes the combined/aggregated tests more interesting
            try (GroupTestEventReporter suite = root.reportTestGroup(testSuiteTargetName + "Suite")) {
                // Start a group of test cases
                suite.started(Instant.now());

                // Simulate some tests running
                int length = testSuiteTargetName.length();
                for (int i = 0; i< length; i++) {

                    try (TestEventReporter test = suite.reportTest("test" + i, "test(" + i + ")")) {
                        // Start an individual test case
                        test.started(Instant.now());

                        // Simulate some activity
                        simulateWork(250);

                        // Every other tests are considered skipped
                        if (i % 2 == 0) {
                            test.skipped(Instant.now());
                        } else {
                            test.succeeded(Instant.now());
                        }
                    }
                }
                // the suite needs to be completed after all tests
                suite.succeeded(Instant.now());
            }

            if (getFail().get()) {
                root.failed(Instant.now());
            } else {
                root.succeeded(Instant.now());
            }
        }
    }

    private void simulateWork(int delay) {
        int wobble = getWobble().get();
        try {
            Thread.sleep(delay + wobble);
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
