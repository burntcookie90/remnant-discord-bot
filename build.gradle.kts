plugins {
    kotlin("jvm") version "2.1.0"
    id("app.cash.sqldelight") version "2.0.2"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "com.vishnurajeevan"
version = "1.0-SNAPSHOT"

application {
    mainClass = "com.vishnurajeevan.MainKt"
}
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("dev.kord:kord-core:0.12.0")
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.vishnurajeevan.remnant")
        }
    }
}

