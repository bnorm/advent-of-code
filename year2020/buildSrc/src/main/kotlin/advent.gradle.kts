plugins {
    kotlin("jvm")
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
    implementation(project(":util"))
}
