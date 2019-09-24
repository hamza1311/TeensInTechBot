import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.50"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("com.avast.gradle.docker-compose") version "0.9.4"

}

group = "teensintech"
version = "1.0"

application {
    mainClassName = "MainKt"
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.dv8tion:JDA:4.0.0_46")
    implementation("org.litote.kmongo:kmongo-coroutine:3.11.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets["main"].java.srcDir("src/main/java")
sourceSets["main"].withConvention(KotlinSourceSet::class) {
    kotlin.srcDir("src/main/kotlin")
}

// Fat jar

tasks.withType<Jar> {
    destinationDirectory.set(File("./build/dist/jar"))

    manifest {
        attributes (
            mapOf (
                "Main-Class" to application.mainClassName
            )
        )
    }

}

dockerCompose {
    useComposeFiles = mutableListOf("./docker-compose.yml")

    projectName = "TeensInTech"

    stopContainers  = true
}

tasks.register("deploy", GradleBuild::class) {
    tasks = mutableListOf("shadowJar", "composeUp")
}