package aoc2022.day7

fun main() {
    val consoleLines = object {}.javaClass.getResourceAsStream("input.txt")!!
        .reader()
        .readLines()

    val drive = Drive(consoleLines)

    println(drive.directories)

    val answer1 = drive.directories
        .filter { it.size <= 100000 }
        .sumOf { it.size }
    println(answer1)

    val requiredSpace = 30000000 - drive.availableSize
    val answer2 = drive.directories
        .filter { it.size >= requiredSpace }
        .minOf { it.size }
    println(answer2)
}

class Drive(consoleLines: List<String>) {
    private val totalSize = 70000000
    val directories: Directory
    val availableSize: Int

    init {
        val iterator = consoleLines.listIterator()
        // Calling next() because we assume "cd /"
        iterator.next()
        directories = Directory(name = "/")

        var pwd = directories
        while (iterator.hasNext()) {
            val consoleLine = iterator.next()
            val cmd = Command.parseCmd(consoleLine)
            pwd = cmd(pwd, iterator)
        }

        availableSize = totalSize - directories.size
    }
}

sealed class Command {
    abstract operator fun invoke(pwd: Directory, consoleLines: ListIterator<String>): Directory

    companion object {
        fun parseCmd(consoleLine: String): Command {
            val tokens = consoleLine.split("""\s+""".toRegex())

            if (tokens[0] != "$") {
                throw IllegalArgumentException()
            }

            return when (tokens[1]) {
                "cd" -> ChangeDirectory(tokens[2])
                "ls" -> ListDirectories
                else -> throw IllegalArgumentException()
            }
        }
    }
}

class ChangeDirectory(private val directoryName: String) : Command() {
    override fun invoke(pwd: Directory, consoleLines: ListIterator<String>): Directory {
        if (directoryName == "..") {
            return pwd.parent!!
        }

        return pwd.createDirectory(directoryName)
    }
}

object ListDirectories : Command() {
    override fun invoke(pwd: Directory, consoleLines: ListIterator<String>): Directory {
        for (consoleLine in consoleLines) {
            if (consoleLine.startsWith('$')) {
                consoleLines.previous()
                break
            }

            val tokens = consoleLine.split("""\s+""".toRegex())
            if (tokens[0] == "dir") {
                continue
            }

            pwd.createFile(size = tokens[0].toInt(), name = tokens[1])
        }

        return pwd
    }
}

class Directory(val parent: Directory? = null, val name: String) : Iterable<Directory> {

    private val directories = mutableListOf<Directory>()
    private val files = mutableListOf<File>()

    val size: Int
        get() = files.sumOf { it.size } + directories.sumOf { it.size }

    fun createDirectory(name: String): Directory {
        val existingDirectory = directories.firstOrNull { it.name == name }
        if (existingDirectory != null) {
            return existingDirectory
        }

        val newDirectory = Directory(this, name)
        directories.add(newDirectory)
        return newDirectory
    }

    fun createFile(name: String, size: Int) {
        files.add(File(name, size))
    }

    override fun iterator(): Iterator<Directory> {
        return buildList {
            add(this@Directory)

            // Note that since Directory implements Iterable<Directory>, directories is a subtype of
            // Collection<Iterable<Directory>> so we need to flatten it to make sure that we call the
            // Directory::iterator() method recursively
            directories.forEach { addAll(it) }
        }.iterator()
    }

    override fun toString(): String {
        return toString("")
    }

    private fun toString(indent: String): String {
        val me = listOf("$indent- $name (dir, size=$size)")
        val files = files.map { it.toString("$indent  ") }
        val directories = directories.map { it.toString("$indent  ") }

        return (me + files + directories).joinToString(separator = "\n")
    }
}

data class File(val name: String, val size: Int) {
    override fun toString(): String {
        return toString("")
    }

    fun toString(indent: String): String {
        return "$indent- $name (file, size=$size)"
    }
}