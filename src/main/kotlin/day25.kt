import java.io.File

fun main() {
	val graph: HashMap<String, HashSet<String>> = HashMap() 
	val lines = File("resources/day25Input.txt").readLines()
	
	// construct undirected graph
	for (line in lines) {
		val node = line.split(": ")[0]
		val connections = line.split(": ")[1].split(" ")
		
		// deal with the forward direction
		if (graph.containsKey(node))
			graph[node]!!.addAll(connections)
		else
			graph[node] = connections.toHashSet()

		// deal with reverse direction
		for (connection in connections) {
			if (graph.containsKey(connection))
				graph[connection]!!.add(node)
			else
				graph[connection] = hashSetOf(node)
		}
	}
	var part1SolutionFound = false

	while (!part1SolutionFound) {
		// Karger's algorithm to find min cut
		val workingGraph: HashMap<String, MutableList<String>> = HashMap()
		for (key in graph.keys) {
			workingGraph[key] = graph[key]!!.toMutableList()
		}

		while (workingGraph.keys.size > 2) {
			val node1: String = workingGraph.keys.shuffled().take(1)[0]
			val node2: String = workingGraph[node1]!!.shuffled().take(1)[0]
			// println("contracting ${node1} and ${node2}")
			// println(workingGraph)
			contract(node1, node2, workingGraph)
		}
		if (workingGraph[workingGraph.keys.take(1)[0]]!!.size == 3) {
			println("Group sizes multiplied: " + (workingGraph.keys.take(2)[0].length / 3) * (workingGraph.keys.take(2)[1].length / 3))
			part1SolutionFound = true
		}
	}
}

private fun contract(node1: String, node2: String, graph: HashMap<String, MutableList<String>>) {
	val newNode = node1 + node2
	val newConnections = graph[node1]!!
	newConnections.addAll(graph[node2]!!)
	newConnections.removeAll(listOf(node1, node2))
	
	// remove two contracting nodes
	graph.remove(node1)
	graph.remove(node2)
	
	// add new contracted node
	graph[newNode] = newConnections

	// update connections to new node
	for (key in graph.keys) {
		while (graph[key]!!.remove(node1) && key != newNode) {
			graph[key]!!.add(newNode)
		}
		while (graph[key]!!.remove(node2) && key != newNode) {
			graph[key]!!.add(newNode)
		}
	}
}

/*
 * Intuition: Karger's algorithm, for another day 
 * 2847978 too high
 * 843453 too high
 * 2942 too low
 * 67788 incorrect
 * 171220
 */
