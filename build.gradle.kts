plugins {
    id("java-library")
    id("maven-publish")
}

group = "dev.booky"
version = "1.3.0"

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    api("io.netty:netty-buffer:4.1.76.Final")

    api("com.velocitypowered:velocity-api:3.0.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
}

task("processSources", Sync::class) {
    from(sourceSets.main.get().java.srcDirs)
    inputs.property("version", version)

    filter { return@filter it.replace("\${version}", project.version as String) }
    into("$buildDir/src")
}

tasks.withType<JavaCompile> {
    dependsOn(tasks["processSources"])
    source = fileTree("$buildDir/src")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.toLowerCase()
        from(components["java"])
    }
}
