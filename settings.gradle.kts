includeSystem("core")
includeWithName("http4k-connect-bom", "bom")
includeStorage("jdbc")
includeStorage("redis")
includeStorage("s3")
includeStorage("http")

includeSystem("amazon", "s3")
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

fun includeWithName(projectName: String, file: String) {
    include(":$projectName")
    project(":$projectName").projectDir = File(file)
}

fun includeStorage(name: String) {
    includeWithName("http4k-connect-storage-$name", "storage/$name")
}
