include("http4k-connect-bom")
includeSystem("example")

fun includeSystem(name: String) {
    include(
        ":http4k-connect-$name",
        ":http4k-connect-$name-fake"
    )
    project(":http4k-connect-$name").projectDir = File("$name/http4k-connect-$name")
    project(":http4k-connect-$name-fake").projectDir = File("$name/http4k-connect-$name-fake")
}
