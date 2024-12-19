plugins {
    id("org.gradle.test-report-aggregation")
}

description = "Dedicated project to aggregate test results from a list of projects"

dependencies {
    testReportAggregation(project(":application"))
    testReportAggregation(project(":library"))
}

reporting {
    reports {
        // Aggregate all test suites with the name "failing"
        register<AggregateTestReport>("aggregateFailing") {
            testSuiteName = "failing"
        }
    }
}
