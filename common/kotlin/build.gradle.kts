plugins {
    kotlin("jvm") version "2.1.0"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    implementation("io.ktor:ktor-client-core:3.0.2")
    runtimeOnly("io.ktor:ktor-client-okhttp:3.0.2")
    runtimeOnly("org.slf4j:slf4j-nop:2.0.16")
}
