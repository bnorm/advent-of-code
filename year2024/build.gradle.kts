plugins {
    kotlin("multiplatform") version "2.1.0"
}

kotlin {
    jvm()

    sourceSets {
        commonMain {
            kotlin.srcDir("src")
            resources.srcDir("src")

            dependencies {
                api(":utils:+")
            }
        }
    }
}
