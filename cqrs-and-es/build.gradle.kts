import Globals.Versions.eventBusVersion

plugins {
    java
}

dependencies {
    implementation("org.greenrobot:eventbus:$eventBusVersion")
    compile(project(":context"))
}
