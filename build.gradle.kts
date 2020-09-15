plugins {
    base
    kotlin("jvm") version "1.3.72" apply false
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.jfrog.bintray")
        plugin("maven")
        plugin("maven-publish")
    }

    group = "org.http4k"

    version = if (project.hasProperty("releaseVersion")) project.property("releaseVersion")!!.toString() else "LOCAL"

    repositories {
        mavenCentral()
        jcenter()
    }

    kotlinProject()

    publishing {
        publications {
            create<MavenPublication>(project.name) {
                groupId = "org.http4k"
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])

                pom.withXml {
                    asNode().apply {
                        appendNode("name", "archivesBaseName")
                        appendNode("description", description)
                        appendNode("url", "https://http4k.org")
                        appendNode("developers")
                            .appendNode("developer").appendNode("name", "Ivan Sanchez").parent().appendNode("email", "ivan@http4k.org")
                            .parent().parent()
                            .appendNode("developer").appendNode("name", "David Denton").parent().appendNode("email", "david@http4k.org")
                            .parent().parent()
                            .appendNode("developer").appendNode("name", "Albert Latacz").parent().appendNode("email", "albert@http4k.org")
                        appendNode("scm").appendNode("url", "git@github.com:http4k/http4k-connect.git").parent().appendNode("connection", "scm:git:git@github.com:http4k/http4k.git").parent().appendNode("developerConnection", "scm:git:git@github.com:http4k/http4k.git")
                        appendNode("licenses").appendNode("license").appendNode("name", "Apache License, Version 2.0").parent().appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.html")
                    }
                }
            }
        }

        bintray {
            user = System.getenv("BINTRAY_USER")
            key = System.getenv("BINTRAY_KEY")
            publish = true

            setPublications(project.name)

            pkg.apply {
                repo = "maven"
                userOrg = "http4k"
                name = project.name
                desc = description
                websiteUrl = "https://http4k.org"
                issueTrackerUrl = "https://github.com/http4k/http4k/issues"
                vcsUrl = "https://github.com/http4k/http4k.git"
                setLicenses("Apache-2.0")
                publicDownloadNumbers = true

                version.apply {
                    name = project.version.toString()
                    vcsTag = project.version.toString()
                    gpg.apply {
                        sign = true
                    }
                    mavenCentralSync.apply {
                        sync = false
                        user = System.getenv("SONATYPE_USER")
                        password = System.getenv("SONATYPE_KEY")
                        close = "1"
                    }
                }
            }
        }
    }
}
