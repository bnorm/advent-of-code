@file:Suppress("PackageDirectoryMismatch")

package aoc.day11

import aoc.run

const val SAMPLE1 = """
aaa: you hhh
you: bbb ccc
bbb: ddd eee
ccc: ddd eee fff
ddd: ggg
eee: out
fff: out
ggg: out
hhh: ccc fff iii
iii: out
"""

const val SAMPLE2 = """
svr: aaa bbb
aaa: fft
fft: ccc
bbb: tty
tty: ccc
ccc: ddd eee
ddd: hub
hub: fff
eee: dac
dac: fff
fff: ggg hhh
ggg: out
hhh: out
"""

suspend fun main() = run(
    year = 2025, day = 11,
    part1 = ::part1, sample1 = SAMPLE1, expected1 = "5",
    part2 = ::part2, sample2 = SAMPLE2, expected2 = "2",
)

private fun part1(input: String): String {
    val devices = parse(input)

    return countPaths(
        start = devices.getValue("you"),
        end = devices.getValue("out"),
    ).toString()
}

private fun part2(input: String): String {
    val devices = parse(input)

    val svr = devices.getValue("svr")
    val fft = devices.getValue("fft")
    val dac = devices.getValue("dac")
    val out = devices.getValue("out")

    val fftToDac = countPaths(fft, dac)
    return if (fftToDac > 0) {
        val svrToFft = countPaths(svr, fft)
        val dacToOut = countPaths(dac, out)
        (svrToFft * fftToDac * dacToOut).toString()
    } else {
        val svrToDac = countPaths(svr, dac)
        val dacToFft = countPaths(dac, fft)
        val fftToOut = countPaths(fft, out)
        (svrToDac * dacToFft * fftToOut).toString()
    }
}

private class Device(
    val label: String,
) {
    val outputs = mutableListOf<Device>()
    val inputs = mutableListOf<Device>()
}

private fun parse(input: String): Map<String, Device> {
    val devices = mutableMapOf<String, Device>()
    for (line in input.trim().lines()) {
        val (label, connections) = line.split(": ")
        val device = devices.getOrPut(label) { Device(label) }
        for (output in connections.split(" ")) {
            val other = devices.getOrPut(output) { Device(output) }
            device.outputs.add(other)
            other.inputs.add(device)
        }
    }
    return devices
}

private fun countPaths(start: Device, end: Device): Long {
    val memory = mutableMapOf<String, Long>()
    fun solve(device: Device): Long {
        return memory.getOrPut(device.label) {
            if (device == end) 1L
            else device.outputs.sumOf { solve(it) }
        }
    }

    return solve(start)
}
