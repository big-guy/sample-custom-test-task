package com.example;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConsumableConfiguration;
import org.gradle.api.artifacts.DependencyScopeConfiguration;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.Usage;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * Define a custom library component.
 */
public abstract class CustomLibraryPlugin implements Plugin<Project> {
    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("com.example.custom-ecosystem");

        CustomLibrary library = project.getExtensions().create("library", CustomLibrary.class);
        TaskProvider<CustomBuild> buildLibrary = project.getTasks().register("buildLibrary", CustomBuild.class, task -> {
            task.getType().convention("library");
            task.getContents().convention("building the library for " + project.getPath());
            task.getOutputFile().convention(project.getLayout().getBuildDirectory().file("library"));
        });
        library.getMainOutput().convention(buildLibrary.flatMap(CustomBuild::getOutputFile));

        // This is where dependencies are declared
        NamedDomainObjectProvider<DependencyScopeConfiguration> implementation = project.getConfigurations().dependencyScope("implementation", configuration -> {
            configuration.fromDependencyCollector(library.getDependencies().getImplementation());
        });

        // This is the variant selected by other projects
        NamedDomainObjectProvider<ConsumableConfiguration> runtimeElements = project.getConfigurations().consumable("runtimeElements", configuration -> {
            configuration.extendsFrom(implementation.get());
            configuration.attributes(attributes -> {
                attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.LIBRARY));
                attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, "runtime"));
            });
            configuration.outgoing(outgoing -> {
                outgoing.artifact(library.getMainOutput());
            });
        });
    }
}
