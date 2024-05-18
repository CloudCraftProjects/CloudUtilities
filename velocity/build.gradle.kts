plugins {
    alias(libs.plugins.run.velocity)
    alias(libs.plugins.blossom)
}

val plugin: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    compileOnly(libs.cloudcore.velocity)

    plugin(variantOf(libs.cloudcore.velocity) { classifier("all") })
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
        pluginJars.from(plugin.resolve())
    }
}
