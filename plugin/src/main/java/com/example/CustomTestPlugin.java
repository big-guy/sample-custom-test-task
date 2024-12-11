package com.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testing.base.TestingExtension;

import java.util.Random;

/**
 * Registers a test task to demonstrate the {@code TestEventReporter} API.
 */
public abstract class CustomTestPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        project.getPluginManager().apply("org.gradle.reporting-base");
        project.getPluginManager().apply("org.gradle.test-suite-base");

        TestingExtension testing = project.getExtensions().getByType(TestingExtension.class);
        testing.getSuites().registerBinding(CustomTestSuite.class, CustomTestSuite.class);

        ReportingExtension reporting = project.getExtensions().getByType(ReportingExtension.class);

        // Register a couple of built-in test suite with two targets each
        testing.getSuites().register("passing", CustomTestSuite.class, suite -> {
            suite.getTargets().register("debug");
            suite.getTargets().register("release");
            suite.getTargets().configureEach(target -> {
                // The passing test suite always passes
                target.getDemonstrateFailure().convention(false);
            });
        });
        testing.getSuites().register("failing", CustomTestSuite.class, suite -> {
            suite.getTargets().register("debug");
            suite.getTargets().register("release");
            suite.getTargets().configureEach(target -> {
                // The failing test suite always fails
                target.getDemonstrateFailure().convention(true);
            });
        });

        Random random = new Random();
        // For every custom test suite
        testing.getSuites().withType(CustomTestSuite.class).all(suite -> {
            // For every target in the suite
            suite.getTargets().withType(CustomTestSuiteTarget.class).all(target -> {
                // Register a task
                String taskName = "test" + capitalize(suite.getName()) + capitalize(target.getName());
                TaskProvider<CustomTest> customTest = project.getTasks().register(taskName, CustomTest.class, task -> {
                    task.getTestSuiteTargetName().convention(capitalize(target.getName()));
                    task.getFail().convention(target.getDemonstrateFailure());

                    task.setGroup("verification");
                    task.setDescription("Runs the tests for " + target.getName());

                    // Configure where the results go
                    task.getBinaryResultsDirectory().convention(project.getLayout().getBuildDirectory().dir("test-results/" + task.getName()));
                    task.getHtmlReportDirectory().convention(reporting.getBaseDirectory().dir("tests/" + task.getName()));

                    // Enables Test UI in IntelliJ
                    task.getExtensions().getExtraProperties().set("idea.internal.test", true);
                    // Generate slightly different times to make the results look interesting
                    task.getWobble().convention(random.nextInt(-50, 50));
                });

                // Attach the output of the task back to the test suite target
                target.getBinaryResultsDirectory().convention(customTest.flatMap(CustomTest::getBinaryResultsDirectory));
                target.getHtmlReportDirectory().convention(customTest.flatMap(CustomTest::getHtmlReportDirectory));
            });
        });
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
