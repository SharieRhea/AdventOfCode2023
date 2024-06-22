import java.io.File
import java.util.*

fun main() {
	val tiles: MutableList<MutableList<Char>> = mutableListOf()
	val lines = File("resources/day23Input.txt").readLines()
	lines.forEach { line ->
		tiles.add(mutableListOf())
		line.forEach { tile ->
			tiles[tiles.size - 1].add(tile)
		}
	}

	println("Longest path wet hike: " + part1(tiles))
	println("Longest path dry hike: " + (part2(tiles) - 1))
}

fun part1(tiles: List<List<Char>>): Int {
	// queue of nodes to explore
	val queue: ArrayDeque<Path> = ArrayDeque()
	// total steps for each possible paths
	var stepCounts: MutableList<Int> = mutableListOf()

	// add the starting tile to the queue to begin
	queue.add(Path(Pair(1, 0), tiles[0][1], 0, hashSetOf()))

	while (queue.isNotEmpty()) {
		val current: Path = queue.removeLast()
		
		if (current.visited.contains(current.coordinates)) 
			continue
		else 
			current.visited.add(current.coordinates)

		// reached the end destination, record number of steps
		if (current.coordinates == Pair(tiles[0].lastIndex - 1, tiles.lastIndex)) {
			stepCounts.add(current.steps)
			continue
		}

		when (current.type) {
			// special paths force a direction
			'>' -> {
				val coords = Pair(current.coordinates.first + 1, current.coordinates.second)
				queue.add(Path(coords, tiles[coords.second][coords.first], current.steps + 1, current.visited.toHashSet()))
			}
			'<' -> {
				val coords = Pair(current.coordinates.first - 1, current.coordinates.second)
				queue.add(Path(coords, tiles[coords.second][coords.first], current.steps + 1, current.visited.toHashSet()))
			}
			'v' -> {
				val coords = Pair(current.coordinates.first, current.coordinates.second + 1)
				queue.add(Path(coords, tiles[coords.second][coords.first], current.steps + 1, current.visited.toHashSet()))
			}
			'^' -> {
				val coords = Pair(current.coordinates.first, current.coordinates.second - 1)
				queue.add(Path(coords, tiles[coords.second][coords.first], current.steps + 1, current.visited.toHashSet()))
			}
			'.' -> {
				// check each direction for a normal path tile
				var coords = Pair(current.coordinates.first + 1, current.coordinates.second)
				if (validCoords(coords, tiles)) {
					val type = tiles[coords.second][coords.first]
					if (type != '#') {
						queue.add(Path(coords, type, current.steps + 1, current.visited.toHashSet()))
					}
				}
				
				coords = Pair(current.coordinates.first - 1, current.coordinates.second)
				if (validCoords(coords, tiles)) {
					val type = tiles[coords.second][coords.first]
					if (type != '#')
						queue.add(Path(coords, type, current.steps + 1, current.visited.toHashSet()))
				}
				
				coords = Pair(current.coordinates.first, current.coordinates.second + 1)
				if (validCoords(coords, tiles)) {
					val type = tiles[coords.second][coords.first]
					if (type != '#')
						queue.add(Path(coords, type, current.steps + 1, current.visited.toHashSet()))
				}

				coords = Pair(current.coordinates.first, current.coordinates.second - 1)
				if (validCoords(coords, tiles)) {
					val type = tiles[coords.second][coords.first]
					if (type != '#')
						queue.add(Path(coords, type, current.steps + 1, current.visited.toHashSet()))
				}
			}
		}
	}	
	return stepCounts.max()
}

private fun validCoords(coords: Pair<Int, Int>, tiles: List<List<Char>>): Boolean {
	return coords.first >= 0 && coords.second >= 0 && coords.first < tiles[0].size && coords.second < tiles.size
}

