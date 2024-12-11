package com.example;

import org.gradle.api.NamedDomainObjectContainer;
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
}
