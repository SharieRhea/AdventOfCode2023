import java.io.File
import java.util.PriorityQueue

// map that denotes whether a given coordinate is currently occupied
private val occupiedSpace: HashMap<Triple<Int, Int, Int>, Int> = hashMapOf()
private val bricks: HashMap<Int, Brick> = hashMapOf()

fun main() {
    val comparator: Comparator<Brick> = compareBy { it.coordinates.first().third }
    val bricksQueue: PriorityQueue<Brick> = PriorityQueue(comparator)
    var brickID = 0
    File("src/main/resources/day22Input.txt").readLines().forEach { line ->
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
        // update the occupiedSpace map for this brick's starting position
        for (coordinate in coordinates) {
            occupiedSpace[coordinate] = brickID
        }
        bricksQueue.add(Brick(brickID, coordinates))
        bricks[brickID] = Brick(brickID, coordinates)
        brickID++
    }

    val bricksList: MutableList<Brick> = mutableListOf()
    bricksList.addAll(bricksQueue)
    // starting with bricks closest to the ground, try to move each brick down
    while (bricksQueue.isNotEmpty()) {
        val current = bricksQueue.poll()
        var moved = true
        while (moved) {
            moved = current.moveDown()
        }
    }

    // count non-supporting bricks
    val disintegrated: HashSet<Int> = hashSetOf()
    for (brick in bricksList) {
        val results = brick.supportedBy()
        if (results.size > 1)
            disintegrated.addAll(results)
        if (brick.isTopLevel())
            disintegrated.add(brick.id)
    }
    println(disintegrated)
    println("Bricks safe to disintegrate: ${disintegrated.size}")
}

private class Brick(
    val id: Int,
    var coordinates: List<Triple<Int, Int, Int>>
) {
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
            occupiedSpace.remove(Triple(coordinate.first, coordinate.second, coordinate.third))
            occupiedSpace[Triple(coordinate.first, coordinate.second, coordinate.third - 1)] = id
        }
        coordinates = newList
        return true
    }

    fun supportedBy(): List<Int> {
        // find all the bricks that are directly below this one
        val supportingBricks: HashSet<Int> = hashSetOf()
        for (coordinate in coordinates) {
            val below = occupiedSpace[Triple(coordinate.first, coordinate.second, coordinate.third - 1)]
            if (below != null) supportingBricks.add(below)
        }
        // remove this brick from this list, it doesn't count
        supportingBricks.remove(id)
        return supportingBricks.toList()
    }

    fun isTopLevel(): Boolean {
        return !coordinates.any { occupiedSpace[Triple(it.first, it.second, it.third + 1)] != null &&
            occupiedSpace[Triple(it.first, it.second, it.third + 1)] != id }
    }
}

// 611 too high