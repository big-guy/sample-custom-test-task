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
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

public abstract class CustomApplicationPlugin  implements Plugin<Project> {
    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("com.example.custom-ecosystem");

        CustomApplication application = project.getExtensions().create("application", CustomApplication.class);

        // Build process to create the application
        TaskProvider<CustomBuild> buildApplication = project.getTasks().register("buildApplication", CustomBuild.class, task -> {
            task.getType().convention("application");
            task.getContents().convention("building the application for " + project.getPath());
            task.getOutputFile().convention(project.getLayout().getBuildDirectory().file("application"));
        });
        application.getMainOutput().convention(buildApplication.flatMap(CustomBuild::getOutputFile));

        // This is where dependencies are declared
        NamedDomainObjectProvider<DependencyScopeConfiguration> implementation = project.getConfigurations().dependencyScope("implementation", configuration -> {
            configuration.fromDependencyCollector(application.getDependencies().getImplementation());
        });

        // This is the variant selected by other projects and test report aggregation
        NamedDomainObjectProvider<ConsumableConfiguration> runtimeElements = project.getConfigurations().consumable("runtimeElements", configuration -> {
            configuration.extendsFrom(implementation.get());
            configuration.attributes(attributes -> {
                attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.LIBRARY));
                attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, "runtime"));
            });
            configuration.outgoing(outgoing -> {
                outgoing.artifact(application.getMainOutput());
            });
        });

        // This is the configuration that can be resolved to represent a classpath for running the application
        NamedDomainObjectProvider<ResolvableConfiguration> runtimeClasspath = project.getConfigurations().resolvable("runtimeClasspath", configuration -> {
            configuration.extendsFrom(implementation.get());
            configuration.attributes(attributes -> {
                attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.LIBRARY));
                attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, "runtime"));
            });
        });
    }
}
