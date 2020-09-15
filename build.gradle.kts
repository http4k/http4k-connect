plugins {
    base
    kotlin("jvm") version "1.3.72" apply false
}

allprojects {
    group = "org.http4k"

    version = if(project.hasProperty("releaseVersion")) project.property("releaseVersion")!!.toString() else "LOCAL"

    repositories {
        mavenCentral()
        jcenter()
    }
}

dependencies {
    subprojects.forEach {
        archives(it)
    }
}
