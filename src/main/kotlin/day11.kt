import java.io.File
import kotlin.math.abs

fun main() {
    process(1)
    process(999999)
}

fun process(expansionAmount: Long) {
    val galaxies = ArrayList<Pair<Long, Long>>()
    var x: Long
    // Keep track of total columns due to expansion
    var numberOfColumns = 0L
    var y = 0L
    File("src/main/resources/day11Input.txt").forEachLine { line ->
        x = 0
        var emptySpace = true
        line.forEach { char ->
            if (char == '#') {
                galaxies.add(Pair(x, y))
                emptySpace = false
            }
            x++
        }
        numberOfColumns = x
        y++
        // Account for expansion of space for rows with no galaxies
        if (emptySpace)
            y += expansionAmount
    }

    // Determine column expansion
    var column = 0L
    while(column < numberOfColumns) {
        var emptySpace = true
        // If there is any galaxy with this x value, then the column is not empty
        for (galaxy in galaxies) {
            if (galaxy.first == column) {
                emptySpace = false
                break
            }
        }

        // Process expansion, then increase total columns and update current position
        if (emptySpace) {
            shiftGalaxies(galaxies, column, expansionAmount)
            numberOfColumns += expansionAmount
            column += expansionAmount
        }
        column++
    }

    // Use a set to get unique pairs
    val pairs = ArrayList<Pair<Pair<Long, Long>, Pair<Long, Long>>>()
    for (i in galaxies.indices) {
        for (j in galaxies.indices) {
            // Always add with lower number first to prevent double counting
            if (i < j)
                pairs.add(Pair(galaxies[i], galaxies[j]))
            else if (j < i)
                pairs.add(Pair(galaxies[j], galaxies[i]))
            // Ignore pairs of the galaxy with itself
        }
    }
    val setOfPairs = pairs.toSet()

    // Calculate distances, no diagonals so it is always x distance + y distance
    var sumOfLengths = 0L
    for (pair in setOfPairs) {
        sumOfLengths += abs(pair.first.first - pair.second.first)
        sumOfLengths += abs(pair.first.second - pair.second.second)
    }

    println("Sum of the lengths between galaxies: %d".format(sumOfLengths))
}

fun shiftGalaxies(galaxies: ArrayList<Pair<Long, Long>>, start: Long, amount: Long) {
    for (i in galaxies.indices) {
        if (galaxies[i].first > start)
            galaxies[i] = Pair(galaxies[i].first + amount, galaxies[i].second)
    }
}