import Globals.Versions.assertjVersion
import Globals.Versions.junit4Version
import Globals.Versions.junitJupiterVersion
import Globals.Versions.logbackVersion
import Globals.Versions.lombokVersion
import Globals.Versions.slf4jVersion
import Globals.Versions.vavrVersion
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    id("com.moowork.node") version "1.2.0"
    id("io.franzbecker.gradle-lombok") version "2.1"
}

lombok {
    version = lombokVersion
}

allprojects {
    version = "1.0.0-SNAPSHOT"
    group = "com.github.daggerok"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    this.group = "${this.parent?.group}.${this.name.replace("-", "")}"

    the<SourceSetContainer>()["main"].java.srcDir("src/main/kotlin")
    the<SourceSetContainer>()["test"].java.srcDir("src/test/kotlin")

    dependencies {
        implementation("io.vavr:vavr:$vavrVersion")
        annotationProcessor("org.projectlombok:lombok:$lombokVersion")
        testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
        compileOnly("org.projectlombok:lombok:$lombokVersion")
        testCompileOnly("org.projectlombok:lombok:$lombokVersion")
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
    }

    dependencies {
        testImplementation("org.assertj:assertj-core:$assertjVersion")
        testImplementation("junit:junit:$junit4Version")
        testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
        testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
        testRuntime("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events(PASSED, SKIPPED, FAILED)
        }
    }
}

defaultTasks("build")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

// vuepress npm config
node {
    download = true
    version = "10.9.0"
    npmVersion = "6.8.0"
}

// jvm global config
val javaVersion = JavaVersion.VERSION_1_8
configure<JavaPluginConvention> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "$javaVersion"
}

// wrapper
tasks.withType<Wrapper> {
    gradleVersion = "5.2.1"
    distributionType = Wrapper.DistributionType.BIN
}

// incrementVersion
tasks.register("incrementVersion") {
    doLast {
        incrementVersion(path = "./build.gradle.kts", prefix = "    version = \"", suffix = "\"")
        incrementVersion(path = "./package.json", prefix = "  \"version\": \"", suffix = "\",")
        incrementVersion(path = "./package-lock.json", prefix = "  \"version\": \"", suffix = "\",")
    }
}
