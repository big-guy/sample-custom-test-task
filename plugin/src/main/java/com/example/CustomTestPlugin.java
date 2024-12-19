package com.example;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConsumableConfiguration;
import org.gradle.api.artifacts.DependencyScopeConfiguration;
import org.gradle.api.artifacts.ResolvableConfiguration;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.Usage;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testing.base.TestingExtension;

import javax.inject.Inject;
import java.util.Random;

/**
 * Registers a test task to demonstrate the {@code TestEventReporter} API.
 */
public abstract class CustomTestPlugin implements Plugin<Project> {
    @Inject
    protected abstract ObjectFactory getObjectFactory();

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
            // This is where dependencies are declared
            NamedDomainObjectProvider<DependencyScopeConfiguration> implementation = project.getConfigurations().dependencyScope("implementation" + capitalize(suite.getName()), configuration -> {
                configuration.fromDependencyCollector(suite.getDependencies().getImplementation());
            });
            // Automatically depend on the project's production runtime
            suite.getDependencies().getImplementation().add(suite.getDependencies().project());

            // For every target in the suite
            suite.getTargets().withType(CustomTestSuiteTarget.class).all(target -> {
                // This is the configuration that can be resolved to represent a classpath for running the tests
                NamedDomainObjectProvider<ResolvableConfiguration> runtimeClasspath = project.getConfigurations().resolvable("runtimeClasspath" + capitalize(suite.getName()) + capitalize(target.getName()), configuration -> {
                    configuration.extendsFrom(implementation.get());
                    configuration.attributes(attributes -> {
                        attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.LIBRARY));
                        attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, "runtime"));
                    });
                });
                target.getClasspath().from(runtimeClasspath);

                // Register a test task
                String taskName = "test" + capitalize(suite.getName()) + capitalize(target.getName());
                TaskProvider<CustomTest> customTest = project.getTasks().register(taskName, CustomTest.class, task -> {
                    task.setGroup("verification");
                    task.setDescription("Runs the tests for " + target.getName());
                    task.getTestSuiteTargetName().convention(capitalize(target.getName()));

                    // Simulated classpath for the test
                    task.getClasspath().from(target.getClasspath());

                    // Configure where the results go
                    task.getBinaryResultsDirectory().convention(project.getLayout().getBuildDirectory().dir("test-results/" + task.getName()));
                    task.getHtmlReportDirectory().convention(reporting.getBaseDirectory().dir("tests/" + task.getName()));

                    // Enables Test UI in IntelliJ
                    task.getExtensions().getExtraProperties().set("idea.internal.test", true);
                    // Generate slightly different times to make the results look interesting
                    task.getWobble().convention(random.nextInt(-50, 50));

                    // Demonstrate failures?
                    task.getFail().convention(target.getDemonstrateFailure());
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
