package com.example;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.tasks.Nested;

/**
 * Custom dependencies block available to "custom" components.
 */
public interface CustomDependencies extends Dependencies {
    /**
     * Declare implementation dependencies
     */
    @Nested
    DependencyCollector getImplementation();
}
