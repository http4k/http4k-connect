val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Http4k.template.pebble)
    implementation("de.sven-jacobs:loremipsum:_")
}
