import java.io.File
import kotlin.math.abs

data class Trench(val direction: Char, val meters: Int, val color: String)
data class BigTrench(val direction: Char, val meters: Long)

fun main() {
    partOne()
    partTwo()
}

private fun partOne() {
    val map = HashMap<Pair<Int, Int>, Trench>()
    var location = Pair(0, 0)
    File("resources/day18Input.txt").forEachLine { line ->
        val contents = line.split(" ")
        val direction = contents[0].toCharArray()[0]
        val meters = contents[1].toInt()
        // ensure that vertical directions take precedence
        if (direction == 'U' || direction == 'D')
            map[location] = Trench(direction, meters, contents[2])

        for (i in 1..meters) {
            when (direction) {
                'U' -> map[Pair(location.first, location.second - i)] = Trench(direction, meters, contents[2])
                'D' -> map[Pair(location.first, location.second + i)] = Trench(direction, meters, contents[2])
                'L' -> map[Pair(location.first - i, location.second)] = Trench(direction, meters, contents[2])
                'R' -> map[Pair(location.first + i, location.second)] = Trench(direction, meters, contents[2])
            }
        }

        // Update location to the last point
        when (direction) {
            'U' -> location = Pair(location.first, location.second - meters)
            'D' -> location = Pair(location.first, location.second + meters)
            'L' -> location = Pair(location.first - meters, location.second)
            'R' -> location = Pair(location.first + meters, location.second)
        }

    }

    val min = map.keys.minOf { it.second }
    val max = map.keys.maxOf { it.second }

    var sum = 0
    for (i in min..max) {
        // sort by x value
        val edges = map.filter { it.key.second == i }.toList().sortedBy { it.first.first }

        var inside = false
        var previous = edges[0]
        for (edge in edges) {
            // add the edge itself
            sum++

            // we've jumped
            if (edge.first.first - 1 != previous.first.first && inside) {
                sum += abs(edge.first.first - previous.first.first - 1)
            }

            // change flag if necessary
            if (edge.second.direction == 'U')
                inside = true
            else if (edge.second.direction == 'D')
                inside = false

            previous = edge
        }
    }

    println("Total cubic meters: %d".format(sum))
}

private fun partTwo() {
    val map = HashMap<Pair<Long, Long>, BigTrench>()
    var location = Pair(0L, 0L)
    var sum = 0L
    var trenches = 0L

    File("src/main/resources/day18Input.txt").forEachLine { line ->
        val contents = line.split(" ")
        val instructions = contents[2].substring(2, contents[2].lastIndex - 1)
        val meters = instructions.toLong(16)
        val directionNumber = contents[2][contents[2].lastIndex - 1]
        var direction = 'U'
        when (directionNumber) {
            '0' -> direction = 'R'
            '1' -> direction = 'D'
            '2' -> direction = 'L'
            '3' -> direction = 'U'
        }
        map[location] = BigTrench(direction, meters)
        trenches += meters

        // Update location to the last point
        var temp = location
        when (direction) {
            'U' -> temp = Pair(location.first, location.second - meters)
            'D' -> temp = Pair(location.first, location.second + meters)
            'L' -> temp = Pair(location.first - meters, location.second)
            'R' -> temp = Pair(location.first + meters, location.second)
        }

        // Shoelace algorithm to keep track of area
        sum += location.second * temp.first
        sum -= location.first * temp.second
        location = temp
    }

    if (sum < 0)
        sum *= -1
    // Adjust area based on missing corners
    sum = (sum + trenches) / 2 + 1
    println("Total cubic meters new instructions: %d".format(sum))
}
