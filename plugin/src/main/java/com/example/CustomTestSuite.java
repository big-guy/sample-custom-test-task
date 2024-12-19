package com.example;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.tasks.Nested;
import org.gradle.testing.base.TestSuite;

/**
 * A custom test suite.
 */
public interface CustomTestSuite extends TestSuite {
    /**
     * Configurable set of targets
     */
    @Override
    NamedDomainObjectContainer<CustomTestSuiteTarget> getTargets();

    /**
     * Custom dependencies block for a test suite
     */
    @Nested
    CustomDependencies getDependencies();

    default void dependencies(Action<? super CustomDependencies> dependencies) {
        dependencies.execute(getDependencies());
    }
}
