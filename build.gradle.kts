import com.google.devtools.ksp.gradle.KspTask
import org.http4k.internal.ModuleLicense.Apache2
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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

    apply(plugin = "java-test-fixtures")

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

        testFixturesApi(platform(Libs.junit_bom))
        testFixturesApi(Http4k.testing.hamkrest)
        testFixturesApi(Http4k.testing.approval)

        testFixturesApi(Testing.junit.jupiter.api)
        testFixturesApi(Testing.junit.jupiter.engine)
        testFixturesApi(platform(Libs.testcontainers_bom))
        testFixturesApi(Testing.junit.jupiter.params)
        testFixturesApi("org.testcontainers:junit-jupiter")
        testFixturesApi("org.testcontainers:testcontainers")
        testFixturesApi("dev.forkhandles:mock4k")

        when {
            project.name.endsWith("core-fake") -> {
                api(project(":http4k-connect-core"))
            }

            project.name.endsWith("fake") -> {
                api(project(":http4k-connect-core-fake"))
                api(project(":${project.name.substring(0, project.name.length - 5)}"))
                testFixturesApi(testFixtures(project(":${project.name.substring(0, project.name.length - 5)}")))
                testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
            }

            project.name.startsWith("http4k-connect-storage-core") -> {
                // bom - no code
            }

            project.name.startsWith("http4k-connect-storage") -> {
                api(project(":http4k-connect-storage-core"))
                testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
                testFixturesApi(testFixtures(project(":http4k-connect-storage-core")))
            }

            project.name == "http4k-connect" -> {
                rootProject.subprojects.forEach {
                    testFixturesApi(project(it.name))
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

                testFixturesApi(Libs.se_ansman_kotshi_compiler)
                testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
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

tasks.register("moveContractFilesToTestFixtures") {
    group = "Setup"
    description = "Moves files named '*Contract' from 'src/test' to 'src/testFixtures' in the same relative location."

    doLast {
        subprojects.forEach { project ->
            val testDir = project.projectDir.resolve("src/test/kotlin")
            val testFixturesDir = project.projectDir.resolve("src/testFixtures/kotlin")

            if (testDir.exists()) {
                // Find all files ending with 'Contract' in 'src/test'
                testDir.walkTopDown()
                    .filter { it.isFile && it.name.endsWith("Contract.kt") }
                    .forEach { file ->
                        // Calculate the relative path within 'src/test'
                        val relativePath = file.relativeTo(testDir)

                        // Define the target path within 'src/testFixtures'
                        val targetFile = testFixturesDir.resolve(relativePath)

                        // Ensure the target directory exists
                        targetFile.parentFile.mkdirs()

                        // Move the file
                        file.copyTo(targetFile, overwrite = true)
                        file.delete()

                        println("Moved ${file.path} to ${targetFile.path}")
                    }
            } else {
                println("No 'src/test' directory found in project ${project.name}")
            }
        }
    }
}
