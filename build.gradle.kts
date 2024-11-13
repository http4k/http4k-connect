plugins {
    kotlin("jvm")
    id("org.http4k.project-metadata")
    id("org.http4k.nexus")
    id("com.google.devtools.ksp")
    id("org.http4k.connect.module")
}

metadata {
    developers = mapOf(
        "David Denton" to "david@http4k.org",
        "Ivan Sanchez" to "ivan@http4k.org",
        "Albert Latacz" to "albert@http4k.org"
    )
}

subprojects {
    dependencies {
        when {
            project.name.endsWith("core-fake") -> {
            }

            project.name.endsWith("fake") -> {
                apply(plugin = "org.http4k.connect.fake")
            }

            project.name.startsWith("http4k-connect-storage-core") -> {
            }

            project.name.startsWith("http4k-connect-storage") -> {
                apply(plugin = "org.http4k.connect.storage")
            }

            project.name == "http4k-connect-bom" -> {
                // bom - no code
            }

            project.name == "http4k-connect-ksp-generator" -> {
            }

            project.name != "http4k-connect-core" -> {
                apply(plugin = "org.http4k.connect.client")
            }
        }
    }

}
