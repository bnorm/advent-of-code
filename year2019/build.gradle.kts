plugins {
    kotlin("jvm") version "1.4.20"
}

repositories {
    mavenCentral()
}

java {
    sourceSets["main"].java.setSrcDirs(listOf("src"))
    sourceSets["main"].resources.setSrcDirs(listOf("res"))
    sourceSets["test"].java.setSrcDirs(listOf("test"))
}
kotlin {
    sourceSets["main"].kotlin.setSrcDirs(listOf("src"))
    sourceSets["test"].kotlin.setSrcDirs(listOf("test"))
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}
