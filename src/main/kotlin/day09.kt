import java.io.File

fun main() {
    var sum = 0L
    var historySum = 0L
    File("src/main/resources/day09Input.txt").forEachLine { line ->
        val lists = ArrayList<ArrayList<Long>>()
        val numbers = line.split(" ").map { it.toLong() }
        lists.add(numbers as ArrayList<Long>)

        // Create a new list of the differences until a list is all 0s
        var currentList = numbers
        while (!isAllZeroes(currentList)) {
            val newList = ArrayList<Long>().toMutableList()
            for (i in 1..<currentList.size) {
                newList.add(currentList[i] - currentList[i - 1])
            }
            lists.add(newList as ArrayList<Long>)
            currentList = newList
        }

        // Extrapolate values for each list, forwards and backwards
        for (i in lists.lastIndex - 1 downTo 0) {
            lists[i].add(lists[i].last() + lists[i + 1].last())
            lists[i].add(0, lists[i][0] - lists[i + 1][0])
        }
        sum += numbers.last()
        historySum += numbers[0]
    }
    println("Sum of extrapolated values: %d".format(sum))
    println("Sum of extrapolated history values: %d".format(historySum))
}

fun isAllZeroes(list: List<Long>): Boolean {
    for (number in list) {
        if (number != 0L)
            return false
    }
    return true
}