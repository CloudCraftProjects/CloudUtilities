import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.MppPlugin
import me.modmuss50.mpp.PublishModTask
import net.kyori.indra.IndraPlugin
import org.jetbrains.gradle.ext.IdeaExtPlugin
import java.nio.file.Files

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.run.velocity) apply false

    alias(libs.plugins.ideaext)
    alias(libs.plugins.indra)
    alias(libs.plugins.blossom) apply false

    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.publishing)
}

tasks.withType<Jar> {
    enabled = false
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply<IdeaExtPlugin>()
    apply<ShadowPlugin>()

    publishing {
        publications.create<MavenPublication>("maven") {
            artifactId = project.name.lowercase()
            from(components["java"])
        }
    }

    tasks {
        withType<Jar> {
            archiveBaseName = project.name
            destinationDirectory = rootProject.tasks.jar.map { it.destinationDirectory }.get()
        }

        assemble {
            dependsOn(project.tasks.withType<ShadowJar>())
        }
    }
}

allprojects {
    apply<IndraPlugin>()
    apply<MppPlugin>()

    group = "dev.booky"

    repositories {
        maven("https://repo.cloudcraftmc.de/public/")
    }

    indra {
        javaVersions {
            target(21)
        }
    }

    java {
        toolchain {
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }

    configure<ModPublishExtension> {
        val repositoryName = "CloudCraftProjects/CloudUtilities"
        changelog = "See https://github.com/$repositoryName/releases/tag/v${project.version}"
        type = if (project.version.toString().endsWith("-SNAPSHOT")) BETA else STABLE
        dryRun = !hasProperty("noDryPublish")

        if (project != rootProject) {
            file = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
            additionalFiles.from(tasks.sourcesJar.flatMap { it.archiveFile })
        } else {
            // this sadly needs a dummy file for github release parenting to properly work
            file = rootProject.layout.buildDirectory.file("empty.txt")
            val filePath = file.asFile.get().toPath()
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath.parent)
                Files.writeString(filePath, "dummy file")
            }
        }

        github {
            accessToken = providers.environmentVariable("GITHUB_API_TOKEN")
                .orElse(providers.gradleProperty("ccGithubToken"))

            displayName = "${rootProject.name} v${project.version}"

            repository = repositoryName
            commitish = "master"
            tagName = "v${project.version}"

            if (project != rootProject) {
                parent(rootProject.tasks.named("publishGithub"))
            }
        }
        if (rootProject != project) {
            modrinth {
                accessToken = providers.environmentVariable("MODRINTH_API_TOKEN")
                    .orElse(providers.gradleProperty("ccModrinthToken"))

                val platformName = project.projectDir.name
                val fancyPlatformName = platformName.replaceFirstChar { it.titlecaseChar() }
                version = "${project.version}+$platformName"
                displayName = "${rootProject.name} $fancyPlatformName v${project.version}"
                modLoaders.add(platformName)

                projectId = "dGVhuvFX"
                minecraftVersionRange {
                    start = rootProject.libs.versions.paper.get().split("-")[0]
                    end = "latest"
                }
            }
        }
    }

    if (rootProject != project) {
        tasks.withType<PublishModTask> {
            dependsOn(tasks.named<Jar>("shadowJar"))
            dependsOn(tasks.sourcesJar)
        }
    }
}
