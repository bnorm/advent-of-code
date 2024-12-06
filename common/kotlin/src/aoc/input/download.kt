package aoc.input

import io.ktor.client.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.cache.storage.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.readText

private val client = HttpClient {
    expectSuccess = true

    install(HttpCookies) {
        val sessionCookie = Path(".aoc/session").readText().trim()
        storage = ConstantCookiesStorage(Cookie(name = "session", value = sessionCookie, domain = "adventofcode.com"))
    }

    install(HttpCache) {
        val cacheFile = Path(".aoc/cache").createDirectories()
        publicStorage(FileStorage(cacheFile.toFile()))
    }
}

suspend fun downloadInput(year: Int, day: Int): String {
    val lines = client.get(Url("https://adventofcode.com/$year/day/$day/input")) {
        cookies()
    }
        .bodyAsText()
    require(lines.isNotEmpty()) { "Input data required!" }
    return lines
}
