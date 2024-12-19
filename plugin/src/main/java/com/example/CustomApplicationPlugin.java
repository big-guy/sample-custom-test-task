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

import javax.inject.Inject;

public abstract class CustomApplicationPlugin  implements Plugin<Project> {
    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("com.example.custom-ecosystem");

        CustomApplication application = project.getExtensions().create("application", CustomApplication.class);

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
        });

        // This is the variant that can be resolved to represent a classpath for running the application
        NamedDomainObjectProvider<ResolvableConfiguration> runtimeClasspath = project.getConfigurations().resolvable("runtimeClasspath", configuration -> {
            configuration.extendsFrom(implementation.get());
            configuration.attributes(attributes -> {
                attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.LIBRARY));
                attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, "runtime"));
            });
        });

    }
}
