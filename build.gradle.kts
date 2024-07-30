import com.google.devtools.ksp.gradle.KspTask
import groovy.namespace.QName
import groovy.util.Node
import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.time.Duration

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    idea
//    jacoco
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
//    id("com.github.kt3k.coveralls") version "2.12.2"
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
//        classpath("com.github.kt3k.coveralls:com.github.kt3k.coveralls.gradle.plugin:_")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:_")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
//    apply(plugin = "org.gradle.jacoco")
//    apply(plugin = "com.github.kt3k.coveralls")
    apply(plugin = "com.google.devtools.ksp")
    apply(plugin = "java-test-fixtures")

    repositories {
        mavenCentral()
    }

    version = project.properties["releaseVersion"] ?: "LOCAL"
    group = "org.http4k"

//    jacoco {
//        toolVersion = "0.8.11"
//    }

    tasks {
        withType<KspTask> {
            outputs.upToDateWhen { false }
        }

        withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                allWarningsAsErrors = false
                jvmTarget.set(JVM_1_8)
            }
        }

        java {
            sourceCompatibility = VERSION_1_8
            targetCompatibility = VERSION_1_8
        }

        withType<Test> {
            useJUnitPlatform()
        }

//        if (hasCodeCoverage(project)) {
//            named<JacocoReport>("jacocoTestReport") {
//                reports {
//                    html.required.set(true)
//                    xml.required.set(true)
//                    csv.required.set(false)
//                }
//            }
//        }

        withType<GenerateModuleMetadata> {
            enabled = false
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
                testImplementation(project(path = ":http4k-connect-core-fake", configuration = "testArtifacts"))
                testImplementation(project(path = ":http4k-connect-storage-core", configuration = "testArtifacts"))
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

    val sourcesJar by tasks.creating(Jar::class) {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
        dependsOn(tasks.named("classes"))
    }

    val javadocJar by tasks.creating(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.named<Javadoc>("javadoc").get().destinationDir)
        dependsOn(tasks.named("javadoc"))
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

        val testJar by creating(Jar::class) {
            archiveClassifier.set("test")
            from(project.the<SourceSetContainer>()["test"].output)
        }

        configurations.create("testArtifacts") {
            extendsFrom(configurations["testApi"])
        }

        artifacts {
            add("testArtifacts", testJar)
            archives(sourcesJar)
            archives(javadocJar)
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

    if (hasAnArtifact(project)) {
        val enableSigning = project.findProperty("sign") == "true"

        apply(plugin = "maven-publish") // required to upload to sonatype

        if (enableSigning) { // when added it expects signing keys to be configured
            apply(plugin = "signing")
            signing {
                val signingKey: String? by project
                val signingPassword: String? by project
                useInMemoryPgpKeys(signingKey, signingPassword)
                sign(publishing.publications)
            }
        }

        publishing {
            val javaComponent = components["java"] as AdhocComponentWithVariants

            javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
            javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

            publications {
                val archivesBaseName = tasks.jar.get().archiveBaseName.get()
                create<MavenPublication>("mavenJava") {
                    artifactId = archivesBaseName
                    pom.withXml {
                        asNode().appendNode("name", archivesBaseName)
                        asNode().appendNode("description", description)
                        asNode().appendNode("url", "https://http4k.org")
                        asNode().appendNode("developers")
                            .appendNode("developer").appendNode("name", "Ivan Sanchez").parent()
                            .appendNode("email", "ivan@http4k.org")
                            .parent().parent()
                            .appendNode("developer").appendNode("name", "David Denton").parent()
                            .appendNode("email", "david@http4k.org")
                            .parent().parent()
                            .appendNode("developer").appendNode("name", "Albert Latacz").parent()
                            .appendNode("email", "albert@http4k.org")
                        asNode().appendNode("scm")
                            .appendNode("url", "https://github.com/http4k/http4k-connect").parent()
                            .appendNode("connection", "scm:git:git@github.com:http4k/http4k-connect.git").parent()
                            .appendNode("developerConnection", "scm:git:git@github.com:http4k/http4k-connect.git")
                        asNode().appendNode("licenses").appendNode("license")
                            .appendNode("name", "Apache License, Version 2.0").parent()
                            .appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.html")
                    }
                    from(components["java"])

                    artifact(sourcesJar)
                    artifact(javadocJar)
                }
            }
        }
    }

    sourceSets {
        test {
            kotlin.srcDir("$projectDir/src/examples/kotlin")
        }
    }
}

//tasks.register<JacocoReport>("jacocoRootReport") {
//    dependsOn(subprojects.map { it.tasks.named<Test>("test").get() })
//
//    sourceDirectories.from(subprojects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
//    classDirectories.from(subprojects.map { it.the<SourceSetContainer>()["main"].output })
//    executionData.from(subprojects
//        .filter { it.name != "http4k-bom" && hasAnArtifact(it) }
//        .map {
//            it.tasks.named<JacocoReport>("jacocoTestReport").get().executionData
//        }
//    )
//
//    reports {
//        html.required.set(true)
//        xml.required.set(true)
//        csv.required.set(false)
//        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/test/jacocoRootReport.xml"))
//    }
//}

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

tasks.register("listProjects") {
    doLast {
        subprojects
            .filter { hasAnArtifact(it) }
            .forEach { System.err.println(it.name) }
    }
}

fun Node.childrenCalled(wanted: String) = children()
    .filterIsInstance<Node>()
    .filter {
        val name = it.name()
        (name is QName) && name.localPart == wanted
    }

tasks.named<KotlinJvmCompile>("compileTestKotlin") {
    compilerOptions {
        jvmTarget.set(JVM_1_8)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

val nexusUsername: String? by project
val nexusPassword: String? by project

nexusPublishing {
    repositories {
        sonatype {
            username.set(nexusUsername)
            password.set(nexusPassword)
        }
    }
    transitionCheckOptions {
        maxRetries.set(150)
        delayBetween.set(Duration.ofSeconds(5))
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

//coveralls {
//    sourceDirs = subprojects.map { it.sourceSets.getByName("main").allSource.srcDirs }.flatten().map { it.absolutePath }
//    jacocoReportPath = file("${layout.buildDirectory}/reports/jacoco/test/jacocoRootReport.xml")
//}

//tasks.named<JacocoReport>("jacocoTestReport") {
//    afterEvaluate {
//        classDirectories.setFrom(classDirectories.files.map {
//            fileTree(it) {
//                exclude("**/Kotshi**/**")
//                exclude("**/**Extensions**")
//            }
//        })
//    }
//}
