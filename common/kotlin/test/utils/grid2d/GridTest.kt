package utils.grid2d

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GridTest {
    @Test
    fun testSubSpan() {
        val grid = Grid(listOf(listOf(1, 2, 3, 4, 5)))
        val span = grid.row(0).subSpan(1, 4)
        val expected = listOf(2, 3, 4)

        assertEquals(expected, (0..<span.size).map { span[it] }.toList())
        assertEquals(expected, span.toList())
    }

    @Test
    fun testSubSpanReverse() {
        val grid = Grid(listOf(listOf(1, 2, 3, 4, 5)))
        val span = grid.row(0).subSpan(1, 4).asReverse()
        val expected = listOf(4, 3, 2)

        assertEquals(expected, (0..<span.size).map { span[it] }.toList())
        assertEquals(expected, span.toList())
    }

    @Test
    fun testReverseSubSpan() {
        val grid = Grid(listOf(listOf(1, 2, 3, 4, 5)))
        val span = grid.row(0).asReverse().subSpan(1, 4)
        val expected = listOf(4, 3, 2)

        assertEquals(expected, (0..<span.size).map { span[it] }.toList())
        assertEquals(expected, span.toList())
    }
}
