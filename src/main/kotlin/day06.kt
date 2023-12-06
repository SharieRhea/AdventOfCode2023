import java.io.File
import kotlin.math.*

fun main() {
    var waysToWinSum = 0

    // Parsing input
    val lines = File("src/main/resources/day06Input.txt").readLines()
    val times = lines[0].split("( )+".toRegex()).toMutableList()
    times.removeAt(0)
    val timesList = times.map { it.toInt() }
    val distances = lines[1].split("( )+".toRegex()).toMutableList()
    distances.removeAt(0)
    val distancesList = distances.map { it.toInt() }

    // Get number of ways to win for each race
    for (i in timesList.indices) {
        val number = getNumberOfWaysToWin(timesList[i], distancesList[i])
        if (waysToWinSum == 0)
            waysToWinSum = number
        else
            waysToWinSum *= number
    }

    // Formatting the list of numbers into one long value for time and distance
    val newTime: Long
    var newTimeString = ""
    val newDistance: Long
    var newDistanceString = ""
    for (i in timesList.indices) {
        newTimeString += timesList[i]
        newDistanceString += distancesList[i]
    }
    newTime = newTimeString.toLong()
    newDistance = newDistanceString.toLong()

    val singleRaceSum = getNumberOfWaysToWin(newTime, newDistance)

    println("Ways to win sum: %d".format(waysToWinSum))
    println("Ways to win sum for single race: %d".format(singleRaceSum))
}

// Use quadratic formula to find the intercept for the "winning" race time
// Ceiling used to get the lowest whole number of seconds that still wins
fun getMinimumTime(time: Int, distance: Int): Int {
    val returnVal = (-time - sqrt(time.toDouble().pow(2.0) - 4 * distance))/2.0
    return ceil(returnVal).toInt()
}
// Use quadratic formula to find the intercept for the "winning" race time
// Floor used to get the largest whole number of seconds that still wins
fun getMaximumTime(time: Int, distance: Int): Int {
    val returnVal = (-time + sqrt(time.toDouble().pow(2.0) - 4 * distance))/2.0
    return floor(returnVal).toInt()
}

fun getNumberOfWaysToWin(time: Int, distance: Int): Int {
    val minimum = getMinimumTime(time, distance)
    val maximum = getMaximumTime(time, distance)
    // All whole numbers from minimum to maximum inclusive
    return maximum - minimum + 1
}

// Overloaded function for part 2 using longs
fun getNumberOfWaysToWin(time: Long, distance: Long): Long {
    val minimum = ceil((-time - sqrt(time.toDouble().pow(2.0) - 4 * distance)) /2.0).toLong()
    val maximum = floor((-time + sqrt(time.toDouble().pow(2.0) - 4 * distance))/2.0).toLong()
    return maximum - minimum + 1
}
