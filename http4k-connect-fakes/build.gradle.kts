plugins {
    kotlin("jvm")
}

kotlinProject()

description = "Http4k Connect Fakes"

dependencies {
    constraints {
        rootProject.subprojects
            .filter { it.name != project.name }
            .sortedBy { "$it.name" }
            .forEach { api(it) }
    }
}