import java.io.File

var sum = 0
var partTwoSum = 0L
val map = HashMap<String, Long>()

fun main() {
    File("src/main/resources/day12Input.txt").forEachLine { line ->
        val contents = line.split(" ")
        val numbers = contents[1].split(",").map { it.toInt() }
        walk(contents[0], numbers)

        val expandedContents = contents[0] + "?" + contents[0] + "?" + contents[0] + "?" + contents[0] + "?" + contents[0]
        val expandedNumbers = listOf(numbers, numbers, numbers, numbers, numbers).flatten()
        walkMemo(expandedContents, expandedNumbers)
    }

    println("Number of ways: %d".format(sum))
    println("Number of ways after unfolding: %d".format(partTwoSum))
}

fun walk(string: String, numbers: List<Int>) {
    // base case
    if (string.isEmpty() && numbers.isEmpty()) {
        sum++
        return
    }
    if (string.isEmpty())
        return

    val char = string[0]
    when (char) {
        '.' -> walk(string.removeRange(0, 1), numbers)
        '?' -> {
            // Treat as a .
            walk(string.removeRange(0, 1), numbers)
            // Treat as a #
            walk(string.replaceFirstChar { '#' }, numbers)
        }
        '#' -> {
            // Invalid
            if (numbers.isEmpty())
                return

            val number = numbers[0]
            // Invalid
            if (string.length < number)
                return

            for (i in 0..<number) {
                // Invalid
                if (string[i] == '.')
                    return
                // Replace first ? with a #, check that
                else if (string[i] == '?') {
                    walk(string.replaceRange(i, i + 1, "#"), numbers)
                    return
                }
            }

            // Contiguous matching group found, remove it and its number, then walk
            if (string.length == number)
                walk(string.removeRange(0, number), numbers.drop(1))
            else if (string[number] == '.' || string[number] == '?')
                walk(string.removeRange(0, number + 1), numbers.drop(1))
        }
    }
}

fun walkMemo(string: String, numbers: List<Int>): Long {
    // Check if this input has already been calculated
    if (map[string + numbers] != null) {
        partTwoSum += map[string + numbers]!!
        return map[string + numbers]!!
    }

    // base case, propagate upwards
    if (string.isEmpty() && numbers.isEmpty()) {
        partTwoSum++
        return 1
    }
    if (string.isEmpty())
        return 0

    // This input has not been calculated yet, keep track of possibilities
    var possibilities = 0L

    val char = string[0]
    when (char) {
        '.' -> possibilities += walkMemo(string.removeRange(0, 1), numbers)
        '?' -> {
            // Treat as a .
            possibilities += walkMemo(string.removeRange(0, 1), numbers)
            // Treat as a #
            possibilities += walkMemo(string.replaceFirstChar { '#' }, numbers)
        }
        '#' -> {
            // Invalid
            if (numbers.isEmpty())
                return 0

            val number = numbers[0]
            //Invalid
            if (string.length < number)
                return 0

            for (i in 0..<number) {
                // Invalid
                if (string[i] == '.')
                    return 0
                else if (string[i] == '?') {
                    // Do not add to possibilities here, because we are altering the string past the first character
                    // so treat as a separate branch
                    return walkMemo(string.replaceRange(i, i + 1, "#"), numbers)
                }
            }

            // Contiguous matching group found, remove it and its number, then walk
            if (string.length == number)
                possibilities += walkMemo(string.removeRange(0, number), numbers.drop(1))
            else if (string[number] == '.' || string[number] == '?')
                possibilities += walkMemo(string.removeRange(0, number + 1), numbers.drop(1))
        }
    }

    // All possibilities for this input have been added, add it to the map
    map[string + numbers] = possibilities
    return possibilities
}
