package minesweeper

import kotlin.random.Random
import kotlin.random.nextInt

class Field {
    private val field = List(9) { MutableList(9) { '.' } }
    private val exploringField = List(9) { MutableList(9) { '.' } }
    private val fieldOfMarks = List(9) { MutableList(9) { '.' } }
    private var notExploded = true
    var continueGame = true
    var text = "Congratulations! You found all the mines!"

    fun createMines(number: Int) {
        repeat(number) {
            var randomX = Random.nextInt(0..8)
            var randomY = Random.nextInt(0..8)

            while (field[randomY][randomX] == 'X') {
                randomX = Random.nextInt(0..8)
                randomY = Random.nextInt(0..8)
            }

            field[randomY][randomX] = 'X'
        }
    }

    fun checkFirstCell(input: String) {
        val xy = input.split(" ")

        while (field[xy[1].toInt() - 1][xy[0].toInt() - 1] == 'X') {
            field[xy[1].toInt() - 1][xy[0].toInt() - 1] = '.'
            this.createMines(1)
        }
    }

    fun createNumbers() {
        for (y in 0..8)
            for (x in 0..8) {
                var count = 0
                for (i in x - 1..x + 1)
                    for (j in y - 1..y + 1)
                        if (i in 0..8 && j in 0..8 && field[j][i] == 'X') count++

                if (count != 0) {
                    if (field[y][x] != 'X') field[y][x] = count.toString().first()
                }
            }
    }

    fun setCellValue(input: String) {
        val xyCommand = input.split(" ")
        val x = xyCommand[0].toInt() - 1
        val y = xyCommand[1].toInt() - 1

        if (xyCommand[2] == "mine") {
            if (fieldOfMarks[y][x] == '*') fieldOfMarks[y][x] = '.' else fieldOfMarks[y][x] = '*'
        } else {
            when(field[y][x]) {
                '.' -> this.openSafeCells(x, y)
                'X' -> {
                    text = "You stepped on a mine and failed!"
                    notExploded = false
                    showMines()
                }
                else -> exploringField[y][x] = field[y][x]
            }
        }

        this.printField()
    }

    private fun openSafeCells(x0: Int, y0: Int) {
        exploringField[y0][x0] = '/'

        repeat(20) {
            for (y in 0..8)
                for (x in 0..8)
                    if (exploringField[y][x] == '/') {
                        for (i in x - 1..x + 1)
                            for (j in y - 1..y + 1)
                                if (i in 0..8 && j in 0..8) {
                                    if (field[j][i].isDigit()) {
                                        exploringField[j][i] = field[j][i]
                                        fieldOfMarks[j][i] = '.'
                                    }
                                    if (field[j][i] == '.') {
                                        exploringField[j][i] = '/'
                                        fieldOfMarks[j][i] = '.'
                                    }
                                }
                    }
        }
    }

    private fun checkMines(mines: Int): Boolean {
        var exploredMines = 0
        var marks = 0

        for (y in 0..8)
            for (x in 0..8) {
                if (field[y][x] == 'X' && fieldOfMarks[y][x] == '*') exploredMines++
                if (fieldOfMarks[y][x] == '*') marks++
            }

        return exploredMines == mines && exploredMines == marks
    }

    private fun checkSaveCells(): Boolean {
        var safeCells = 0
        var openedSafeCells = 0
        var numbersCells = 0
        var openedNumbersCells = 0

        for (y in 0..8)
            for (x in 0..8) {
                if (field[y][x] == '.') safeCells++
                if (field[y][x] == '.' && exploringField[y][x] == '/') openedSafeCells++
                if (field[y][x].isDigit()) numbersCells++
                if (field[y][x].isDigit() && exploringField[y][x].isDigit()) openedNumbersCells++
            }

        return safeCells == openedSafeCells && numbersCells == openedNumbersCells
    }

    private fun showMines() {
        for (y in 0..8)
            for (x in 0..8)
                if (field[y][x] == 'X') {
                    fieldOfMarks[y][x] = '.'
                    exploringField[y][x] = 'X'
                }
    }

    fun check(mines: Int) {
        if (!notExploded) continueGame = false
        if (checkMines(mines)) continueGame = false
        if (checkSaveCells()) continueGame = false
    }

    fun printField() {
        println("\n │123456789│\n" +
                "—│—————————│")
        for ((y, line) in exploringField.withIndex()) {
            print("${y + 1}|")
            for (x in line.indices) {
                if (fieldOfMarks[y][x] == '*') print(fieldOfMarks[y][x]) else print(line[x])
            }
            println("|")
        }
        println("—│—————————│")
    }
}

fun main() {
    print("How many mines do you want on the field? ")

    val game = Field()
    val mines = readLine()!!.toInt()

    game.createMines(mines)
    game.printField()

    print("Set/unset mines marks or claim a cell as free: ")

    val firstCell = readLine()!!

    game.checkFirstCell(firstCell)
    game.createNumbers()
    game.setCellValue(firstCell)
    game.check(mines)

    while (game.continueGame) {
        print("Set/delete mines marks (x and y coordinates): ")

        game.setCellValue(readLine()!!)
        game.check(mines)
    }

    println(game.text)
}
