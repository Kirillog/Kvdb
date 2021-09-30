plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "ru.spbu.math-cs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}