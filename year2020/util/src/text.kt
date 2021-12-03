import org.intellij.lang.annotations.Language

fun String.translate(fromChars: String, toChars: String) = buildString {
    val translation = fromChars.zip(toChars).toMap()
    for (c in this@translate) {
        append(translation[c] ?: c)
    }
}

fun String.extract(regex: Regex) =
    regex.matchEntire(this)?.destructured

fun String.extract(@Language("RegExp") regex: String) =
    extract(regex.toRegex())
