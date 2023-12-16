plugins {
    kotlin("jvm") version "1.9.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
        resources.srcDir("res")
        resources.srcDir("src")
    }
    test {
        kotlin.srcDir("test")
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
