import java.io.File

fun main() {
    // Initialize a HashMap to store string to char conversions for easy lookups
    val map: HashMap<String, Char> = HashMap()
    map["one"] = '1'
    map["two"] = '2'
    map["three"] = '3'
    map["four"] = '4'
    map["five"] = '5'
    map["six"] = '6'
    map["seven"] = '7'
    map["eight"] = '8'
    map["nine"] = '9'

    var sum: Int = 0
    File("resources/day01Input.txt").forEachLine { line ->
        // Initial values
        var firstDigit: Char? = null
        var firstDigitIndex: Int = -1
        var lastDigit: Char? = null
        var lastDigitIndex: Int = -1

        var count = 0
        line.forEach { character ->
            // Only set firstDigit if none has been found
            if (character.isDigit()) {
                if (firstDigit == null) {
                    firstDigit = character
                    firstDigitIndex = count
                }
                // Update lastDigit every time
                lastDigit = character
                lastDigitIndex = count
            }
            count++
        }

        map.keys.forEach() { key ->
            val firstIndex = line.indexOf(key)
            // lastIndex covers the case that one line has the same number string, ex. "one" more
            // than once
            val lastIndex = line.lastIndexOf(key)

            // Update firstDigit if a numerical string is found and prior to the digit
            if (firstIndex != -1 && (firstDigitIndex == -1 || firstIndex < firstDigitIndex)) {
                firstDigit = map[key]
                firstDigitIndex = firstIndex
            }
            // Update lastDigit if the first occurrence of the numerical string is after the last digit
            if (firstIndex > lastDigitIndex) {
                lastDigit = map[key]
                lastDigitIndex = firstIndex
            }
            // Update lastDigit if the last occurrence of the numerical string is after the last digit
            if (lastIndex > lastDigitIndex) {
                lastDigit = map[key]
                lastDigitIndex = lastIndex
            }
        }

        val finalNumber: Int = "$firstDigit$lastDigit".toInt()
        sum += finalNumber
    }
    println("Final calibration number: %d".format(sum))
}
