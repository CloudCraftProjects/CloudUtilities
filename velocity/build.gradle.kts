plugins {
    alias(libs.plugins.run.velocity)
    alias(libs.plugins.blossom)
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    compileOnly(libs.cloudcore.velocity)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

tasks {
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}
