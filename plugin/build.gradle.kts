plugins {
    `java-gradle-plugin`
}

description = "Gradle plugin that demonstrates test suites and the new test event reporter API"

gradlePlugin {
    plugins {
        create("customTest") {
            id = "com.example.custom-test"
            implementationClass = "com.example.CustomTestPlugin"
        }
    }
}
