plugins {
    id("xyz.jpenilla.run-velocity") version "2.3.0"
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
