package aoc2022.day6

fun main() {

    val input = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .readLine()

    val marker1 = Marker(4)
    for (c in input) {
        marker1.add(c)
        if (marker1.done()) break
    }
    println(marker1.index)

    val marker2 = Marker(14)
    for (c in input) {
        marker2.add(c)
        if (marker2.done()) break
    }
    println(marker2.index)
}

class Marker(private val size: Int) {

    private val marker = mutableListOf<Char>()

    var index: Int = 0
        private set

    fun add(c: Char) {
        index++
        marker.add(c)
        if (marker.size > size) marker.removeFirst()
    }

    fun done() = marker.toSet().size == size
}
