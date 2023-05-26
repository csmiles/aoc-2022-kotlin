package aoc2022.day3

fun main() {
    val rucksacks = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .lineSequence()
        .map { Rucksack(it) }
        .toList()

    val answer1 = rucksacks.flatMap { it.commonItems }
        .sumOf { it.priority }

    println(answer1)

    val answer2 = rucksacks.chunked(3)
        .map { Rucksack.commonItem(it) }
        .sumOf { it.priority }

    println(answer2)
}

class Rucksack(input: String) {
    val items: List<Item>

    private val compartment1: List<Item>
        get() = items.slice(0 until items.size / 2)
    private val compartment2: List<Item>
        get() = items.slice(items.size / 2 until items.size)

    val commonItems: Set<Item>
        get() = compartment1.intersect(compartment2.toSet())

    init {
        items = input.asSequence()
            .map { Item.of(it) }
            .toList()
    }

    companion object {
        fun commonItem(rucksacks: Collection<Rucksack>): Item {
            val count = mutableMapOf<Item, Int>()
            rucksacks.map { it.items.toSet() }
                .flatMap { it.asSequence() }
                .forEach {
                    count.compute(it) { _, count -> if (count != null) count + 1 else 1 }
                }

            return count.filterValues { it == rucksacks.size }
                .keys
                .first()
        }
    }
}

class Item private constructor(private val code: Char) {
    val priority: Int
        get() = if (code.isLowerCase()) code - 'a' + 1 else code - 'A' + 27

    override fun equals(other: Any?): Boolean {
        if (other !is Item) {
            return false
        }
        return code == other.code
    }

    override fun hashCode(): Int = code.hashCode()

    companion object {
        private val cache = mutableMapOf<Char, Item>()

        fun of(code: Char): Item = cache.computeIfAbsent(code) { Item(code) }
    }
}
