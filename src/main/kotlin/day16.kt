import java.io.File

data class Tile(val type: Char, val location: Pair<Int, Int>, var energized: Boolean = false, var split: Boolean = false)
data class Beam(val tile: Tile, val direction: Directions)

fun main() {
    val rows = ArrayList<ArrayList<Tile>>()
    var yIndex = 0
    File("src/main/resources/day16Input.txt").forEachLine { line ->
        var xIndex = 0
        val row = ArrayList<Tile>()
        for (char in line) {
            row.add(Tile(char, Pair(xIndex, yIndex)))
            xIndex++
        }
        rows.add(row)
        yIndex++
    }

    var currentBeam = Beam(Tile('.', Pair(-1, 0)), Directions.East)
    walk(rows, currentBeam)

    val energizedTiles = calculateTiles(rows)
    println("Total energized tiles: %d".format(energizedTiles))

    resetTiles(rows)
    val totals = ArrayList<Int>()
    // Try every left edge
    for (i in rows.indices) {
        currentBeam = Beam(Tile('.', Pair(-1, i)), Directions.East)
        walk(rows, currentBeam)
        totals.add(calculateTiles(rows))
        resetTiles(rows)
    }
    // Try every right edge
    for (i in rows.indices) {
        currentBeam = Beam(Tile('.', Pair(rows[0].lastIndex + 1, i)), Directions.West)
        walk(rows, currentBeam)
        totals.add(calculateTiles(rows))
        resetTiles(rows)
    }
    // Try every top edge
    for (i in rows[0].indices) {
        currentBeam = Beam(Tile('.', Pair(i, -1)), Directions.South)
        walk(rows, currentBeam)
        totals.add(calculateTiles(rows))
        resetTiles(rows)
    }
    // Try every bottom edge
    for (i in rows[0].indices) {
        currentBeam = Beam(Tile('.', Pair(i, rows.lastIndex + 1)), Directions.North)
        walk(rows, currentBeam)
        totals.add(calculateTiles(rows))
        resetTiles(rows)
    }

    println("Max energized tiles: %d".format(totals.max()))
}

private fun calculateTiles(grid: ArrayList<ArrayList<Tile>>): Int {
    var energizedTiles = 0
    for (row in grid) {
        for (tile in row) {
            if (tile.energized)
                energizedTiles++
        }
    }
    return energizedTiles
}

private fun resetTiles(grid: ArrayList<ArrayList<Tile>>) {
    for (row in grid) {
        for (tile in row) {
            tile.energized = false
            tile.split = false
        }
    }
}

fun walk(grid: ArrayList<ArrayList<Tile>>, beam: Beam) {
    var currentBeam = beam
    var x: Int
    var y: Int

    // Every beam will either exit or loop to an already split tile
    while (true) {
        x = currentBeam.tile.location.first
        y = currentBeam.tile.location.second
        when (currentBeam.direction) {
            Directions.North -> {
                if (y - 1 < 0)
                    return
                // Energize the tile that we are passing through
                val tile = grid[y - 1][x]
                tile.energized = true
                if (tile.split)
                    return
                // Update currentBeam
                var direction = currentBeam.direction
                when (tile.type) {
                    '/' -> direction = Directions.East
                    '\\' -> direction = Directions.West
                    '-' -> {
                        tile.split = true
                        walk(grid, Beam(tile, Directions.East))
                        walk(grid, Beam(tile, Directions.West))
                        return
                    }
                }
                currentBeam = Beam(tile, direction)
            }
            Directions.East -> {
                if (x + 1 > grid[0].lastIndex)
                    return
                val tile = grid[y][x + 1]
                tile.energized = true
                if (tile.split)
                    return
                var direction = currentBeam.direction
                when (tile.type) {
                    '/' -> direction = Directions.North
                    '\\' -> direction = Directions.South
                    '|' -> {
                        tile.split = true
                        walk(grid, Beam(tile, Directions.North))
                        walk(grid, Beam(tile, Directions.South))
                        return
                    }
                }
                currentBeam = Beam(tile, direction)
            }
            Directions.South -> {
                if (y + 1 > grid.lastIndex)
                    return
                // Energize the tile that we are passing through
                val tile = grid[y + 1][x]
                tile.energized = true
                if (tile.split)
                    return
                var direction = currentBeam.direction
                when (tile.type) {
                    '/' -> direction = Directions.West
                    '\\' -> direction = Directions.East
                    '-' -> {
                        tile.split = true
                        walk(grid, Beam(tile, Directions.East))
                        walk(grid, Beam(tile, Directions.West))
                        return
                    }
                }
                currentBeam = Beam(tile, direction)
            }
            Directions.West -> {
                if (x - 1 < 0)
                    return
                val tile = grid[y][x - 1]
                tile.energized = true
                if (tile.split)
                    return
                var direction = currentBeam.direction
                when (tile.type) {
                    '/' -> direction = Directions.South
                    '\\' -> direction = Directions.North
                    '|' -> {
                        tile.split = true
                        walk(grid, Beam(tile, Directions.North))
                        walk(grid, Beam(tile, Directions.South))
                        return
                    }
                }
                currentBeam = Beam(tile, direction)
            }
            Directions.None -> {}
        }
    }
}