package com.example;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.testing.base.TestSuite;

public interface CustomTestSuite extends TestSuite {
    @Override
    NamedDomainObjectContainer<CustomTestSuiteTarget> getTargets();
}
