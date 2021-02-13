import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.gitlab.arturbosch.detekt")
    id("io.spring.dependency-management")
    id("org.springframework.boot") apply false
    kotlin("jvm") apply false
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
    idea
    java
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")

    group = "com.github.michaellif.ping"

    tasks.withType<Detekt> {
        failFast = false
        jvmTarget = "11"
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    repositories {
        maven { setUrl("https://repo1.maven.org/maven2") }
        maven { setUrl("https://packages.confluent.io/maven/") }
        mavenCentral()
        google()
        jcenter()
        maven { setUrl("https://mvnrepository.com/artifact") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlinx.html") }
    }

    dependencyManagement {
        imports {
            mavenBom("io.projectreactor:reactor-bom:${CoreVersion.REACTOR_BOM}")
            mavenBom("org.springframework.boot:spring-boot-starter-parent:${CoreVersion.SPRING_BOOT}")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${CoreVersion.SPRING_CLOUD}")
        }
        dependencies {
            dependency("ch.qos.logback.contrib:logback-jackson:${CoreVersion.LOGBACK_CONTRIB}")
            dependency("ch.qos.logback.contrib:logback-json-classic:${CoreVersion.LOGBACK_CONTRIB}")
            dependency("com.github.tomakehurst:wiremock-jre8:${CoreVersion.WIREMOCK_JRE8}")
            dependency("com.google.guava:guava:${CoreVersion.GUAVA}")
            dependency("com.h2database:h2:${CoreVersion.H2}")
            dependency("com.icegreen:greenmail:${CoreVersion.GREEN_MAIL}")
            dependency("com.willowtreeapps.assertk:assertk-jvm:${CoreVersion.ASSERTK_JVM}")
            dependency("io.jsonwebtoken:jjwt-api:${CoreVersion.JSONWEBTOKEN}")
            dependency("io.jsonwebtoken:jjwt-impl:${CoreVersion.JSONWEBTOKEN}")
            dependency("io.jsonwebtoken:jjwt-jackson:${CoreVersion.JSONWEBTOKEN}")
            dependency("io.projectreactor.kotlin:reactor-kotlin-extensions:${CoreVersion.REACTOR_KOTLIN_EXTENSIONS}")
            dependency("javax.validation:validation-api:${CoreVersion.JAVAX_VALIDATION}")
            dependency("org.apache.commons:commons-text:${CoreVersion.APACHE_COMMONS}")
            dependency("org.hamcrest:java-hamcrest:${CoreVersion.JAVA_HAMCREST}")
            dependency("org.hibernate:hibernate-jpamodelgen:${CoreVersion.JPAMODELGEN}")
            dependency("org.seleniumhq.selenium:selenium-chrome-driver:${CoreVersion.SELENIUM}")
            dependency("org.seleniumhq.selenium:selenium-firefox-driver:${CoreVersion.SELENIUM}")
            dependency("org.seleniumhq.selenium:selenium-java:${CoreVersion.SELENIUM}")
            dependency("io.kubernetes:client-java:${CoreVersion.KUBERNETES}")
            dependency("com.github.javafaker:javafaker:${CoreVersion.JAVAFAKER}")
            dependency("org.logback-extensions:logback-ext-loggly:${CoreVersion.LOGBACK_EXTENSIONS}")
            dependency("org.springframework:spring-mock:${CoreVersion.SPRING_MOCK}")
            dependency("io.confluent:kafka-avro-serializer:${CoreVersion.CONFLUENT}")
            dependency("org.apache.avro:avro:${CoreVersion.AVRO}")
        }
    }

    tasks {
        test {
            useJUnitPlatform {
                includeEngines("junit-jupiter")
                excludeEngines("junit-vintage")
            }
            testLogging.showStandardStreams = false
            jvmArgs("-Dspring.profiles.active=test")
        }
    }
}

