import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.pubnub"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}


val confluentKafka = "7.3.3-ccs"
val jsonPathVer = "2.0.1"
val jacksonVer = "2.13.1"
val log4jslf4jVer = "2.17.2"

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:$log4jslf4jVer"))
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
    implementation("org.apache.logging.log4j:log4j-layout-template-json")

    implementation("com.nfeld.jsonpathkt:jsonpathkt:$jsonPathVer")

    implementation(platform("com.fasterxml.jackson:jackson-bom:$jacksonVer"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.apache.kafka:kafka-streams:$confluentKafka")
    implementation("org.apache.kafka:connect-api:$confluentKafka")
    implementation("org.apache.kafka:connect-json:$confluentKafka")
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