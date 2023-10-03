plugins {
    id("java-library")
    id("maven-publish")
}

group = "dev.booky"
version = "1.3.1"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api("io.netty:netty-buffer:4.1.89.Final")

    val velocityVersion = "3.2.0-SNAPSHOT"
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
