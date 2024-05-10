plugins {
    id("java-library")
    id("maven-publish")
}

tasks["jar"].enabled = false

allprojects {
    group = "dev.booky"
    version = "1.4.0"

    repositories {
        maven("https://repo.cloudcraftmc.de/public/")
    }
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()

    java {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            artifactId = "${rootProject.name}-${project.name}".lowercase()
            from(components["java"])
        }
    }
}
