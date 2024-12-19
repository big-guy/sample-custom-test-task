plugins {
    `java-gradle-plugin`
}

description = "Gradle plugin that demonstrates test suites and the new test event reporter API"

gradlePlugin {
    plugins {
        create("customEcosystem") {
            id = "com.example.custom-ecosystem"
            implementationClass = "com.example.CustomEcosystemPlugin"
        }
        create("customApplication") {
            id = "com.example.custom-application"
            implementationClass = "com.example.CustomApplicationPlugin"
        }
        create("customLibrary") {
            id = "com.example.custom-library"
            implementationClass = "com.example.CustomLibraryPlugin"
        }
        create("customTest") {
            id = "com.example.custom-test"
            implementationClass = "com.example.CustomTestPlugin"
        }
    }
}
