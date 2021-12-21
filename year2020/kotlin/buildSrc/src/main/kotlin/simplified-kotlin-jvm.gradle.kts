import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.implementation
import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.java
import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.kotlin
import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.sourceSets
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.repositories

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
