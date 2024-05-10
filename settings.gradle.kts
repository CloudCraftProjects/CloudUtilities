rootProject.name = "CloudUtilities"

listOf("velocity").forEach { name ->
    val projectName = "${rootProject.name}${name.replaceFirstChar { it.titlecase() }}"
    include(projectName)
    findProject(":${projectName}")!!.projectDir = file(name)
}

