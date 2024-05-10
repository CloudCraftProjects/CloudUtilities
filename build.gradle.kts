import net.kyori.indra.IndraPlugin
import org.jetbrains.gradle.ext.IdeaExtPlugin

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.run.velocity) apply false

    alias(libs.plugins.ideaext)
    alias(libs.plugins.indra)
    alias(libs.plugins.blossom) apply false
}

tasks["jar"].enabled = false

allprojects {
    apply<IndraPlugin>()

    group = "dev.booky"
    version = "1.4.0"

    repositories {
        mavenLocal {
            content {
                includeModuleByRegex("dev\\.booky", "cloudcore.*")
            }
        }
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
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply<IdeaExtPlugin>()

    publishing {
        publications.create<MavenPublication>("maven") {
            artifactId = project.name.lowercase()
            from(components["java"])
        }
        repositories.maven("https://repo.cloudcraftmc.de/releases") {
            name = "horreo"
            credentials(PasswordCredentials::class)
        }
    }

    tasks {
        withType<Jar> {
            archiveBaseName = project.name
            destinationDirectory = rootProject.tasks.jar.map { it.destinationDirectory }.get()
        }
    }
}
