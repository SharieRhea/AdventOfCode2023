import java.io.File
import java.util.PriorityQueue

// map that denotes whether a given coordinate is currently occupied
private val occupiedSpace: HashMap<Triple<Int, Int, Int>, Int> = hashMapOf()
private val bricks: HashMap<Int, Brick> = hashMapOf()
private val graph: HashMap<Int, Pair<List<Int>, List<Int>>> = hashMapOf()

fun main() {
    val comparator: Comparator<Brick> = compareBy { it.coordinates.first().third }
    val bricksQueue: PriorityQueue<Brick> = PriorityQueue(comparator)
    var brickID = 0
    File("resources/day22Input.txt").readLines().forEach { line ->
        val endpoints = line.split("~")
        val start = endpoints[0].split(",")
        val end = endpoints[1].split(",")
        val coordinates: MutableList<Triple<Int, Int, Int>> = mutableListOf()

        // case where brick is only one block
        if (start[0].toInt() == end[0].toInt() && start[1].toInt() == end[1].toInt() && start[2].toInt() == end[2].toInt())
            coordinates.add(Triple(start[0].toInt(), start[1].toInt(), start[2].toInt()))

        // determine all the tiles that this brick occupies
        for (i in 0..2) {
            if (start[i].toInt() == end[i].toInt())
                continue

            for (j in start[i].toInt()..end[i].toInt()) {
                when (i) {
                    0 -> coordinates.add(Triple(j, start[1].toInt(), start[2].toInt()))
                    1 -> coordinates.add(Triple(start[0].toInt(), j, start[2].toInt()))
                    2 -> coordinates.add(Triple(start[0].toInt(), start[1].toInt(), j))
                }
            }
            break
        }

        bricks[brickID] = Brick(brickID, coordinates)
        brickID++
    }

    bricksQueue.addAll(bricks.values)
    // starting with bricks closest to the ground, try to move each brick down
    while (bricksQueue.isNotEmpty()) {
        val current = bricksQueue.poll()
        var moved = true
        while (moved) {
            moved = current.moveDown()
        }
    }

    // part 1, count non-brick bricks
    val candidates: HashSet<Int> = hashSetOf()
    val necessaryBricks: HashSet<Int> = hashSetOf()
    for (brick in bricks.values) {
        val results = brick.supportedBy()
        if (results.size > 1)
            candidates.addAll(results)
        else
            necessaryBricks.addAll(results)
        if (brick.isTopLevel())
            candidates.add(brick.id)
    }
    val disintegrated = candidates.filter { !necessaryBricks.contains(it) }
    println("Bricks safe to disintegrate: ${disintegrated.size}")

    // part 2
    // build graph in each direction
    for (i in 0..<bricks.size) {
        val supports = bricks[i]?.supports() ?: emptyList()
        val supportedBy = bricks[i]?.supportedBy() ?: emptyList()
        graph[i] = Pair(supports, supportedBy)
    }
    // recursively follow all supported bricks and keep count
    var fallingBricks = 0
    for (brick in bricks.values) {
        fallingBricks += brick.countBricks(mutableListOf())
    }
    println("Sum of falling bricks: $fallingBricks")
}

private class Brick(
    val id: Int,
    var coordinates: List<Triple<Int, Int, Int>>
) {
    init {
        // update space map
        for (coordinate in coordinates) {
            occupiedSpace[coordinate] = id
        }
    }

    fun moveDown() : Boolean {
        // already touching the ground
        if (coordinates.any { it.third == 1 })
            return false

        // determine if there is anything in a space directly below this block that is
        // not another part of this block
        if (coordinates.any { occupiedSpace[Triple(it.first, it.second, it.third - 1)] != null &&
            !coordinates.contains(Triple(it.first, it.second, it.third - 1))})
            return false

        // block can be moved down one tile
        val newList: MutableList<Triple<Int, Int, Int>> = mutableListOf()
        for (coordinate in coordinates) {
            // add the new coordinate
            newList.add(Triple(coordinate.first, coordinate.second, coordinate.third - 1))
            // remove old occupied space and update new
            occupiedSpace.remove(coordinate)
            occupiedSpace[Triple(coordinate.first, coordinate.second, coordinate.third - 1)] = id
        }
        coordinates = newList
        return true
    }

    fun supportedBy(): List<Int> {
        // find all the bricks that are directly below this one
        val brickBricks: HashSet<Int> = hashSetOf()
        for (coordinate in coordinates) {
            val below = occupiedSpace[Triple(coordinate.first, coordinate.second, coordinate.third - 1)]
            if (below != null) brickBricks.add(below)
        }
        // remove this brick from this list, it doesn't count
        brickBricks.remove(id)
        return brickBricks.toList()
    }

    fun supports(): List<Int> {
        // find all the bricks that are directly above this one
        val supportedBricks: HashSet<Int> = hashSetOf()
        for (coordinate in coordinates) {
            val above = occupiedSpace[Triple(coordinate.first, coordinate.second, coordinate.third + 1)]
            if (above != null) supportedBricks.add(above)
        }
        // remove this brick from the list, it doesn't count
        supportedBricks.remove(id)
        return supportedBricks.toList()
    }

    // a top level brick has nothing above it
    fun isTopLevel(): Boolean {
        return !coordinates.any { occupiedSpace[Triple(it.first, it.second, it.third + 1)] != null &&
            occupiedSpace[Triple(it.first, it.second, it.third + 1)] != id }
    }

    fun countBricks(falling: MutableList<Int>): Int {
        // first, determine which supported bricks would fall
        var count = 0
        val chain: MutableList<Int> = mutableListOf()
        for (brick in graph[id]!!.first) {
            if (falling.contains(brick))
                continue
			// if this brick is only supported by bricks in the falling list, it will also fall, add it
            if (!graph[brick]!!.second.any { it != id && !falling.contains(it) }) {
                count++
                chain.add(brick)
                falling.add(brick)
            }
        }

        // recursively add
        for (brick in chain) {
            count += bricks[brick]!!.countBricks(falling)
        }
        return count
    }
}
