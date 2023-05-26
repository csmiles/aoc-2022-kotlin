package aoc2022.day1

class Elves {

    private val elves = mutableListOf<Elf>()
    private var current = Elf()

    init { next() }

    fun next() {
        current = Elf()
        elves.add(current)
    }

    fun addCalories(calories: Int) {
        current.calories += calories
    }

    fun mostCalories(n: Int = 1): Int {
        return elves.toList()
            .sortedByDescending { it.calories }
            .take(n)
            .sumOf { it.calories }
    }
}