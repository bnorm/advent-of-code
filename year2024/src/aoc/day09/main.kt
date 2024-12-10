package aoc.day09

import aoc.input.downloadInput
import java.util.*
import kotlin.collections.ArrayDeque

const val SAMPLE1 = """
2333133121414131402
"""

const val SAMPLE2 = SAMPLE1

suspend fun main() {
    val input = downloadInput(2024, 9)

    val part1 = part1(SAMPLE1)
    require(part1 == "1928") { part1 }
    println(part1(input))

    val part2 = part2(SAMPLE2)
    require(part2 == "2858") { part2 }
    println(part2(input))
}

data class Fragment(
    val address: Int,
    val size: Int,
    val id: Int?,
) : Comparable<Fragment> {
    fun checksum(): Long {
        if (id == null) return 0L

        var checksum = 0L
        for (i in 0..<size) {
            checksum += (address + i) * id
        }
        return checksum
    }

    fun shrink(size: Int): Fragment {
        return Fragment(address, this.size - size, id)
    }

    fun move(address: Int): Fragment {
        return Fragment(address, size, id)
    }

    override fun compareTo(other: Fragment): Int {
        return compareValues(address, other.address)
    }
}

private fun part1(input: String): String {
    val disk = buildDisk(input)

    var address = 0
    var checksum = 0L
    while (disk.isNotEmpty()) {
        val first = disk.removeFirst().shrink(1)
        if (first.size == -1) continue

        if (first.size > 0) disk.addFirst(first)
        if (first.id == null) {
            while (true) {
                val last = disk.removeLast().shrink(1)
                if (last.size == -1 || last.id == null) continue

                if (last.size > 0) disk.addLast(last)
                checksum += address * last.id
                break
            }
        } else {
            checksum += address * first.id
        }
        address++
    }

    return checksum.toString()
}

private fun part2(input: String): String {
    val disk = buildDisk(input)

    // Build a map of free space by size.
    val free = TreeMap<Int, PriorityQueue<Fragment>>()
    for (fragment in disk) {
        if (fragment.id == null && fragment.size > 0) {
            free.getOrPut(fragment.size) { PriorityQueue() }.add(fragment)
        }
    }

    var checksum = 0L
    while (disk.isNotEmpty()) {
        val last = disk.removeLast()
        if (last.size == 0 || last.id == null) continue

        val entry = free.tailMap(last.size).minByOrNull { it.value.peek().address }
        if (entry == null || entry.value.peek().address > last.address) {
            checksum += last.checksum()
        } else {
            val space = entry.value.poll()
            if (entry.value.isEmpty()) free.remove(entry.key)

            checksum += last.move(space.address).checksum()

            if (space.size > last.size) {
                val shrink = space.shrink(last.size).move(space.address + last.size)
                free.getOrPut(shrink.size) { PriorityQueue() }.add(shrink)
            }
        }
    }

    return checksum.toString()
}

private fun buildDisk(input: String): ArrayDeque<Fragment> {
    var address = 0
    var free = false
    var id = 0

    val disk = ArrayDeque<Fragment>()
    for (c in input.trim()) {
        val size = c.digitToInt()

        val fragment = Fragment(
            address = address,
            size = size,
            id = if (free) null else id++,
        )
        disk.addLast(fragment)

        address += size
        free = !free
    }

    return disk
}
