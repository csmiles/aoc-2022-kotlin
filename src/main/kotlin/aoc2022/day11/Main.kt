package aoc2022.day11

fun main() {
    val input = object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .readLines()
        .chunked(7)

    // Part 1
    val monkeys1 = input.map { Monkey(it) }
    val game1 = Game(monkeys1) { it / 3 }
    game1.playRounds(20)
    println(game1.monkeyBusiness)

    // Part 2
    val monkeys2 = input.map { Monkey(it) }
    val lcm = monkeys2.map { it.test.divisor }.reduce { x, y -> x * y }
    val game2 = Game(monkeys2) { it % lcm }
    game2.playRounds(10_000)
    println(game2.monkeyBusiness)
}

class Game(private val monkeys: List<Monkey>, private val reduction: (Long) -> (Long)) {

    val monkeyBusiness: Long
        get() = monkeys.sortedByDescending { it.inspectionCount }
            .let { (m1, m2) -> m1.inspectionCount * m2.inspectionCount }

    fun playRounds(numRounds: Int) {
        repeat(numRounds) {
            monkeys.forEach {
                while (it.hasItem()) {
                    val item = it.inspectItem()
                    item.worryLevel = reduction(item.worryLevel)
                    val catcherIdx = it.nextMonkeyIdx()
                    it.throwItemTo(monkeys[catcherIdx])
                }
            }
        }
    }
}

class Monkey(input: List<String>) {

    private val items = parseItems(input[1])
    private val operation = parseOperation(input[2])
    val test = MonkeyTest(input.slice(3 until input.size))
    var inspectionCount : Long = 0

    private fun parseItems(str: String): MutableList<Item> {
        val regex = Regex("""^\s*Starting items: (.*)$""")
        val result = regex.matchEntire(str) ?: throw IllegalArgumentException()
        return result.groupValues[1].split(',').map { Item(it.trim().toLong()) }.toMutableList()
    }

    private fun parseOperation(str: String): Operation {
        val regex = Regex("""^\s*Operation: (.*)$""")
        val result = regex.matchEntire(str) ?: throw IllegalArgumentException()
        return ofOperation(result.groupValues[1])
    }

    fun hasItem(): Boolean = items.isNotEmpty()

    fun inspectItem(): Item {
        inspectionCount++
        val item = items.first()
        item.worryLevel = operation(item.worryLevel)
        return item
    }

    fun nextMonkeyIdx(): Int = test.determineCatcher(items.first())

    fun throwItemTo(monkey: Monkey) {
        monkey.items.add(items.removeFirst())
    }
}

class Item(var worryLevel: Long)

fun ofOperation(str: String): Operation {
    val regex = """^\s*new = old (.) (.+)$""".toRegex()
    val match = regex.matchEntire(str) ?: throw IllegalArgumentException("Unable to parse: $str")
    val operation = match.groupValues[1]
    val operand = match.groupValues[2]

    return when (operation) {
        "+" -> if (operand == "old") doubleOperation() else plusOperation(operand.toInt())
        "*" -> if (operand == "old") squareOperation() else multiplyOperation(operand.toInt())
        else -> throw IllegalArgumentException("'$str': Only supports '*', '+' operations but $operand was found.")
    }
}

fun interface Operation {
    operator fun invoke(worryLevel: Long): Long
}
fun plusOperation(operand: Int) = Operation { it + operand }
fun multiplyOperation(operand: Int) = Operation { it * operand }
fun doubleOperation() = Operation { it * 2 }
fun squareOperation()  = Operation { it * it }

private val REGEX_CONDITION = """^\s*Test: divisible by (\d+)$""".toRegex()
private val REGEX_TRUE_ACTION = """^\s*If true: throw to monkey (\d+)$""".toRegex()
private val REGEX_FALSE_ACTION = """^\s*If false: throw to monkey (\d+)$""".toRegex()
class MonkeyTest(input: List<String>) {

    val divisor = firstGroup(input[0], REGEX_CONDITION)
    private val trueTargetMonkey = firstGroup(input[1], REGEX_TRUE_ACTION)
    private val falseTargetMonkey = firstGroup(input[2], REGEX_FALSE_ACTION)

    private fun firstGroup(input: String, regex: Regex): Int {
        val match = regex.matchEntire(input) ?: throw IllegalArgumentException("Unable to parse: $input")
        return match.groupValues[1].toInt()
    }

    fun determineCatcher(item: Item): Int = if (item.worryLevel % divisor == 0L) trueTargetMonkey else falseTargetMonkey

}
