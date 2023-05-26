package aoc2022.day2

fun main() {
    val game = Game()
    object {}.javaClass.getResourceAsStream("input.txt")
        ?.bufferedReader()
        ?.forEachLine { game.addTurn(it) }

    println(game.score)
}

class Game {
    var score: Int = 0
        private set

    fun addTurn(input: String) {
        val turn = input.split("""\s+""".toRegex())
        val opponentsHand = Hand.fromCode(turn[0][0])
        score += when(turn[1]) {
            "X" -> opponentsHand.beats.score
            "Y" -> 3 + opponentsHand.score
            "Z" -> 6 + opponentsHand.loses.score
            else -> throw IllegalArgumentException()
        }
    }
}

enum class Hand {
    ROCK {
        override val beats: Hand
            get() = SCISSORS
        override val loses: Hand
            get() = PAPER
    },

    PAPER {
        override val beats: Hand
            get() = ROCK
        override val loses: Hand
            get() = SCISSORS
    },

    SCISSORS {
        override val beats: Hand
            get() = PAPER
        override val loses: Hand
            get() = ROCK
    };

    val score = ordinal + 1
    abstract val beats: Hand
    abstract val loses: Hand

    companion object {
        fun fromCode(code: Char): Hand {
            return Hand.values()[code - 'A']
        }
    }
}