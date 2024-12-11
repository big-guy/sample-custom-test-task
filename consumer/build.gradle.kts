plugins {
    application
}

description = "Demonstrates a Tooling API client that receives test events from a non-Test task"

repositories {
    maven {
        url = uri(file("repo"))
    }
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases")
    }
}

dependencies {
    // NOTE: This is not the final 8.13, but a checked-in nightly called 8.13
    implementation("org.gradle:gradle-tooling-api:8.13")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.10")
}

application {
    mainClass = "org.gradle.sample.Main"
}
tasks {
    "run"(JavaExec::class) {
        args(gradle.gradleHomeDir!!.absolutePath, gradle.parent!!.rootProject.rootDir.absolutePath)
    }
}
