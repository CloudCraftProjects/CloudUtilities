plugins {
    java
    idea
}

repositories {
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    implementation("io.netty:netty-buffer:4.1.58.Final")
    implementation("com.velocitypowered:velocity-api:3.0.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.0")
}

task("processSources", Sync::class) {
    from(sourceSets.main.get().java.srcDirs)
    inputs.property("version", version)

    filter { return@filter it.replace("@version@", project.version as String) }
    into("$buildDir/src")
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(tasks["processSources"])
    source = fileTree("$buildDir/src")
}
