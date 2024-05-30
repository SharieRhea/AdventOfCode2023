import java.io.File
var rows =  ArrayList<String>()
var columns = ArrayList<String>()
val memoizationMap = HashMap<String, String>()
fun main() {
    rows = File("resources/day14Input.txt").readLines() as ArrayList<String>
    // Get Columns
    getColumns()

    var key = "$rows$columns"

    tiltNorth()
    println("Total load on the support beams: %d".format(calculateLoad()))

    // Finish the first cycle by completing west, south, east
    getRows()
    tiltWest()
    getColumns()
    tiltSouth()
    getRows()
    tiltEast()
    getColumns()

    // After first cycle, add to the map
    memoizationMap[key] =  "$rows$columns"
    var i = 0
    val cycles = 999999999
    // Complete more cycles
    // Warning: This still takes quite a while even with attempted memoization
    while (i < cycles) {
        // Check to see if this cycle has already been calculated
        var result = memoizationMap["$rows$columns"]
        if (result != null) {
            i++
            // Check if there is a path to follow
            while (memoizationMap[result]  != null && i < cycles) {
                result = memoizationMap[result]
                // account for extra steps taken
                i++
            }
            // Update the map
            memoizationMap["$rows$columns"] = result!!
            // Parse what the grid looks like according the map value
            val contents = result.split("[", "][", "]")
            rows = contents[1].split(", ") as ArrayList<String>
            columns = contents[2].split(", ") as ArrayList<String>
        }
        else {
            key = "$rows$columns"
            tiltNorth()
            getRows()
            tiltWest()
            getColumns()
            tiltSouth()
            getRows()
            tiltEast()
            getColumns()

            memoizationMap[key] = "$rows$columns"
            i++
        }
    }
    println("Total load after 1 billion cycles: %d".format(calculateLoad()))
}

private fun calculateLoad(): Int {
    var totalLoad = 0
    for (columnIndex in columns.indices) {
        for (charIndex in columns[0].indices) {
            if (columns[columnIndex][charIndex] == 'O') {
                totalLoad += columns[columnIndex].length - charIndex
            }
        }
    }
    return totalLoad
}

private fun getColumns() {
    columns = ArrayList()
    for (i in 0..<rows[0].length) {
        var column = ""
        for (row in rows) {
            column += row[i]
        }
        columns.add(column)
    }
}

private fun getRows() {
    rows = ArrayList()
    for (i in 0..<columns[0].length) {
        var row = ""
        for (column in columns) {
            row += column[i]
        }
        rows.add(row)
    }
}

private fun tiltNorth() {
    for (columnIndex in columns.indices) {
        for (charIndex in columns[0].indices) {
            if (columns[columnIndex][charIndex] == 'O')
                columns[columnIndex] = rollRocksNorthOrWest(columns[columnIndex], charIndex)
        }
    }
}

private fun tiltWest() {
    for (rowIndex in rows.indices) {
        for (charIndex in rows[0].indices) {
            if (rows[rowIndex][charIndex] == 'O')
                rows[rowIndex] = rollRocksNorthOrWest(rows[rowIndex], charIndex)
        }
    }
}

private fun tiltSouth() {
    for (columnIndex in columns.indices) {
        for (charIndex in columns[0].indices.reversed()) {
            if (columns[columnIndex][charIndex] == 'O')
                columns[columnIndex] = rollRocksSouthOrEast(columns[columnIndex], charIndex)
        }
    }
}

private fun tiltEast() {
    for (rowIndex in rows.indices) {
        for (charIndex in rows[0].indices.reversed()) {
            if (rows[rowIndex][charIndex] == 'O')
                rows[rowIndex] = rollRocksSouthOrEast(rows[rowIndex], charIndex)
        }
    }
}

fun rollRocksNorthOrWest(column: String, index: Int): String {
    var currentIndex = index
    var returnValue = column
    while (currentIndex != 0 && returnValue[currentIndex - 1] == '.') {
        returnValue = returnValue.replaceRange(currentIndex - 1, currentIndex + 1, "O.")
        currentIndex--
    }
    return returnValue
}

fun rollRocksSouthOrEast(column: String, index: Int): String {
    var currentIndex = index
    var returnValue = column
    while (currentIndex != column.lastIndex && returnValue[currentIndex + 1] == '.') {
        returnValue = returnValue.replaceRange(currentIndex, currentIndex + 2, ".O")
        currentIndex++
    }
    return returnValue
}
