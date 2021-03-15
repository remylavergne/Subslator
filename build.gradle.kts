import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("java")
    application
}

group = "dev.remylavergne"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("com.github.ajalt.clikt:clikt:3.1.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.12.2")
    implementation("org.json:json:20201115")
    implementation("org.apache.commons:commons-text:1.9")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "MainKt"
}
/*
shadowJar {
    archiveBaseName.set("shadow")
    archiveClassifier.set("")
    archiveVersion.set("")
}
 */
