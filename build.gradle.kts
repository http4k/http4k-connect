import com.google.devtools.ksp.gradle.KspTask
import org.http4k.internal.ModuleLicense.Apache2
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
    id("org.http4k.project-metadata")
    id("org.http4k.nexus")
    id("com.google.devtools.ksp")
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
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

allprojects {
    val license by project.extra { Apache2 }

    apply(plugin = "org.http4k.module")

    apply(plugin = "com.google.devtools.ksp")

    tasks {
        withType<KspTask> {
            outputs.upToDateWhen { false }
        }
    }

    dependencies {
        api(platform("org.http4k:http4k-bom:${project.properties["http4k_version"]}")) // manually set because of auto-upgrading
        api(platform(Libs.forkhandles_bom))
        api(Http4k.core)
        api("dev.forkhandles:result4k")

        ksp("se.ansman.kotshi:compiler:_")

        testImplementation(platform(Libs.junit_bom))
        testImplementation(Http4k.testing.hamkrest)
        testImplementation(Http4k.testing.approval)

        testImplementation(Testing.junit.jupiter.api)
        testImplementation(Testing.junit.jupiter.engine)
        testImplementation(platform(Libs.testcontainers_bom))
        testImplementation(Testing.junit.jupiter.params)
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:testcontainers")
        testImplementation("dev.forkhandles:mock4k")

        when {
            project.name.endsWith("core-fake") -> {
                api(project(":http4k-connect-core"))
            }

            project.name.endsWith("fake") -> {
                api(project(":http4k-connect-core-fake"))
                api(project(":${project.name.substring(0, project.name.length - 5)}"))
                testImplementation(
                    project(
                        path = ":${project.name.substring(0, project.name.length - 5)}",
                        configuration = "testArtifacts"
                    )
                )
                testImplementation(project(path = ":http4k-connect-core-fake", configuration = "testArtifacts"))
            }

            project.name.startsWith("http4k-connect-storage-core") -> {
                // bom - no code
            }

            project.name.startsWith("http4k-connect-storage") -> {
                api(project(":http4k-connect-storage-core"))
                testImplementation(testFixtures(project(":http4k-connect-core-fake")))
                testImplementation(testFixtures(project(":http4k-connect-storage-core")))
            }

            project.name == "http4k-connect" -> {
                rootProject.subprojects.forEach {
                    testImplementation(project(it.name))
                }
            }

            project.name == "http4k-connect-bom" -> {
                // bom - no code
            }

            project.name == "http4k-connect-kapt-generator" -> {
                api(project(":http4k-connect-core"))
            }

            project.name == "http4k-connect-ksp-generator" -> {
                api(project(":http4k-connect-core"))
                kspTest(project(":http4k-connect-ksp-generator"))
            }

            project.name != "http4k-connect-core" -> {
                api(Http4k.cloudnative)
                api(project(":http4k-connect-core"))
                ksp(project(":http4k-connect-ksp-generator"))
                ksp(Libs.se_ansman_kotshi_compiler)

                testImplementation(Libs.se_ansman_kotshi_compiler)
                testImplementation(project(path = ":http4k-connect-core-fake", configuration = "testArtifacts"))
            }
        }
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "idea")
    apply(plugin = "java-test-fixtures")

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

        configurations.create("testArtifacts") {
            extendsFrom(configurations["testApi"])
        }
    }

    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }

        test {
            kotlin.srcDir("src/examples/kotlin")
            kotlin.srcDir("build/generated/ksp/test/kotlin")
            kotlin.srcDir("build/generated-test-avro-java")
        }
    }

    sourceSets {
        test {
            kotlin.srcDir("$projectDir/src/examples/kotlin")
        }
    }
}

dependencies {
    subprojects
        .forEach {
            implementation(project(it.name))
        }

    implementation(platform(Libs.bom))
    implementation(libs.cloudfront)
    implementation(libs.cognitoidentityprovider)
    implementation(libs.dynamodb)
    implementation(libs.kms)
    implementation(libs.lambda)
    implementation(libs.s3)
    implementation(libs.secretsmanager)
    implementation(libs.ses)
    implementation(libs.sns)
    implementation(libs.sqs)
    implementation(libs.ssm)
    implementation(libs.sts)
}

fun hasAnArtifact(it: Project) = !it.name.contains("test-function") && !it.name.contains("integration-test")

sourceSets {
    test {
        kotlin.srcDir("$projectDir/src/docs")
        resources.srcDir("$projectDir/src/docs")
    }
}

tasks.named<KotlinJvmCompile>("compileTestKotlin") {
    compilerOptions {
        jvmTarget.set(JVM_1_8)
        freeCompilerArgs.add("-Xjvm-default=all")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
    }
}

dependencies {
    subprojects
        .forEach {
            implementation(project(it.name))
        }

    implementation(platform(Libs.bom))
    implementation(libs.cloudfront)
    implementation(libs.cognitoidentityprovider)
    implementation(libs.dynamodb)
    implementation(libs.kms)
    implementation(libs.lambda)
    implementation(libs.s3)
    implementation(libs.secretsmanager)
    implementation(libs.ses)
    implementation(libs.sns)
    implementation(libs.sqs)
    implementation(libs.ssm)
    implementation(libs.sts)
}

fun hasCodeCoverage(project: Project) = project.name != "http4k-connect-bom" &&
    !project.name.endsWith("generator")
