import java.io.File

fun main() {
    val contents = File("resources/day13Input.txt").readText()
    val sections = contents.split("\n\n")
    var sum = 0
    var newSum = 0

    for (section in sections) {
        val rows = section.split("\n")
        val columns = ArrayList<String>()

        // Get Columns
        for (i in 0..<rows[0].length) {
            var column = ""
            for (row in rows) {
                column += row[i]
            }
            columns.add(column)
        }

        // Check horizontal lines of symmetry
        for (i in 1..<rows.size) {
            if (isReflection(i, rows))
                sum += i * 100
            if (findOffByOneReflection(i, rows))
                newSum += i * 100
        }

        // Check vertical lines of symmetry
        for (i in 1..<columns.size) {
            if (isReflection(i, columns))
                sum += i
            if (findOffByOneReflection(i, columns))
                newSum += i
        }
    }

    println("Sum of the notes: %d".format(sum))
    println("Sum after repairing smudges: %d".format(newSum))
}

private fun isReflection(lineOfSymmetryIndex: Int, list: List<String>): Boolean {
    var reflection = true
    for (j in 0..<lineOfSymmetryIndex) {
        // There is no corresponding row/column to check, so ignore it
        if (2 * lineOfSymmetryIndex - 1 - j > list.lastIndex)
            continue
        if (list[j] != list[2 * lineOfSymmetryIndex - 1 - j]) {
            reflection = false
            break
        }
    }
    return reflection
}

// Find a near reflection that is wrong by exactly 1 character, the "smudge"
private fun findOffByOneReflection(lineOfSymmetryIndex: Int, list: List<String>): Boolean {
    var differences = 0
    for (j in 0..<lineOfSymmetryIndex) {
        // There is no corresponding row/column to check, so ignore it
        if (2 * lineOfSymmetryIndex - 1 - j > list.lastIndex)
            continue
        for (i in list[j].indices) {
            if (list[j][i] != list[2 * lineOfSymmetryIndex - 1 - j][i])
                differences++
        }
    }
    return differences == 1
}
