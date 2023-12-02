import java.io.File

fun main() {
    var sumID = 0
    var sumPower = 0

    File("src/main/resources/day02Input.txt").forEachLine { line ->
        val sessions = line.split("[:;]".toRegex()).toMutableList()
        val gameID = sessions[0].split(" ")[1].toInt()
        // Remove the first element which is "Game #", leaving only sessions
        sessions.removeAt(0)

        var isValid = true
        var minRed = 0
        var minGreen = 0
        var minBlue = 0

        sessions.forEach { session ->
            val pulls = session.split(",")
            pulls.forEach { pull ->
                val info = pull.split(" ")
                // ignore first value, there is a leading space that results in an empty string
                val number = info[1].toInt()
                val color = info[2]

                // Part 1
                if (color == "red" && number > 12)
                    isValid = false
                else if (color == "green" && number > 13)
                    isValid = false
                else if (color == "blue" && number > 14)
                    isValid = false

                // Part 2
                if (color == "red" && number > minRed)
                    minRed = number
                else if (color == "green" && number > minGreen)
                    minGreen = number
                else if (color == "blue" && number > minBlue)
                    minBlue = number
            }
        }
        if (isValid)
            sumID += gameID
        sumPower += minRed * minGreen * minBlue
    }
    println("Sum of the game IDs: %d".format(sumID))
    println("Sum of the powers of sets: %d".format(sumPower))
}