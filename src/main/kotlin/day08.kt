import java.io.File

data class Node(val node: String, val path: Pair<String, String>)

fun main() {
    var instructions = ""
    // Store nodes that end with "A" for part 2
    val startNodes = ArrayList<Node>().toMutableList()
    val map = HashMap<String, Node>()
    File("resources/day08Input.txt").forEachLine {line ->
        if (instructions.isEmpty())
            instructions = line
        else if (line.isNotBlank()) {
            val contents = line.split(" = (", ", ", ")")
            map[contents[0]] = Node(contents[0], Pair(contents[1], contents[2]))

            if (contents[0].endsWith("A"))
                startNodes.add(map[contents[0]]!!)
        }
    }

    // Part 1
    var currentNode = map["AAA"]
    var steps = 0
    var index = 0
    // Continuously follow the instructions until reaching the end node
    while (currentNode!!.node != "ZZZ") {
        currentNode = if (instructions[index] == 'L')
            map[currentNode.path.first]!!
        else
            map[currentNode.path.second]!!
        steps++
        index++
        // Ran out of instructions, start from beginning
        if (index >= instructions.length)
            index = 0
    }
    println("Number of steps: %d".format(steps))

    // Part 2, determine how many steps each starting node takes to reach a
    // valid destination node
    val stepsList = ArrayList<Long>().toMutableList()
    for (node in startNodes) {
        currentNode = node
        steps = 0
        index = 0
        while (!currentNode!!.node.endsWith("Z")) {
            currentNode = if (instructions[index] == 'L')
                map[currentNode.path.first]!!
            else
                map[currentNode.path.second]!!
            steps++
            index++
            if (index >= instructions.length)
                index = 0
        }
        stepsList.add(steps.toLong())
    }

    // Find the least common multiple of the different paths
    for (i in 0..<(stepsList.size - 1)) {
        stepsList[i + 1] = findLCM(stepsList[i], stepsList[i + 1])
    }
    println("Number of ghost steps: %d".format(stepsList[stepsList.size - 1]))
}

fun findLCM(number1: Long, number2: Long): Long {
    return (number1 / findGCD(number1, number2)) * number2
}

fun findGCD(number1: Long, number2: Long): Long {
    if (number1 == 0L)
        return number2
    return findGCD(number2 % number1, number1)
}
