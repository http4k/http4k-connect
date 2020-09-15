include("http4k-connect-bom")
includeSystem("example")
includeSystem("google", "analytics")

fun includeSystem(system: String) {
    val projectName = "http4k-connect-$system"
    include(
        ":$projectName",
        ":$projectName-fake"
    )
    project(":$projectName").projectDir = File("$system/client")
    project(":$projectName-fake").projectDir = File("$system/fake")
}

fun includeSystem(owner: String, system: String) {
    val projectName = "http4k-connect-$owner-$system"
    include(
        ":$projectName",
        ":$projectName-fake"
    )
    project(":$projectName").projectDir = File("$owner/$system/client")
    project(":$projectName-fake").projectDir = File("$owner/$system/fake")
}
