import java.io.File

// A Lens object is unique only according to its label, so override equals
data class Lens(val label: String, val focalLength: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is Lens)
            return false

        if (label == other.label)
            return true

        return false
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }
}

fun main() {
    val input = File("src/main/resources/day15Input.txt").readText()
    val steps = input.split(",")
    var sum = 0
    for (step in steps) {
        sum += hash(step)
    }
    println("Sum of the results: %d".format(sum))

    val boxes = Array<ArrayList<Lens>>(256) { ArrayList() }
    for (step in steps) {
        val minus = step.indexOf("-")
        val equals = step.indexOf("=")
        if (minus != -1) {
            val label = step.substringBefore("-")
            val box = boxes[hash(label)]
            // Focal length is not relevant here, set to 0
            val lens = Lens(label, 0)
            box.remove(lens)
        }
        else if (equals != -1) {
            val label = step.substringBefore("=")
            val lens = Lens(label, step.substringAfter("=").toInt())
            val box = boxes[hash(label)]
            if (box.contains(lens))
                box[box.indexOf(lens)] = lens
            else
                box.add(lens)
        }
    }

    var focusingPower = 0
    for (boxIndex in boxes.indices) {
        for (lensIndex in boxes[boxIndex].indices) {
            focusingPower += (boxIndex + 1) * (lensIndex + 1) * boxes[boxIndex][lensIndex].focalLength
        }
    }

    println("Total focusing power: %d".format(focusingPower))

}

// Return a number from 0-255 according to the hashing algorithm
private fun hash(string: String): Int {
    var currentValue = 0
    for (char in string) {
        currentValue += char.code
        currentValue *= 17
        currentValue %= 256
    }
    return currentValue
}