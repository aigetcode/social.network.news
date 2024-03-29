plugins {
    java
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.1"
    id("com.dorongold.task-tree") version "2.1.0"
    id("nebula.integtest") version "9.6.2"
    checkstyle
}

group = "ru.news"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

// version variables
val projectName = "news-service"
val springVersion = "3.0.3"
val lombokVersion = "1.18.24"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${springVersion}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${springVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web:${springVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator:${springVersion}")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.414")
    implementation("org.apache.commons:commons-io:1.3.2")
    implementation("org.liquibase:liquibase-core:4.18.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    runtimeOnly("org.postgresql:postgresql:42.5.1")

    // add for generate traceId and spanId
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.0.1")
    implementation("io.micrometer:micrometer-observation:1.10.4")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${springVersion}")
    testImplementation("com.h2database:h2")

    integTestImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.0.0")
    integTestImplementation("org.testcontainers:junit-jupiter:1.17.6")
    integTestImplementation("org.testcontainers:postgresql:1.17.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set(projectName)
}
tasks.bootJar {
    archiveBaseName.set(projectName)
}

checkstyle {
    toolVersion = "10.3.2"
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

jib.to.image = "social-news:${version}"
