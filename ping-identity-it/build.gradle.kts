import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

apply(plugin = "kotlin-spring")
apply(plugin = "io.gitlab.arturbosch.detekt")

dependencies {

    implementation("io.kubernetes:client-java")
    implementation("com.icegreen:greenmail")
    implementation("ch.qos.logback.contrib:logback-jackson")
    implementation("ch.qos.logback.contrib:logback-json-classic")
    implementation("ch.qos.logback:logback-classic")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.tomakehurst:wiremock-jre8")
    implementation("com.google.guava:guava")
    implementation("com.h2database:h2")
    implementation("com.willowtreeapps.assertk:assertk-jvm")
    implementation("io.jsonwebtoken:jjwt-api")
    implementation("io.jsonwebtoken:jjwt-impl")
    implementation("io.jsonwebtoken:jjwt-jackson")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.httpcomponents:httpclient")
    implementation("org.apache.httpcomponents:httpmime")
    implementation("org.hamcrest:java-hamcrest")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime")
    implementation("org.jetbrains.kotlin:kotlin-script-util")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.junit.platform:junit-platform-launcher")
    implementation("org.mockito:mockito-core")
    implementation("org.postgresql:postgresql")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver")
    implementation("org.seleniumhq.selenium:selenium-firefox-driver")
    implementation("org.seleniumhq.selenium:selenium-java")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.integration:spring-integration-mail")
    implementation("org.springframework.ws:spring-ws-core")
    implementation("org.springframework:spring-context")
}

sourceSets {
    create("integration") {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("src/integration/kotlin")
            resources.srcDir("src/integration/resources")
            compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
            runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}

configurations.named("integrationImplementation") {
    extendsFrom(configurations["implementation"])
}

configurations.named("integrationRuntimeOnly") {
    extendsFrom(configurations["runtimeOnly"])
}

tasks {
    val profiles = if (!project.hasProperty("profiles")) "local" else project.property("profiles")
    val imapPassword = if (!project.hasProperty("imapPassword")) "" else project.property("imapPassword")

    val integration by registering(Test::class) {
        description = "Runs the integration tests"
        group = "verification"

        systemProperties(
                Pair("spring.profiles.active", profiles),
                Pair("junit.jupiter.execution.parallel.enabled", "true"),
                Pair("junit.jupiter.execution.parallel.mode.default", "concurrent"),
                Pair("junit.jupiter.execution.parallel.config.fixed.parallelism", if (profiles == "qa") 1 else 8)
        )

        testClassesDirs = sourceSets["integration"].output.classesDirs
        classpath = sourceSets["integration"].runtimeClasspath
        mustRunAfter("test")
        useJUnitPlatform {
            includeEngines("junit-jupiter")
            excludeEngines("junit-vintage")
        }
        reports {
            junitXml.isEnabled = true
            html.isEnabled = false
        }
        testLogging {
            // set options for log level LIFECYCLE
            events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)

            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
            info.events = debug.events
            info.exceptionFormat = debug.exceptionFormat

            afterSuite(KotlinClosure2<TestDescriptor, TestResult, Any>({ desc, result ->
                // if (!desc.parent) { // will match the outermost suite
                val output =
                        "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                println(output)
                // }
            }))
        }
    }
}


