plugins {
    alias(libs.plugins.run.paper)
    alias(libs.plugins.pluginyml.bukkit)
}

val plugin: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    compileOnly(libs.paper.api)

    compileOnly(libs.cloudcore.bukkit)

    plugin(variantOf(libs.cloudcore.bukkit) { classifier("all") })
}

tasks {
    runServer {
        minecraftVersion(libs.versions.paper.map { it.split("-")[0] }.get())
        pluginJars.from(plugin.resolve())

        downloadPlugins {
            hangar("CommandAPI", libs.versions.commandapi.get())
            github(
                "PaperMC", "Debuggery",
                "v${libs.versions.debuggery.get()}",
                "debuggery-bukkit-${libs.versions.debuggery.get()}.jar"
            )
        }
    }

    withType<Jar> {
        manifest.attributes(
            "paperweight-mappings-namespace" to "mojang"
        )
    }
}

bukkit {
    name = rootProject.name
    main = "$group.cloudutilities.bukkit.CloudUtilitiesMain"
    apiVersion = "1.20.5"
    authors = listOf("booky10")
    website = "https://github.com/CloudCraftProjects/CloudUtilities"
    depend = listOf("CloudCore", "CommandAPI")
}
