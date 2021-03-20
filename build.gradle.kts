import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("java")
    application
}

group = "dev.remylavergne"
version = "0.1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.12.2")
    implementation("org.json:json:20201115")
    implementation("org.apache.commons:commons-text:1.9")

    testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
    testImplementation(kotlin("test-junit"))
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("io.kotest:kotest-assertions-core:4.4.3")
}

tasks.withType<Test> {
    useJUnit()
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "dev.remylavergne.subslator.MainKt"
}

tasks.shadowJar {
    archiveBaseName.set("shadow")
    archiveClassifier.set("")
    archiveVersion.set(version)
    manifest {
        attributes(mapOf("Main-Class" to application.mainClassName))
    }
}

