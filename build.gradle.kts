import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.serialization") version "1.9.10"
    kotlin("plugin.noarg") version "1.9.10"
    kotlin("plugin.jpa") version "1.9.10"
}

group = "app.hyuabot"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

noArg {
    annotation("app.hyuabot.backend.domain")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Connect to PostgreSQL using JDBC and JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql", "postgresql", "42.6.0")
    implementation("io.hypersistence", "hypersistence-utils-hibernate-62", "3.6.0")
    // Serialization
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // Security
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.4")
    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.1.4")
    // JWT
    implementation("io.jsonwebtoken:jjwt:0.12.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.2")
    // Log
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
