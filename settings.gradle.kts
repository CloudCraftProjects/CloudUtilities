rootProject.name = "CloudUtilities"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

listOf("velocity", "paper").forEach { name ->
    val projectName = "${rootProject.name}-${name.replaceFirstChar { it.titlecase() }}"
    include(projectName)
    findProject(":${projectName}")!!.projectDir = file(name)
}

