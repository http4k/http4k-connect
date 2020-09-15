include("http4k-connect-bom")
includeSystem("example")
includeSystem("google", "analytics")

fun includeSystem(system: String) {
    val projectName = "http4k-connect-$system"
    include(
        ":$projectName",
        ":$projectName-fake"
    )
    project(":$projectName").projectDir = File("$system/$projectName")
    project(":$projectName-fake").projectDir = File("$system/$projectName-fake")
}

fun includeSystem(owner: String, system: String) {
    val projectName = "http4k-connect-$owner-$system"
    include(
        ":$projectName",
        ":$projectName-fake"
    )
    project(":$projectName").projectDir = File("$owner/$system/$projectName")
    project(":$projectName-fake").projectDir = File("$owner/$system/$projectName-fake")
}
