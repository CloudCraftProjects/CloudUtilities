plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-velocity") version "2.2.0"
}

group = "dev.booky"
version = "1.4.0"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

val velocityVersion = "3.2.0-SNAPSHOT"

dependencies {
    api("io.netty:netty-buffer:4.1.89.Final")

    api("com.velocitypowered:velocity-api:$velocityVersion")
    annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")
}

tasks {
    val processSources = register("processSources", Sync::class) {
        from(sourceSets.main.get().java.srcDirs)

        inputs.property("version", project.version)
        filesNotMatching("") { // go over every file
            expand("version" to project.version)
        }

        into(layout.buildDirectory.dir("src"))
    }

    withType<JavaCompile> {
        dependsOn(processSources)
        source = fileTree(processSources.get().destinationDir)
    }

    runVelocity {
        velocityVersion(velocityVersion)
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.lowercase()
        from(components["java"])
    }
}
