package com.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.Usage;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

/**
 * Configure ecosystem level things for the "custom" ecosystem.
 */
public abstract class CustomEcosystemPlugin implements Plugin<Project> {
    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("org.gradle.test-report-aggregation", plugin -> {
            Configuration testReportAggregation =
                    project.getConfigurations().getByName("aggregateTestReportResults");
            testReportAggregation.attributes(attributes -> {
                attributes.attribute(Category.CATEGORY_ATTRIBUTE, getObjectFactory().named(Category.class, Category.LIBRARY));
                attributes.attribute(Usage.USAGE_ATTRIBUTE, getObjectFactory().named(Usage.class, "runtime"));
            });
        });
    }
}
