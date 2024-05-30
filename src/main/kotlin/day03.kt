import java.io.File

data class Point(
    val coordinates: Pair<Int, Int>,
    val character: Char,
    val isDigit: Boolean,
    val isSymbol: Boolean,
    var counted: Boolean
)

fun main() {
    var x: Int
    var y = 0
    val list = ArrayList<ArrayList<Point>>()
    // Populate list of lists to simulate a grid
    File("resources/day03Input.txt").forEachLine { line ->
        x = 0
        val row = ArrayList<Point>()
        line.forEach { character ->
            if (character.isDigit())
                row.add(Point(Pair(x, y), character, true, false, false))
            else if (character != '.')
                row.add(Point(Pair(x, y), character, false, true, false))
            else
                row.add(Point(Pair(x, y), character, false, false, false))
            x++
        }
        list.add(row)
        y++
    }

    // Check every point, if it's a symbol, check for adjacent numbers
    var sum = 0
    for (yIndex in list.indices) {
        for (xIndex in list[yIndex].indices) {
            if (list[yIndex][xIndex].isSymbol) {
                sum += checkAdjacent(xIndex, yIndex, list)
            }
        }
    }
    println("Sum of the part numbers: %d".format(sum))

    // Reset the counted boolean to prepare for checking gear ratios
    for (yIndex in list.indices) {
        for (xIndex in list[yIndex].indices) {
            list[yIndex][xIndex].counted = false
        }
    }

    // Check every point, if it's a *, check for adjacent numbers
    var gearRatioSum = 0
    for (yIndex in list.indices) {
        for (xIndex in list[yIndex].indices) {
            if (list[yIndex][xIndex].character == '*') {
                gearRatioSum += checkRatio(xIndex, yIndex, list)
            }
        }
    }
    println("Sum of the gear ratios: %d".format(gearRatioSum))
}

// Check every adjacent point on the grid, and get its number if valid
fun checkAdjacent(x: Int, y: Int, list: ArrayList<ArrayList<Point>>): Int {
    var sum = 0
    if (list[y-1][x-1].isDigit)
        sum += getNumber(x-1, y-1, list)
    if (list[y-1][x].isDigit)
        sum += getNumber(x, y-1, list)
    if (list[y-1][x+1].isDigit)
        sum += getNumber(x+1, y-1, list)
    if (list[y][x-1].isDigit)
        sum += getNumber(x-1, y, list)
    if (list[y][x+1].isDigit)
        sum += getNumber(x+1, y, list)
    if (list[y+1][x-1].isDigit)
        sum += getNumber(x-1, y+1, list)
    if (list[y+1][x].isDigit)
        sum += getNumber(x, y+1, list)
    if (list[y+1][x+1].isDigit)
        sum += getNumber(x+1, y+1, list)
    return sum
}

fun getNumber(x: Int, y: Int, list: ArrayList<ArrayList<Point>>): Int {
    // Move to the left until a non-digit is found, at beginning of number
    var xPointer = x
    while (xPointer > -1 && list[y][xPointer].isDigit) {
        xPointer--
    }

    var number = "0"
    // Move one forward to be at first digit of the number
    xPointer++
    while (xPointer < list[0].size && list[y][xPointer].isDigit) {
        // If any part of the number has been counted before, skip it
        if (list[y][xPointer].counted)
            break

        number += list[y][xPointer].character
        list[y][xPointer].counted = true
        xPointer++
    }
    return number.toInt()
}

// Check adjacent numbers, but keep track of how many, only exactly 2 matters
fun checkRatio(x: Int, y: Int, list: ArrayList<ArrayList<Point>>): Int {
    var ratio = 0
    var numberOfPartNumbers = 0
    var number: Int
    if (list[y-1][x-1].isDigit) {
        number = getNumber(x - 1, y - 1, list)
        if (number > 0) {
            numberOfPartNumbers++
            ratio = number
        }
    }
    if (list[y-1][x].isDigit) {
        number = getNumber(x, y - 1, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    if (list[y-1][x+1].isDigit) {
        number = getNumber(x + 1, y - 1, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    if (list[y][x-1].isDigit) {
        number = getNumber(x - 1, y, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    if (list[y][x+1].isDigit) {
        number = getNumber(x + 1, y, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    if (list[y+1][x-1].isDigit) {
        number = getNumber(x - 1, y + 1, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    if (list[y+1][x].isDigit) {
        number = getNumber(x, y + 1, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    if (list[y+1][x+1].isDigit) {
        number = getNumber(x + 1, y + 1, list)
        if (number > 0) {
            numberOfPartNumbers++
            if (numberOfPartNumbers == 1)
                ratio = number
            else
                ratio *= number
        }
    }
    return if (numberOfPartNumbers == 2)
        ratio
    else
        0
}
