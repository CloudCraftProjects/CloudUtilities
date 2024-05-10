plugins {
    alias(libs.plugins.run.velocity)
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    compileOnly(libs.cloudcore.velocity)
}

tasks {
    val processSources = register("processSources", Sync::class) {
        from(sourceSets.main.map { it.java.srcDirs }.get())

        inputs.property("version", project.version)
        filesNotMatching("") { // go over every file
            expand("version" to project.version)
        }

        into(layout.buildDirectory.dir("src"))
    }

    withType<JavaCompile> {
        dependsOn(processSources)
        source = fileTree(processSources.map { it.destinationDir }.get())
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}
