package com.example;

import org.gradle.api.Action;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.Nested;

/**
 * A custom application, stand-in for an Android Application.
 */
public interface CustomApplication {
    /**
     * Custom dependencies block for application {}
     */
    @Nested
    CustomDependencies getDependencies();

    default void dependencies(Action<? super CustomDependencies> dependencies) {
        dependencies.execute(getDependencies());
    }

    /**
     * Primary output from the application
     */
    RegularFileProperty getMainOutput();
}
