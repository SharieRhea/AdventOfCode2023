import java.io.File
import kotlin.math.pow

fun main() {
    var pointsSum = 0
    File("src/main/resources/day04Input.txt").forEachLine { line ->
        val card = line.split(":( )+".toRegex()).toMutableList()
        // Remove the first element which is "Card #", leaving only card numbers
        card.removeAt(0)

        val numbers = card[0].split(" \\|( )+".toRegex()).toMutableList()
        val winningNumbers = numbers[0].split("( )+".toRegex())
        val yourNumbers = numbers[1].split("( )+".toRegex())

        var numberOfWinningNumbers = 0
        for (number: String in winningNumbers) {
            if (yourNumbers.contains(number)) {
                numberOfWinningNumbers++
            }
        }
        if (numberOfWinningNumbers > 0)
            pointsSum += 2.0.pow(numberOfWinningNumbers.toDouble() - 1).toInt()
    }

    println("Points sum: %d".format(pointsSum))
}