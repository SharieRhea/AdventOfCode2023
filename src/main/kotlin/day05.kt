import java.io.File

var seedToSoil = HashMap<Pair<Long, Long>, Long>()
var soilToFertilizer = HashMap<Pair<Long, Long>, Long>()
var fertilizerToWater = HashMap<Pair<Long, Long>, Long>()
var waterToLight = HashMap<Pair<Long, Long>, Long>()
var lightToTemperature = HashMap<Pair<Long, Long>, Long>()
var temperatureToHumidity = HashMap<Pair<Long, Long>, Long>()
var humidityToLocation = HashMap<Pair<Long, Long>, Long>()

fun main() {
    val contents: String = File("src/main/resources/day05Input.txt").readText()
    // Split into the different sections of the file
    val chunks = contents.split("\n\n")
    val seeds = chunks[0].split(" ").toMutableList()
    // Remove "Seeds:"
    seeds.removeAt(0)
    val seedNumbers = seeds.map { it.toLong() }.toMutableList()
    val newSeedNumbers = seedNumbers.toMutableList()

    // Prepare the ranges for each conversion, with destination start as the map's value
    seedToSoil = getRange(chunks[1])
    soilToFertilizer = getRange(chunks[2])
    fertilizerToWater = getRange(chunks[3])
    waterToLight = getRange(chunks[4])
    lightToTemperature = getRange(chunks[5])
    temperatureToHumidity = getRange(chunks[6])
    humidityToLocation = getRange(chunks[7])

    // For each seed number, map for each step
    mapToNewValue(seedNumbers, seedToSoil)
    mapToNewValue(seedNumbers, soilToFertilizer)
    mapToNewValue(seedNumbers, fertilizerToWater)
    mapToNewValue(seedNumbers, waterToLight)
    mapToNewValue(seedNumbers, lightToTemperature)
    mapToNewValue(seedNumbers, temperatureToHumidity)
    mapToNewValue(seedNumbers, humidityToLocation)

    // Note: This will take a VERY long time to run, and it only checks one range
    // The range was found by checking the minimum of all the ranges at steps of 1000 numbers
    val minimum = getMinimumOfRange(newSeedNumbers[14], newSeedNumbers[14] + newSeedNumbers[15])

    println("Minimum location number: %d".format(seedNumbers.min()))
    println("Minimum location number of new seeds: %d".format(minimum))
}

fun getRange(string: String): HashMap<Pair<Long, Long>, Long> {
    val map = HashMap<Pair<Long, Long>, Long>()
    val lines = string.split("\n").toMutableList()
    // Remove the title of the section
    lines.removeAt(0)
    lines.forEach { line ->
        val numbers: List<Long> = line.split(" ").map { it.toLong() }
        // map[source start, source end] = destination start
        map[Pair(numbers[1], numbers[1] + numbers[2] - 1)] = numbers[0]
    }
    return map
}

fun mapToNewValue(list: MutableList<Long>, map: HashMap<Pair<Long, Long>, Long>) {
    for (i in list.indices) {
        // Flag is used to ensure that a mapped value does not get "remapped"
        var mapped = false
        map.forEach {entry ->
            if (!mapped && list[i] >= entry.key.first && list[i] <= entry.key.second) {
                // number = distance from source start + destination start
                list[i] = (list[i] - entry.key.first) + entry.value
                mapped = true
            }
        }
    }
}

fun mapToNewValue(number: Long, map: HashMap<Pair<Long, Long>, Long>): Long {
        map.forEach {entry ->
            if (number >= entry.key.first && number <= entry.key.second)
                // number = distance from source start + destination start
                return (number - entry.key.first) + entry.value
        }
    return number
}

fun mapSeedToLocation(number: Long): Long {
    var returnVal = number
    returnVal = mapToNewValue(returnVal, seedToSoil)
    returnVal = mapToNewValue(returnVal, soilToFertilizer)
    returnVal = mapToNewValue(returnVal, fertilizerToWater)
    returnVal = mapToNewValue(returnVal, waterToLight)
    returnVal = mapToNewValue(returnVal, lightToTemperature)
    returnVal = mapToNewValue(returnVal, temperatureToHumidity)
    returnVal = mapToNewValue(returnVal, humidityToLocation)
    return returnVal
}

fun getMinimumOfRange(start: Long, end: Long): Long {
    var minOfRange = Long.MAX_VALUE
    // Added "step 1000" to this loop to find the range that held the minimum number
    for (j in start..<end) {
        val number = mapSeedToLocation(j)
        if (number < minOfRange)
            minOfRange = number
    }
    return minOfRange
}