plugins {
    application
}

// tag::use-tooling-api[]
repositories {
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases")
    }
    mavenLocal()
}

dependencies {
    implementation("org.gradle:gradle-tooling-api:8.13-20241209223709+0000")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.10")
}
// end::use-tooling-api[]

application {
    mainClass = "org.gradle.sample.Main"
}
tasks {
    "run"(JavaExec::class) {
        args(gradle.gradleHomeDir!!.absolutePath, gradle.parent!!.rootProject.rootDir.absolutePath)
    }
}