private fun part2(tiles: List<List<Char>>): Int {
	// create an object for each tile, cost is 1 for everything to start
	val intersectionMap: HashMap<Pair<Int, Int>, Intersection> = hashMapOf()
	var y = 0
	tiles.forEach { row ->
		var x = 0
		row.forEach { tile -> 
			if (tile != '#') {
				// this tile is valid, check its neighbors
				val neighbors: MutableList<Pair<Pair<Int, Int>, Int>> = mutableListOf()
				// check each direction
				if (validCoords(Pair(x + 1, y), tiles)) {
					if (tiles[y][x + 1] != '#') 
						neighbors.add(Pair(Pair(x + 1, y), 1))
				}	
				if (validCoords(Pair(x - 1, y), tiles)) {
					if (tiles[y][x - 1] != '#') 
						neighbors.add(Pair(Pair(x - 1, y), 1))
				}
				if (validCoords(Pair(x, y + 1), tiles)) {
					if (tiles[y + 1][x] != '#') 
						neighbors.add(Pair(Pair(x, y + 1), 1))
				}
				if (validCoords(Pair(x, y - 1), tiles)) {
					if (tiles[y - 1][x] != '#') 
						neighbors.add(Pair(Pair(x, y - 1), 1))
				}
				
				intersectionMap[Pair(x, y)] = Intersection(Pair(x, y), neighbors)
			}
			x++
		}
		y++
	}

	val tilesToRemove: MutableList<Pair<Int, Int>> = mutableListOf()
	// for each tile, if it only has two neighbors, may be "compressed"
	for ((coordinates, intersection) in intersectionMap) {
		if (intersection.neighbors.size != 2)
			continue

		val neighbor1: Intersection = intersectionMap[intersection.neighbors[0].first]!!
		val neighbor2: Intersection = intersectionMap[intersection.neighbors[1].first]!!

		// remove this tile from the map later since its unnecessary
		tilesToRemove.add(coordinates)

		// find the neighbors' connections to this node
		val connection1 = neighbor1.neighbors.first { it.first == coordinates }
		val connection2 = neighbor2.neighbors.first { it.first == coordinates }

		// add new connections to bypass this node
		neighbor1.neighbors.add(Pair(intersection.neighbors[1].first, connection2.second + connection1.second))
		neighbor2.neighbors.add(Pair(intersection.neighbors[0].first, connection1.second + connection2.second))

		// remove connections to this node
		neighbor1.neighbors.remove(connection1)
		neighbor2.neighbors.remove(connection2)

	}

	// remove unnecessary mappings
	for (tile in tilesToRemove) {
		intersectionMap.remove(tile)
	}

	// now run pathfind with new map
	return pathfind(intersectionMap, tiles)
}

private fun pathfind(map: HashMap<Pair<Int, Int>, Intersection>, tiles: List<List<Char>>): Int {
	// queue of nodes to explore
	val queue: ArrayDeque<Pair<Intersection, Int>> = ArrayDeque()
	// total steps for each possible paths
	var stepCounts: MutableList<Int> = mutableListOf()

	// add the starting tile to the queue to begin
	queue.add(Pair(map[Pair(1, 0)]!!, 1))

	while (queue.isNotEmpty()) {
		val current: Pair<Intersection, Int> = queue.removeLast()
		
		if (current.first.visited.contains(current.first.coordinates)) 
			continue
		else 
			current.first.visited.add(current.first.coordinates)

		// reached the end destination, record number of steps
		if (current.first.coordinates == Pair(tiles[0].lastIndex - 1, tiles.lastIndex)) {
			stepCounts.add(current.second)
			continue
		}

		// add each neighbor to the queue
		for (neighbor in current.first.neighbors) {
			queue.add(Pair(Intersection(neighbor.first, map[neighbor.first]!!.neighbors, current.first.visited.toHashSet()), current.second + neighbor.second))
		}
	}
	return stepCounts.max()
}

private class Path(
	val coordinates: Pair<Int, Int>,
	val type: Char,
	var steps: Int,
	val visited: HashSet<Pair<Int, Int>>
) {
	// nodes are equivalent if they have the same coords and type
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Path) return false
		if (coordinates != other.coordinates || type != other.type)
			return false
		return true
	}

	override fun hashCode(): Int {
		return Objects.hash(coordinates, type)
	}
}

private data class Intersection(
	val coordinates: Pair<Int, Int>,
	val neighbors: MutableList<Pair<Pair<Int, Int>, Int>>,
	val visited: HashSet<Pair<Int, Int>> = hashSetOf()
) {
	// nodes are equivalent if they have the same coords
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Intersection) return false
		if (coordinates != other.coordinates) return false
		return true
	}

	override fun hashCode(): Int {
		return Objects.hash(coordinates)
	}
}
