include(":http4k-kapt-generator")

includeWithName("http4k-connect-bom", "bom")
includeSystem("core")
includeStorage("jdbc")
includeStorage("redis")
includeStorage("s3")
includeStorage("http")

includeCommon("amazon-core", "amazon/core")
includeSystem("amazon", "kms")
includeSystem("amazon", "s3")
includeSystem("amazon", "secretsmanager")
includeSystem("amazon", "sqs")
includeSystem("amazon", "sts")
includeSystem("amazon", "systemsmanager")
includeSystem("example")
includeSystem("google", "analytics")

fun includeSystem(system: String) {
    val projectName = "http4k-connect-$system"
    includeWithName(projectName, "$system/client")
    includeWithName("$projectName-fake", "$system/fake")
}

fun includeSystem(owner: String, system: String) {
    val projectName = "http4k-connect-$owner-$system"
    includeWithName(projectName, "$owner/$system/client")
    includeWithName("$projectName-fake", "$owner/$system/fake")
}

fun includeCommon(projectName: String, file: String) {
    includeWithName("http4k-connect-$projectName", file)
}

fun includeWithName(projectName: String, file: String) {
    include(":$projectName")
    project(":$projectName").projectDir = File(file)
}

fun includeStorage(name: String) {
    includeWithName("http4k-connect-storage-$name", "storage/$name")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}
