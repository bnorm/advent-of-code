package aoc.input

import io.ktor.client.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.cache.storage.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.readText

suspend fun downloadInput(year: Int, day: Int): String {
    require(day > 0)
    require(year > 0)

    val client = HttpClient {
        expectSuccess = true

        install(HttpCookies) {
            val session = Path(".aoc/session").readText().trim()
            val cookie = Cookie(name = "session", value = session, domain = "adventofcode.com")
            storage = ConstantCookiesStorage(cookie)
        }

        install(HttpCache) {
            val cacheFile = Path(".aoc/cache").createDirectories()
            publicStorage(FileStorage(cacheFile.toFile()))
        }

        install(DefaultRequest) {
            val userAgent = Path(".aoc/user-agent").readText().trim()
            header(HttpHeaders.UserAgent, userAgent)
        }
    }

    val lines = client.use {
        client.get(Url("https://adventofcode.com/$year/day/$day/input"))
            .bodyAsText()
    }
    require(lines.isNotEmpty()) { "Input data required!" }
    return lines
}
