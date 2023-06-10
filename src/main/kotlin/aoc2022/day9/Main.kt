package aoc2022.day9

import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val instructions = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .lineSequence()
        .map { Instruction(it) }
        .toList()

    val field1 = RopeField(2)
    field1.execute(instructions)
    println(field1.visitCount)

    val field2 = RopeField(10)
    field2.execute(instructions)
    println(field2.visitCount)
}

class Instruction(input: String) {
    private val direction = ofDirection(input.first())
    private val amount = input.substring(2).toInt()

    fun execute(field: RopeField) {
        field.moveHead(direction, amount)
    }
}

enum class Direction { UP, RIGHT, DOWN, LEFT }

fun ofDirection(c: Char): Direction {
    return when (c) {
        'U' -> Direction.UP
        'D' -> Direction.DOWN
        'L' -> Direction.LEFT
        'R' -> Direction.RIGHT
        else -> throw IllegalArgumentException()
    }
}

class RopeField(amount: Int) {
    private val visited = mutableSetOf(0 to 0)
    private val knots = Array(amount) { 0 to 0 }

    val visitCount: Int
        get() = visited.size

    fun execute(instructions: List<Instruction>) = instructions.forEach { it.execute(this) }

    fun moveHead(direction: Direction, amount: Int) {
        repeat(amount) {
            knots[0] = knots[0].let { (x, y) ->
                when (direction) {
                    Direction.LEFT -> x - 1 to y
                    Direction.RIGHT -> x + 1 to y
                    Direction.UP -> x to y + 1
                    Direction.DOWN -> x to y - 1
                }
            }

            for (i in 0..knots.size-2) {
                makeAdjacent(i, i+1)
            }

            visited.add(knots.last())
        }
    }

    private fun makeAdjacent(headIdx: Int, tailIdx: Int) {
        val head = knots[headIdx]
        val tail = knots[tailIdx]
        val dx = head.first - tail.first
        val dy = head.second - tail.second

        if (abs(dx) <= 1 && abs(dy) <= 1) {
            return
        }

        knots[tailIdx] = if (head.first == tail.first) {
            tail.first to tail.second + dy.sign * 1
        } else if (head.second == tail.second) {
            tail.first + dx.sign * 1 to tail.second
        } else {
            tail.first + dx.sign * 1 to tail.second + dy.sign * 1
        }
    }
}