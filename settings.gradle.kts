pluginManagement {
    repositories {
        mavenLocal {
            url = uri("$rootDir/gradle/repo")
        }
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.http4k") {
                useModule("org.http4k:gradle-plugins:0.0.0.0")
            }
        }
    }
}

plugins {
    id("de.fayard.refreshVersions").version("0.60.5")
}

refreshVersions {
    enableBuildSrcLibs()

    rejectVersionIf {
        candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
    }
}

include(":tools")

include(":http4k-connect-ksp-generator")

includeWithName("http4k-connect-bom", "bom")
includeSystem("core")
includeStorage("core")
includeStorage("jdbc")
includeStorage("redis")
includeStorage("s3")
includeStorage("http")

includeCommon("ai-core", "ai/core")
includeVendorSystem("ai", "anthropic")
includeVendorSystem("ai", "azure")
includeVendorSystem("ai", "lmstudio")
includeVendorSystem("ai", "openai")
includeVendorSystem("ai", "ollama")

includeCommon("ai-langchain", "ai/langchain")
includeCommon("ai-openai-plugin", "ai/openai/plugin")

includeCommon("amazon-core", "amazon/core")
includeVendorSystem("amazon", "apprunner")
includeVendorSystem("amazon", "cloudfront")
includeVendorSystem("amazon", "cloudwatchlogs")
includeVendorSystem("amazon", "cognito")
includeVendorSystem("amazon", "containercredentials")
includeVendorSystem("amazon", "eventbridge")
includeVendorSystem("amazon", "firehose")
includeVendorSystem("amazon", "instancemetadata")
includeVendorSystem("amazon", "dynamodb")
includeVendorSystem("amazon", "iamidentitycenter")
includeVendorSystem("amazon", "kms")
includeVendorSystem("amazon", "lambda")
includeVendorSystem("amazon", "s3")
includeVendorSystem("amazon", "secretsmanager")
includeVendorSystem("amazon", "sns")
includeVendorSystem("amazon", "ses")
includeVendorSystem("amazon", "sqs")
includeVendorSystem("amazon", "sts")
includeVendorSystem("amazon", "systemsmanager")
includeVendorSystem("amazon", "evidently")
includeSystem("example")

includeSystem("github")
includeSystem("gitlab")
includeSystem("mattermost")
includeSystem("openai", "plugin")

includeCommon("langchain", "langchain")

includeVendorSystem("kafka", "rest")
includeVendorSystem("kafka", "schemaregistry")

includeCommon("google-analytics-core", "google/analytics-core")
includeVendorSystem("google", "analytics-ua")
includeVendorSystem("google", "analytics-ga4")

fun includeSystem(system: String, vararg extraModules: String) {
    val projectName = "http4k-connect-$system"
    includeWithName(projectName, "$system/client")
    includeWithName("$projectName-fake", "$system/fake")
    extraModules.forEach {
        includeWithName("$projectName-$it", "$system/$it")
    }
}

fun includeVendorSystem(owner: String, system: String) {
    val projectName = "http4k-connect-$owner-$system"
    includeWithName(projectName, "$owner/$system/client")
    includeWithName("$projectName-fake", "$owner/$system/fake")
}

fun includeCommon(projectName: String, file: String) {
    includeWithName("http4k-connect-$projectName", file)
}

fun includeWithName(projectName: String, file: String) {
    include(":$projectName")
    project(":$projectName").projectDir = File(file)
}

fun includeStorage(name: String) {
    includeWithName("http4k-connect-storage-$name", "storage/$name")
}
