import java.io.File
import java.util.*

val grid: MutableList<MutableList<Int>> = mutableListOf()
fun main() {
    val lines: List<String> = File("src/main/resources/test").readLines()
    lines.forEach { line ->
        grid.add(mutableListOf())
        line.forEach { tile ->
            grid[grid.size - 1].add(tile.digitToInt())
        }
    }

    val part1HeatLoss: Int = part1(grid)
    println("Minimal heat loss: $part1HeatLoss")
    val part2HeatLoss: Int = part2(grid)
    println("Ultra minimal heat loss: $part2HeatLoss")
}

enum class Direction {
    North, East, South, West
}

class Block(
    val coordinates: Pair<Int, Int>,
    val heatLoss: Int,
    val incomingDirection: Direction,
    val momentum: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Block) return false
        if (coordinates != other.coordinates ||
            incomingDirection != other.incomingDirection ||
            momentum != other.momentum)
            return false
        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(coordinates, incomingDirection, momentum)
    }
}

fun part1(grid: List<List<Int>>): Int {
    // hash set to keep track of already explored nodes
    val explored: HashSet<Block> = hashSetOf()
    // priority queue to determine order to explore nodes
    val comparator: Comparator<Block> = compareBy { it.heatLoss }
    val queue: PriorityQueue<Block> = PriorityQueue<Block>(comparator)

    // add the path going East
    queue.add(Block(Pair(1, 0), grid[0][1], Direction.West, 0))
    // add the path going South
    queue.add(Block(Pair(0, 1), grid[1][0], Direction.North, 0))

    while (queue.isNotEmpty()) {
        val current: Block = queue.poll()

        // do nothing if this node has already been explored, if not, add it and explore
        if (explored.contains(current))
            continue
        else
            explored.add(current)

        // reached the destination
        if (current.coordinates.first == grid[0].size -1 && current.coordinates.second == grid.size - 1)
            return current.heatLoss

        // if momentum is less than 2, moving straight is allowed
        if (current.momentum < 2) {
            val newCoordinates: Pair<Int, Int> = when (current.incomingDirection) {
                Direction.North -> getNewCoordinates(current.coordinates, Direction.South, 1)
                Direction.East -> getNewCoordinates(current.coordinates, Direction.West, 1)
                Direction.South -> getNewCoordinates(current.coordinates, Direction.North, 1)
                Direction.West -> getNewCoordinates(current.coordinates, Direction.East, 1)
            }
            if (isValidCoordinates(newCoordinates))
                queue.add(Block(
                    newCoordinates,
                    current.heatLoss + grid[newCoordinates.second][newCoordinates.first],
                    current.incomingDirection,
                    current.momentum + 1)
                )
        }

        // turning left or right is always allowed, momentum should be reset to 0
        // left turn
        var newCoordinates: Pair<Int, Int>
        var newDirection: Direction
        when (current.incomingDirection) {
            Direction.North -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.East, 1)
                newDirection = Direction.West
            }
            Direction.East -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.South, 1)
                newDirection = Direction.North
            }
            Direction.South -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.West, 1)
                newDirection = Direction.East
            }
            Direction.West -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.North, 1)
                newDirection = Direction.South
            }
        }
        if (isValidCoordinates(newCoordinates))
            queue.add(Block(
                newCoordinates,
                current.heatLoss + grid[newCoordinates.second][newCoordinates.first],
                newDirection,
                0
            ))

        // right turn
        when (current.incomingDirection) {
            Direction.North -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.West, 1)
                newDirection = Direction.East
            }
            Direction.East -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.North, 1)
                newDirection = Direction.South
            }
            Direction.South -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.East, 1)
                newDirection = Direction.West
            }
            Direction.West -> {
                newCoordinates =  getNewCoordinates(current.coordinates, Direction.South, 1)
                newDirection = Direction.North
            }
        }
        if (isValidCoordinates(newCoordinates))
            queue.add(Block(
                newCoordinates,
                current.heatLoss + grid[newCoordinates.second][newCoordinates.first],
                newDirection,
                0
            ))
    }
    return -1
}

