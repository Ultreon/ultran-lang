import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

val projectVersion = property("project_version")

group = "com.ultreon"
version =
    "${projectVersion}-${if (System.getenv("GITHUB_BUILD_NUMBER") == null) "local" else System.getenv("GITHUB_BUILD_NUMBER")}"

fun getViewVersion(): Any {
    return "${projectVersion}+${if (System.getenv("GITHUB_BUILD_NUMBER") == null) "local" else System.getenv("GITHUB_BUILD_NUMBER")}"
}

val buildDate: ZonedDateTime = ZonedDateTime.now()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation("com.google.code.gson:gson:2.10")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.processResources {
    inputs.dir("src/main/resources")

    inputs.property("version", getViewVersion())
    inputs.property("build_date", buildDate.format(DateTimeFormatter.RFC_1123_DATE_TIME))

    filesMatching("product.json") {
        expand(
            "version" to getViewVersion(),
            "build_date" to buildDate.format(DateTimeFormatter.RFC_1123_DATE_TIME),
        )
    }
}

application {
    mainClass.set("MainKt")
}