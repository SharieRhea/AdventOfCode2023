import java.io.File

fun main() {
    val map: HashMap<Pair<Int, Int>, List<Block>> = hashMapOf()
    var y = 0
    var x = 0
    File("src/main/resources/test.txt").forEachLine { line ->
        line.forEach { character ->
            val heatLoss = character.digitToInt()
            val coordinates: Pair<Int, Int> = Pair(x, y)
            val nodesList: MutableList<Block> = mutableListOf()
            // need a node for each combination of direction and momentum
            for (i in 0..3) {
                nodesList.add(Block(coordinates, heatLoss, Direction.North, i, mutableListOf()))
                nodesList.add(Block(coordinates, heatLoss, Direction.East, i, mutableListOf()))
                nodesList.add(Block(coordinates, heatLoss, Direction.South, i, mutableListOf()))
                nodesList.add(Block(coordinates, heatLoss, Direction.West, i, mutableListOf()))
            }
            map[coordinates] = nodesList
            x++
        }
        y++
    }

    for (location in map) {
        // most nodes can go in the three directions that are not the incoming direction
        // only exception is if momentum is already 3
    }
}

enum class Direction {
    North, East, South, West
}

data class Block(
    val coordinates: Pair<Int, Int>,
    val heatLoss: Int,
    val incomingDirection: Direction,
    val momentum: Int,
    val neighbors: MutableList<Block>
)