plugins {
    id("com.github.johnrengelman.shadow") version "5.0.0"
    application
}

application {
    mainClassName = "com.github.daggerok.samples.aggregate.App"
}

group = "${parent?.group}.aggregate"

dependencies {
    implementation(project(":cqrs-and-es"))
}
