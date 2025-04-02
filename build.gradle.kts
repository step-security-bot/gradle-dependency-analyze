import com.gradle.publish.PublishTask

plugins {
    id("java")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.3.1"
    id("jacoco")
    id("pl.droidsonroids.jacoco.testkit") version "1.0.12"
    id("com.diffplug.spotless") version "7.0.2"
    id("com.adarshr.test-logger") version "4.0.0"
}

group = "ca.cutterslade.gradle"
version = "1.11.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.maven.shared:maven-dependency-analyzer:1.15.1") {
        exclude(group = "org.apache.maven")
    }
    implementation("org.apache.commons:commons-collections4:4.4")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    testImplementation("io.github.java-diff-utils:java-diff-utils:4.15")
    testImplementation("commons-io:commons-io:2.18.0")
    testImplementation(platform("org.junit:junit-bom:5.12.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<PublishTask>("publishPlugins") {
    dependsOn(tasks.check)
}

gradlePlugin {
    website = "https://github.com/gradle-dependency-analyze/gradle-dependency-analyze"
    vcsUrl = "https://github.com/gradle-dependency-analyze/gradle-dependency-analyze.git"
    plugins {
        create("dependencyAnalyze") {
            id = "ca.cutterslade.analyze"
            displayName = "Gradle Dependency Analyze"
            description = "Dependency analysis plugin for gradle. This plugin attempts to replicate the functionality" +
                " of the maven dependency plugin's analyze goals which fail the build if dependencies are declared" +
                " but not used or used but not declared."
            tags = listOf("dependency", "verification", "analyze")
            implementationClass = "ca.cutterslade.gradle.analyze.AnalyzeDependenciesPlugin"
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
    reports {
        html.required = true
        csv.required = true
    }
}

jacoco {
    toolVersion = "0.8.12"
}

spotless {
    java {
        googleJavaFormat()
    }
    kotlinGradle {
        ktlint()
    }
}
