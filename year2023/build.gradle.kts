plugins {
    kotlin("jvm") version "1.9.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
        resources.srcDir("src")
    }
}

dependencies {
    api(":utils:+")
}
