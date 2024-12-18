plugins {
    id("org.gradle.test-report-aggregation")
}

dependencies {
    testReportAggregation(project(":application"))
    testReportAggregation(project(":library"))
}

reporting {
    reports {
        register<AggregateTestReport>("aggregate") {
            testSuiteName = "failing"
        }
    }
}
