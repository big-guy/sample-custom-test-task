package com.example;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

/**
 * A custom library, stand-in for an Android Library.
 */
public interface CustomLibrary {
    @Nested
    CustomDependencies getDependencies();

    default void dependencies(Action<? super CustomDependencies> dependencies) {
        dependencies.execute(getDependencies());
    }
}
