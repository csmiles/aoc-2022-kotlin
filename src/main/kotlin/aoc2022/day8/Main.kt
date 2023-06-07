package aoc2022.day8

fun main() {
    val forest = Forest()
    object {}.javaClass.getResourceAsStream("input.txt")!!
        .bufferedReader()
        .lineSequence()
        .forEach {
            forest.nextRow()
            it.asSequence().forEach { forest.addTree(it.digitToInt()) }
        }
    forest.done()

    val answer1 = forest.visibleCount
    println(answer1)

    val answer2 = forest.mostScenic.scenicScore
    println(answer2)
}

class Forest {
    private val trees = Matrix<Tree>()
    val visibleCount
        get() = trees.count { it.visible }

    val mostScenic
        get() = trees.maxBy { it.scenicScore ?: 0 }

    fun nextRow() = trees.nextRow()

    fun addTree(height: Int) = trees.addCell(Tree(height = height))

    fun done() {
        trees.run {
            sequenceOf(rowIterator(), rowIterator(reverse = true), columnIterator(), columnIterator(reverse = true))
                .forEach { revealVisibility(it) }
        }
    }

    private fun revealVisibility(iterator: Iterator<SettableIterator<Tree>>) {
        for (treeLine in iterator) {
            var max = Tree(height = -1)
            val visibleTrees = mutableListOf<Int>()
            while (treeLine.hasNext()) {
                val current = treeLine.next()
                var newTree = current.improveScenery(visibleTrees)

                if (current > max) {
                    newTree = newTree.withVisibility()
                    max = newTree
                }

                treeLine.set(newTree)
                visibleTrees.add(0, current.height)
            }
        }
    }

    override fun toString() = trees.toString()
}

data class Tree(val height: Int, val scenicScore: Int? = null, val visible: Boolean = false) {

    private val visibleCode
        get() = if (visible) 'v' else 'h'

    fun withVisibility(): Tree = copy(visible = true)

    fun improveScenery(scenery: List<Int>): Tree {
        val additionalScenery = scenery.takeWhile { height > it }
            .count()
            .let { if (scenery.size > it) it + 1 else it }

        return copy(scenicScore = (scenicScore ?: 1) * additionalScenery)
    }

    operator fun compareTo(other: Tree) = this.height - other.height

    override fun toString() = "<$height,$scenicScore,${visibleCode}>"
}

class Matrix<E> private constructor(
    private val rowFirst: MutableList<MutableList<E>>,
    private val columnFirst: MutableList<MutableList<E>>,
    private var rowIdx: Int,
    private var columnIdx: Int
): Iterable<E> {
    constructor() : this(mutableListOf(), mutableListOf(), -1, -1)

    operator fun set(i: Int, j: Int, value: E) {
        rowFirst[j][i] = value
        columnFirst[i][j] = value
    }

    operator fun get(i: Int, j: Int): E = rowFirst[j][i]

    fun nextRow() {
        columnIdx = -1
        rowIdx++
    }

    fun addCell(value: E) {
        columnIdx++

        val row = rowFirst.getOrAdd(rowIdx, ::mutableListOf)
        row.add(value)

        val column = columnFirst.getOrAdd(columnIdx, ::mutableListOf)
        column.add(value)
    }

    override fun iterator(): Iterator<E> = rowFirst.flatten().iterator()

    override fun toString(): String {
        val sb = StringBuilder()

        for (row in rowFirst) {
            for (cell in row) {
                sb.append(cell)
            }
            sb.appendLine()
        }

        return sb.toString()
    }

    fun rowIterator(reverse: Boolean = false): Iterator<SettableIterator<E>> = object : AbstractIterator<SettableIterator<E>>() {
        private var i = 0
        override fun computeNext() {
            if (i == rowFirst.size) {
                done()
            } else {
                setNext(newIterator(rowFirst[i], i++))
            }
        }

        fun newIterator(row: MutableList<E>, idx: Int): SettableIterator<E> {
            return if (reverse) {
                ReverseRowIterator(row, idx)
            } else {
                RowIterator(row, idx)
            }
        }
    }

    fun columnIterator(reverse: Boolean = false): Iterator<SettableIterator<E>> = object : AbstractIterator<SettableIterator<E>>() {
        private var i = 0
        override fun computeNext() {
            if (i == columnFirst.size) {
                done()
            } else {
                setNext(newIterator(columnFirst[i], i++))
            }
        }

        fun newIterator(row: MutableList<E>, idx: Int): SettableIterator<E> {
            return if (reverse) {
                ReverseColIterator(row, idx)
            } else {
                ColIterator(row, idx)
            }
        }
    }

    private abstract inner class MatrixIterator(
        protected val list: List<E>,
        protected var x: Int,
        protected var y: Int)
        : AbstractIterator<E>(), SettableIterator<E> {
        override fun set(element: E) {
            this@Matrix[x, y] = element
        }
    }

    private inner class RowIterator(list: List<E>, y: Int) : MatrixIterator(list, -1, y) {
        override fun computeNext() = if (++x < list.size) setNext(list[x]) else done()
    }

    private inner class ReverseRowIterator(list: List<E>, y: Int) : MatrixIterator(list, list.size, y) {
        override fun computeNext() = if (--x >= 0) setNext(list[x]) else done()
    }

    private inner class ColIterator(list: List<E>, x: Int): MatrixIterator(list, x, -1) {
        override fun computeNext() = if (++y < list.size) setNext(list[y]) else done()
    }

    private inner class ReverseColIterator(list: List<E>, x: Int) : MatrixIterator(list, x, list.size) {
        override fun computeNext() = if (--y >= 0) setNext(list[y]) else done()
    }
}

fun <E> MutableList<E>.getOrAdd(i: Int, f: () -> E): E = if (i in 0 until size) get(i) else f().also(this::add)

interface SettableIterator<E> : Iterator<E> {
    fun set(element: E)
}
