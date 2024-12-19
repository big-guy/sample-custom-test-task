package com.example;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Simple representation of a build process
 */
public abstract class CustomBuild extends DefaultTask {
    @Input
    protected abstract Property<String> getType();

    @Input
    protected abstract Property<String> getContents();

    @OutputFile
    protected abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void build() throws IOException {
        System.out.println("Building...");
        Files.writeString(getOutputFile().get().getAsFile().toPath(), getContents().get());
        System.out.println("Built " + getOutputFile().get().getAsFile());
    }
}
