plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    api(Kotlin.gradlePlugin)
    api(gradleApi())
    api("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:_")
}