fun part2(grid: List<List<Int>>): Int {
    // hash set to keep track of already explored nodes
    val explored: HashSet<Block> = hashSetOf()
    // priority queue to determine order to explore nodes
    val comparator: Comparator<Block> = compareBy { it.heatLoss }
    val queue: PriorityQueue<Block> = PriorityQueue<Block>(comparator)

    // add the path going East
    queue.add(Block(Pair(4, 0), accumulateHeat(Pair(0, 0), Direction.East, 4), Direction.West, 4))
    // add the path going South
    queue.add(Block(Pair(0, 4), accumulateHeat(Pair(0, 0), Direction.South, 4),  Direction.North, 4))

    while (queue.isNotEmpty()) {
        val current: Block = queue.poll()
        println("${current.coordinates} ${current.heatLoss} ${current.incomingDirection} ${current.momentum}")

        // do nothing if this node has already been explored, if not, add it and explore
        if (explored.contains(current))
            continue
        else
            explored.add(current)

        // reached the destination
        if (current.coordinates.first == grid[0].size -1 && current.coordinates.second == grid.size - 1)
            return current.heatLoss

        // if momentum is less than 10, moving straight is allowed
        if (current.momentum < 10) {
            val newCoordinates: Pair<Int, Int> = when (current.incomingDirection) {
                Direction.North -> getNewCoordinates(current.coordinates, Direction.South, 1)
                Direction.East -> getNewCoordinates(current.coordinates, Direction.West, 1)
                Direction.South -> getNewCoordinates(current.coordinates, Direction.North, 1)
                Direction.West -> getNewCoordinates(current.coordinates, Direction.East, 1)
            }
            if (isValidCoordinates(newCoordinates))
                queue.add(Block(
                    newCoordinates,
                    current.heatLoss + grid[newCoordinates.second][newCoordinates.first],
                    current.incomingDirection,
                    current.momentum + 1)
                )
        }

        // turning left or right is always allowed but a minimum of 4 moves is required
        // left turn
        var newCoordinates: Pair<Int, Int>
        var newDirection: Direction
        var accumulatedHeat: Int
        when (current.incomingDirection) {
            Direction.North -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.East, 4)
                newDirection = Direction.West
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.East, 4)
            }
            Direction.East -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.South, 4)
                newDirection = Direction.North
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.South, 4)
            }
            Direction.South -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.West, 4)
                newDirection = Direction.East
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.West, 4)
            }
            Direction.West -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.North, 4)
                newDirection = Direction.South
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.North, 4)
            }
        }
        if (isValidCoordinates(newCoordinates))
            queue.add(Block(
                newCoordinates,
                current.heatLoss + accumulatedHeat,
                newDirection,
                4
            ))

        // right turn
        when (current.incomingDirection) {
            Direction.North -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.West, 4)
                newDirection = Direction.East
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.West, 4)
            }
            Direction.East -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.North, 4)
                newDirection = Direction.South
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.North, 4)
            }
            Direction.South -> {
                newCoordinates = getNewCoordinates(current.coordinates, Direction.East, 4)
                newDirection = Direction.West
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.East, 4)
            }
            Direction.West -> {
                newCoordinates =  getNewCoordinates(current.coordinates, Direction.South, 4)
                newDirection = Direction.North
                accumulatedHeat = accumulateHeat(current.coordinates, Direction.South, 4)
            }
        }
        if (isValidCoordinates(newCoordinates))
            queue.add(Block(
                newCoordinates,
                current.heatLoss + accumulatedHeat,
                newDirection,
                4
            ))
    }
    return -1
}

fun getNewCoordinates(coordinates: Pair<Int, Int>, direction: Direction, length: Int): Pair<Int, Int> {
    return when (direction) {
        Direction.North -> Pair(coordinates.first, coordinates.second - length)
        Direction.East -> Pair(coordinates.first + length, coordinates.second)
        Direction.South -> Pair(coordinates.first, coordinates.second + length)
        Direction.West -> Pair(coordinates.first - length, coordinates.second)
    }
}

fun isValidCoordinates(coordinates: Pair<Int, Int>): Boolean {
    if (coordinates.first < 0 || coordinates.second < 0) return false
    return (coordinates.first < grid[0].size && coordinates.second < grid.size)
}

fun accumulateHeat(coordinates: Pair<Int, Int>, direction: Direction, length: Int): Int {
    var heat = 0
    for (i in 1..length) {
        val newCoordinates = getNewCoordinates(coordinates, direction, i)
        if (isValidCoordinates(newCoordinates))
            heat += grid[newCoordinates.second][coordinates.first]
    }
    // println("$coordinates $direction $length $heat")
    return heat
}