import com.google.devtools.ksp.gradle.KspTask

plugins {
    kotlin("jvm")
    id("org.http4k.project-metadata")
    id("org.http4k.nexus")
    id("com.google.devtools.ksp")

    id("org.http4k.connect.module")
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
    apply(plugin = "org.http4k.connect.module")

    apply(plugin = "java-test-fixtures")

    apply(plugin = "com.google.devtools.ksp")

    tasks {
        withType<KspTask> {
            outputs.upToDateWhen { false }
        }
    }

    dependencies {
        ksp("se.ansman.kotshi:compiler:_")
    }

    dependencies {
        when {
            project.name.endsWith("core-fake") -> {
            }

            project.name.endsWith("fake") -> {
                apply(plugin = "org.http4k.connect.fake")
            }

            project.name.startsWith("http4k-connect-storage-core") -> {
            }

            project.name.startsWith("http4k-connect-storage") -> {
                apply(plugin = "org.http4k.connect.storage")
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

}
