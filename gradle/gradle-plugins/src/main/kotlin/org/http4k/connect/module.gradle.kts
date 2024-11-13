package org.http4k.connect

import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.`java-test-fixtures`
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform("org.http4k:http4k-bom:${project.properties["http4k_version"]}")) // manually set because of auto-upgrading
    api(platform("dev.forkhandles:forkhandles-bom:_"))
    api("org.http4k:http4k-core")
    api("dev.forkhandles:result4k")

    testFixturesApi(platform("org.junit:junit-bom:_"))
    testFixturesApi("org.http4k:http4k-testing-hamkrest")
    testFixturesApi("org.http4k:http4k-testing-approval")

    testFixturesApi("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testFixturesApi(platform("org.testcontainers:testcontainers-bom:_"))
    testFixturesApi("org.junit.jupiter:junit-jupiter-params")
    testFixturesApi("org.testcontainers:junit-jupiter")
    testFixturesApi("org.testcontainers:testcontainers")
    testFixturesApi("dev.forkhandles:mock4k")
}
