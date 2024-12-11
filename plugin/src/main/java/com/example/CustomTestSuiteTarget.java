package com.example;

import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.testing.base.TestSuiteTarget;

public interface CustomTestSuiteTarget extends TestSuiteTarget, Named {
    Property<Boolean> getDemonstrateFailure();

    DirectoryProperty getBinaryResultsDirectory();

    DirectoryProperty getHtmlReportDirectory();
}
