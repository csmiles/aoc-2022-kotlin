package aoc2022.day10

fun main() {
    val instructions = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .lineSequence()
        .map { ofInstruction(it) }
        .toList()

    val device = Device(listOf(20, 60, 100, 140, 180, 220))
    device.execute(instructions)
    println(device.signalStrengthSum)

    device.printScreen()
}

fun ofInstruction(input: String): Instruction {
    val tokens = input.split("""\s+""".toRegex())
    return when (tokens[0]) {
        "addx" -> AddX(tokens[1].toInt())
        "noop" -> Noop
        else -> throw IllegalArgumentException()
    }
}

interface Instruction {
    fun execute(device: Device)
}

class AddX(val operand: Int) : Instruction {
    override fun execute(device: Device) {
        repeat(2) { device.cycle() }
        device.x += operand
    }
}

object Noop : Instruction {
    override fun execute(device: Device) {
        device.cycle()
    }
}

class Device(val interestingCycles: List<Int>) {
    private var cycles = 0
    var x = 1
    var signalStrengthSum = 0

    val crt = Array(6) { Array(40) { '.' } }

    fun execute(instructions: List<Instruction>) {
        instructions.forEach { it.execute(this) }
    }

    fun cycle() {
        val row = cycles / 40
        val column = cycles % 40
        if (column in x-1..x+1) {
            crt[row][column] = '#'
        }

        if (++cycles in interestingCycles) { signalStrengthSum += cycles * x }
    }

    fun printScreen() {
        for (row in crt) {
            for (pixel in row) {
                print(pixel)
            }
            println()
        }
    }
}
