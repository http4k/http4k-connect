plugins {
    kotlin("jvm")
}

kotlinProject()

description = "Http4k Bill Of Materials (BOM)"

dependencies {
    constraints {
        rootProject.subprojects
            .filter { it.name != project.name }
            .sortedBy { "$it.name" }
            .forEach { api(it) }
    }
}