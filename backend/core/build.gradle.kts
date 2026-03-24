plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.spring)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.ktlint)
}

group = "me.pinfort"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(libs.samba)

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core") // Kotestを使う
    }
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)

    // testcontainers
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit5)
    testImplementation(libs.testcontainers.mariadb)

    testImplementation(libs.testcontainers.spring.boot)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(25)

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
