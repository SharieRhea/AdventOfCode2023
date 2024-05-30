import java.io.File

private val plots = constructMap()
private var startPosition: Pair<Int, Int> = Pair(0, 0)

fun main() {
    constructMap()
    println("Part 1: " + bfs(64, 1))
    println("Part 2: " + part2())
}

private fun bfs(steps: Int, part: Int): Int {
    val beginningPositions: ArrayDeque<Pair<Int, Int>> = ArrayDeque()
    beginningPositions.add(startPosition)
    val endingPositions: HashSet<Pair<Int, Int>> = hashSetOf()

    for (i in 0..<steps) {
        endingPositions.clear()
        while (beginningPositions.isNotEmpty()) {
            val current = beginningPositions.removeFirst()
            // North
            if (isValidMove(Pair(current.first, current.second - 1), part))
                endingPositions.add(Pair(current.first, current.second - 1))
            // South
            if (isValidMove(Pair(current.first, current.second + 1), part))
                endingPositions.add(Pair(current.first, current.second + 1))
            // East
            if (isValidMove(Pair(current.first + 1, current.second), part))
                endingPositions.add(Pair(current.first + 1, current.second))
            // West
            if (isValidMove(Pair(current.first - 1, current.second), part))
                endingPositions.add(Pair(current.first - 1, current.second))
        }
        beginningPositions.addAll(endingPositions)
    }
    return endingPositions.size
}

private fun part2(): Long {
    val y0 = bfs(65, 2)
    val y1 = bfs(196, 2)
    val y2 = bfs(327, 2)

    // use simplified LaGrange interpolation to find quadratic coefficients
    val a = (y0 - 2 * y1 + y2) / 2
    val b = (-3 * y0 + 4 * y1 - y2) / 2
    // c = y0

    // since we have shifted values to make x = [0, 1, 2] for calculating coefficients
    // must also modify our target x value
    val x: Long = (26501365 - 65) / 131
    return (a * x * x) + (b * x) + y0
}

private fun isValidMove(coordinates: Pair<Int, Int>, part: Int): Boolean {
    if (part == 1) {
        // check if within grid bounds
        if (coordinates.first < 0 || coordinates.second < 0 ||
            coordinates.first >= plots[0].size || coordinates.second >= plots.size
        )
            return false
        // not a valid move it there is a wall
        return plots[coordinates.second][coordinates.first]
    }

    // in part 2 the map wraps infinitely, mod to determine position on original tile
    var x = coordinates.first % plots[0].size
    if (x < 0) x += plots[0].size
    var y = coordinates.second % plots.size
    if (y < 0) y+= plots.size
    return plots[y][x]
}

private fun constructMap(): List<List<Boolean>> {
    val columns: MutableList<List<Boolean>> = mutableListOf()
    File("resources/day21Input.txt").readLines().forEach { line ->
        val row: MutableList<Boolean> = mutableListOf()
        line.forEach { character ->
            when (character) {
                'S' -> {
                    startPosition = Pair(row.size, columns.size)
                    row.add(true)
                }
                '.' -> row.add(true)
                else -> row.add(false)
            }
        }
        columns.add(row)
    }
    return columns
}
