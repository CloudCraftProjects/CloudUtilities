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
    implementation(libs.bstats.velocity)

    plugin(variantOf(libs.cloudcore.velocity) { classifier("all") })
}

configure<JavaPluginExtension> {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    disableAutoTargetJvm()
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
    withType<JavaCompile> {
        options.release = 21
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
        pluginJars.from(plugin.resolve())
    }

    shadowJar {
        relocate("org.bstats", "${project.group}.cloudutilities.bstats")
    }
}
