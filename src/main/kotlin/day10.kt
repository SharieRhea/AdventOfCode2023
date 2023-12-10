import java.io.File

enum class Directions {
    North, South, East, West, None
}

data class Pipe(val value: Char, val partOfLoop: Boolean = false)

fun main() {
    val grid = ArrayList<ArrayList<Pipe>>()
    var start = Pair(0, 0)
    File("src/main/resources/day10Input.txt").forEachLine {line ->
        val list = ArrayList<Pipe>().toMutableList()
        line.forEach { char ->
            list.add(Pipe(char))
            if (char == 'S') {
                start = Pair(list.lastIndex, grid.lastIndex + 1)
                list[list.lastIndex] = Pipe('L')
            }
        }
        grid.add(list as ArrayList<Pipe>)
    }

    var currentLocation = Pair(start.first, start.second)
    var steps = 0
    var directionComingFrom = Directions.East
    // Traverse the entire main loop, using the direction coming from to determine path
    // Since we're traversing the whole loop, set boolean flag that this point is part of the path
    do {
        val pipe = grid[currentLocation.second][currentLocation.first]
        grid[currentLocation.second][currentLocation.first] = Pipe(pipe.value, true)
        when (pipe.value) {
            'L' -> {
                if (directionComingFrom == Directions.East) {
                    currentLocation  = Pair(currentLocation.first, currentLocation.second - 1)
                    directionComingFrom = Directions.South
                }
                else {
                    currentLocation = Pair(currentLocation.first + 1, currentLocation.second)
                    directionComingFrom = Directions.West
                }
            }
            'J' -> {
                if (directionComingFrom == Directions.West) {
                    currentLocation = Pair(currentLocation.first, currentLocation.second - 1)
                    directionComingFrom = Directions.South
                }
                else {
                    currentLocation = Pair(currentLocation.first - 1, currentLocation.second)
                    directionComingFrom = Directions.East
                }
            }
            '|' -> {
                if (directionComingFrom == Directions.North) {
                    currentLocation = Pair(currentLocation.first, currentLocation.second + 1)
                    directionComingFrom = Directions.North
                }
                else {
                    currentLocation = Pair(currentLocation.first, currentLocation.second - 1)
                    directionComingFrom = Directions.South
                }
            }
            '-' -> {
                if (directionComingFrom == Directions.West) {
                    currentLocation = Pair(currentLocation.first + 1, currentLocation.second)
                    directionComingFrom = Directions.West
                }
                else {
                    currentLocation = Pair(currentLocation.first - 1, currentLocation.second)
                    directionComingFrom = Directions.East
                }
            }
            '7' -> {
                if (directionComingFrom == Directions.West) {
                    currentLocation = Pair(currentLocation.first, currentLocation.second + 1)
                    directionComingFrom = Directions.North
                }
                else {
                    currentLocation = Pair(currentLocation.first - 1, currentLocation.second)
                    directionComingFrom = Directions.East
                }
            }
            'F' -> {
                if (directionComingFrom == Directions.East) {
                    currentLocation = Pair(currentLocation.first, currentLocation.second + 1)
                    directionComingFrom = Directions.North
                }
                else {
                    currentLocation = Pair(currentLocation.first + 1, currentLocation.second)
                    directionComingFrom = Directions.West
                }
            }
            else -> throw Exception("Exited pipe network.")
        }
        steps++
    } while (currentLocation != start)

    // The max number of steps (AKA to the furthest point of the loop) is steps / 2
    println("Steps to far side of loop: %d".format(steps / 2))

    // Part 2
    // This nested code is not very pretty, but it works
    var pointsInside = 0
    for (row in grid) {
        var areInside = false
        // Keep track of direction entered to determine if we are entering or just
        // going along a wall (ex. L--J is just going along an edge, not entering, L--7 is entering)
        var directionEntered = Directions.None
        for (pipe in row) {
            if (pipe.partOfLoop && pipe.value != '-') {
                when (directionEntered) {
                    Directions.None -> {
                        when (pipe.value) {
                            'L', 'J' -> directionEntered = Directions.North
                            'F', '7' -> directionEntered = Directions.South
                            '|' -> areInside = !areInside
                        }
                    }
                    Directions.North -> {
                        when (pipe.value) {
                            'L', 'J' -> directionEntered = Directions.None
                            'F', '7' -> {
                                // Direction going is different from entered, so flip boolean
                                directionEntered = Directions.None
                                areInside = !areInside
                            }
                            '|' -> areInside = !areInside
                        }
                    }
                    Directions.South -> {
                        when (pipe.value) {
                            'L', 'J' -> {
                                // Direction going is different from entered, so flip boolean
                                directionEntered = Directions.None
                                areInside = !areInside
                            }
                            'F', '7' -> directionEntered = Directions.None
                            '|' -> areInside = !areInside
                        }
                    }
                    else -> throw Exception("Direction entered is invalid.")
                }
            }
            else if (!pipe.partOfLoop) {
                if (areInside)
                    pointsInside++
            }
        }
    }
    println("Tiles enclosed by the loop: %d".format(pointsInside))
}
