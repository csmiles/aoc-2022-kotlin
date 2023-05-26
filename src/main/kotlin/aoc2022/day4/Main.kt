package aoc2022.day4

fun main() {
    val assignments = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .lineSequence()
        .map { AssignmentPair(it) }
        .toList()

    println(assignments.count { it.a in it.b || it.b in it.a })
    println(assignments.count { it.a.overlaps(it.b) })
}

class AssignmentPair(input: String) {
    val a: IntRange
    val b: IntRange

    init {
        val assignments = input.split(",")
        a = assignment(assignments[0])
        b = assignment(assignments[1])
    }

    private fun assignment(input: String): IntRange {
        return input.split("-")
            .let { (a, b) -> a.toInt()..b.toInt() }
    }
}

operator fun IntRange.contains(other: IntRange): Boolean = first <= other.first && last >= other.last

fun IntRange.overlaps(other: IntRange): Boolean = first <= other.last && last >= other.first