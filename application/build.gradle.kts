plugins {
    id("com.example.custom-test")
    id("org.gradle.test-report-aggregation")
    id("java")
}

version = "1.0.2"

dependencies {
    implementation(project(":library"))
}