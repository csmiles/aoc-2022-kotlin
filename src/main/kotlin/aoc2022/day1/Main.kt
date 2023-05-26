package aoc2022.day1

fun main() {
    val elves = Elves()

    object {}.javaClass.getResourceAsStream("input.txt")
        ?.bufferedReader()
        ?.forEachLine {
            if (it.isBlank()) elves.next()
            else elves.addCalories(it.toInt())
        }

    println(elves.mostCalories())
    println(elves.mostCalories(3))
}