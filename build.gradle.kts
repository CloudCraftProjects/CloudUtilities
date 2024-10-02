import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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

    alias(libs.plugins.shadow) apply false
}

tasks.withType<Jar> {
    enabled = false
}

allprojects {
    apply<IndraPlugin>()

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
