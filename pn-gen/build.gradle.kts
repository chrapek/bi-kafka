import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.pubnub"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kafkaApiVersion = "3.4.0"
val fakerVersion = "1.13.0"
dependencies {
    implementation("org.apache.kafka:kafka-clients:$kafkaApiVersion")
    implementation("io.github.serpro69:kotlin-faker:$fakerVersion")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}