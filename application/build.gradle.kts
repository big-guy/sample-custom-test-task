plugins {
    id("com.example.custom-application")
    id("com.example.custom-test")
    // Aggregation plugin will aggregate all test results from projects this project depends on
    id("org.gradle.test-report-aggregation")
}

description = "An application project in the 'custom' ecosystem"

application {
    dependencies {
        implementation(project(":library"))
    }
}
