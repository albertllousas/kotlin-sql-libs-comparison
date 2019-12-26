import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"
}

repositories {
    jcenter()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jooq:jooq:3.12.3")
    implementation("org.jooq:jooq-codegen:3.12.3")
    implementation("org.jetbrains.exposed:exposed:0.17.7")
    implementation("org.slf4j:slf4j-simple:1.7.28")
    implementation("org.postgresql:postgresql:42.2.9")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("org.testcontainers:testcontainers:1.12.3")
    testImplementation("org.testcontainers:postgresql:1.12.3")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")
    testImplementation("com.github.javafaker:javafaker:1.0.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
}