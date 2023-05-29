package aoc2022.day5

import java.io.BufferedReader

fun main() {
    val input = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()

    val operation = CargoOperation(input)
    operation.executeInstructions()

    val answer = operation.stacks
        .map { it.last() }
        .let { String(it.toCharArray()) }

    println(answer)
}

class CargoOperation(input: BufferedReader) {
    val stacks: Stacks
    private val instructions: Instructions

    init {
        val lines = input.readLines()

        val stacksInput = lines.takeWhile { it.isNotBlank() }
        stacks = Stacks(stacksInput)

        val instructionsInput = lines.slice(stacksInput.size+1 until lines.size)
        instructions = Instructions(instructionsInput)
    }

    fun executeInstructions() {
        instructions.forEach { it(stacks) }
    }
}

class Stacks(input: List<String>): Iterable<ArrayDeque<Char>> {

    private val value = mutableListOf<ArrayDeque<Char>>()

    init {
        input.reversed()
            .slice(1 until input.size)
            .forEach { row ->
                row.chunked(4)
                    .map { it[1] }
                    .forEachIndexed { i, c ->
                        if (!c.isWhitespace()) value.addIfAbsent(i, ::ArrayDeque).addLast(c)
                    }
            }
    }

    override fun iterator() = value.iterator()

    operator fun get(i: Int) = value[i]

}

class Instructions(input: List<String>): Iterable<Instruction> {

    private val instructions = input.map { Instruction(it) }

    override fun iterator() = instructions.iterator()

}

class Instruction(input: String) {

    private val amount: Int
    private val from: Int
    private val to: Int

    init {
        val regex = Regex("""move (\d+) from (\d+) to (\d+)""")
        val result = regex.matchEntire(input)!!
        amount = result.groupValues[1].toInt()
        from = result.groupValues[2].toInt()
        to = result.groupValues[3].toInt()
    }
    operator fun invoke(stacks: Stacks) {
        val fromStack = stacks[from-1]
        val toStack = stacks[to-1]

        val toMove = ArrayDeque<Char>()
        repeat(amount) {
            toMove.addFirst(fromStack.removeLast())
        }

        toStack.addAll(toMove)
    }
}

fun <T> MutableList<T>.addIfAbsent(i: Int, f: () -> T): T {
    if (size <= i || getOrNull(i) == null) {
        add(i, f())
    }

    return get(i)
}
