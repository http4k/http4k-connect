import com.google.devtools.ksp.gradle.KspTask

plugins {
    kotlin("jvm")
    id("org.http4k.project-metadata")
    id("org.http4k.nexus")
    id("com.google.devtools.ksp")
    `java-test-fixtures`
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.github.kt3k.coveralls:com.github.kt3k.coveralls.gradle.plugin:_")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:_")
    }
}

metadata {
    developers = mapOf(
        "David Denton" to "david@http4k.org",
        "Ivan Sanchez" to "ivan@http4k.org",
        "Albert Latacz" to "albert@http4k.org"
    )
}

subprojects {
    apply(plugin = "org.http4k.module")

    apply(plugin = "java-test-fixtures")

    apply(plugin = "com.google.devtools.ksp")

    tasks {
        withType<KspTask> {
            outputs.upToDateWhen { false }
        }
    }

    dependencies {
        api(platform("org.http4k:http4k-bom:${project.properties["http4k_version"]}")) // manually set because of auto-upgrading
        api(platform("dev.forkhandles:forkhandles-bom:_"))
        api(Http4k.core)
        api("dev.forkhandles:result4k")

        ksp("se.ansman.kotshi:compiler:_")

        testFixturesApi(platform("org.junit:junit-bom:_"))
        testFixturesApi(Http4k.testing.hamkrest)
        testFixturesApi(Http4k.testing.approval)

        testFixturesApi(Testing.junit.jupiter.api)
        testFixturesApi(Testing.junit.jupiter.engine)
        testFixturesApi(platform("org.testcontainers:testcontainers-bom:_"))
        testFixturesApi(Testing.junit.jupiter.params)
        testFixturesApi("org.testcontainers:junit-jupiter")
        testFixturesApi("org.testcontainers:testcontainers")
        testFixturesApi("dev.forkhandles:mock4k")

        when {
            project.name.endsWith("core-fake") -> {
            }

            project.name.endsWith("fake") -> {
                api(project(":http4k-connect-core-fake"))
                api(project(":${project.name.substring(0, project.name.length - 5)}"))
                testFixturesApi(testFixtures(project(":${project.name.substring(0, project.name.length - 5)}")))
                testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
            }

            project.name.startsWith("http4k-connect-storage-core") -> {
            }

            project.name.startsWith("http4k-connect-storage") -> {
                api(project(":http4k-connect-storage-core"))
                testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
                testFixturesApi(testFixtures(project(":http4k-connect-storage-core")))
            }

            project.name == "http4k-connect-bom" -> {
                // bom - no code
            }

            project.name == "http4k-connect-ksp-generator" -> {
            }

            project.name != "http4k-connect-core" -> {
                api(Http4k.cloudnative)
                api(project(":http4k-connect-core"))
                ksp(project(":http4k-connect-ksp-generator"))
                ksp("se.ansman.kotshi:compiler:_")

                testFixturesApi("se.ansman.kotshi:compiler:_")
                testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
            }
        }
    }

    tasks {
        named<Jar>("jar") {
            manifest {
                attributes(
                    mapOf(
                        "Implementation-Title" to project.name,
                        "Implementation-Vendor" to "org.http4k",
                        "Implementation-Version" to project.version
                    )
                )
            }
        }
    }

    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }

        test {
            kotlin.srcDir("src/examples/kotlin")
        }

        testFixtures {
            kotlin.srcDir("build/generated/ksp/testFixtures/kotlin")
            kotlin.srcDir("build/generated-testFixtures-avro-java")
        }
    }
}
