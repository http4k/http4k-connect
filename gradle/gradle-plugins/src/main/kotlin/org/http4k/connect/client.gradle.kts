package org.http4k.connect

import gradle.kotlin.dsl.accessors._0e3dbbf81313c38faa652e7693f66ab5.api
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.repositories

plugins {
    id("org.http4k.connect.module")
    id("com.google.devtools.ksp")
}

dependencies {
    api("org.http4k:http4k-cloudnative")
    api(project(":http4k-connect-core"))
    ksp(project(":http4k-connect-ksp-generator"))
    ksp("se.ansman.kotshi:compiler:_")

    testFixturesApi("se.ansman.kotshi:compiler:_")
    testFixturesApi(testFixtures(project(":http4k-connect-core-fake")))
}
