import java.nio.file.NoSuchFileException
import kotlin.io.path.readLines
import kotlin.io.path.toPath

fun readInput(fileName: String): List<String> {
    val resource = ClassLoader.getSystemResource(fileName) ?: throw NoSuchFileException(fileName)
    return resource.toURI().toPath().readLines()
}
