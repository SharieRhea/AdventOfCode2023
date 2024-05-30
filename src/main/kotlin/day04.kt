import java.io.File
import java.util.stream.IntStream.range
import kotlin.math.pow

fun main() {
    var pointsSum = 0
    // Begin with one instance of each scratchoff
    val gamesList = IntArray(204) { 1 }
    var index = 0
    File("resources/day04Input.txt").forEachLine { line ->
        val card = line.split(":( )+".toRegex()).toMutableList()
        // Remove the first element which is "Card #", leaving only card numbers
        card.removeAt(0)

        val numbers = card[0].split(" \\|( )+".toRegex()).toMutableList()
        val winningNumbers = numbers[0].split("( )+".toRegex())
        val yourNumbers = numbers[1].split("( )+".toRegex())

        var numberOfWinningNumbers = 0
        for (number: String in winningNumbers) {
            if (yourNumbers.contains(number))
                numberOfWinningNumbers++
        }
        // Total "points" won is doubled everytime after the first, so -1
        if (numberOfWinningNumbers > 0)
            pointsSum += 2.0.pow(numberOfWinningNumbers.toDouble() - 1).toInt()

        // Increase the number of cards of the following numberOfWinningNumbers games
        // by however many instances of the winning card there are (gamesList[index])
        for (subIndex in range(index + 1, index + 1 + numberOfWinningNumbers)) {
            gamesList[subIndex] += gamesList[index]
        }

        index++
    }

    println("Points sum: %d".format(pointsSum))
    println("Total number of scratchoffs: %d".format(gamesList.sum()))
}
