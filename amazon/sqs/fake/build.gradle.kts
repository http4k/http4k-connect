val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
//    testImplementation(Libs.sqs)  FIXME why doesn't this work?
    testImplementation("software.amazon.awssdk:sqs:_")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
