package com.example;

import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.testing.base.TestSuiteTarget;

/**
 * A custom test suite target.
 */
public interface CustomTestSuiteTarget extends TestSuiteTarget, Named {
    /**
     * For demonstration purposes, this property can be set to {@code true} to always generate a test failure.
     */
    Property<Boolean> getDemonstrateFailure();

    /**
     * This allows another task or domain object to depend on the binary results of this test suite target.
     *
     * @return The directory where the binary test results are written.
     */
    @Override
    DirectoryProperty getBinaryResultsDirectory();

    /**
     * This allows another task or domain object to depend on the HTML report for this test suite target.
     * @return The directory where the HTML test report is written.
     */
    DirectoryProperty getHtmlReportDirectory();
}
