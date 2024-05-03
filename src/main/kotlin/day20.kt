import java.io.File

var lowPulses: Int = 0
var highPulses: Int = 0
val queue: ArrayDeque<Triple<String, Module, Pulse>> = ArrayDeque()

fun main() {
    // construct modules
    var modules: HashMap<String, Module> = constructModules()
    part1(modules)
    println("Part 1 result: " + lowPulses * highPulses)

    // reset state for part 2
    modules = constructModules()
    println("Part 2 button presses: " + part2(modules))
}

private fun constructModules(): HashMap<String, Module> {
    val modules: HashMap<String, Module> = HashMap()
    val lines = File("src/main/resources/day20Input.txt").readLines()
    lines.forEach { line ->
        val parts = line.split(" -> ")
        val destinations = parts[1].split(", ")

        if (parts[0] == "broadcaster")
            modules[parts[0]] = Module(parts[0], ModuleType.Broadcaster, destinations = destinations)
        else {
            val name = parts[0].substring(1)
            val type = if (parts[0][0] == '%') ModuleType.Flip else ModuleType.Conjunction
            if (type == ModuleType.Conjunction) {
                val lastPulses: HashMap<String, Pulse> = HashMap()
                modules[name] = Module(name, type, lastPulses = lastPulses, destinations = destinations)
            } else
                modules[name] = Module(name, type, destinations = destinations)
        }
    }

    // set conjunction inputs to default to low
    for (module in modules.values) {
        for (destination in module.destinations) {
            if (modules[destination]?.type == ModuleType.Conjunction)
                modules[destination]?.lastPulses?.set(module.name, Pulse.Low)
        }
    }
    return modules
}

private fun part1(modules: HashMap<String, Module>) {
    for (i in 0..<1000) {
        // add the initial button press pulse
        queue.add(Triple("broadcaster", modules["broadcaster"]!!, Pulse.Low))
        lowPulses++

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            modules[current.first]?.processPulse(current.second, current.third)
        }
    }
}

private fun part2(modules: HashMap<String, Module>): Long {
    var buttonPresses: Long = 1
    var period1: Long? = null
    var period2: Long? = null
    var period3: Long? = null
    var period4: Long? = null
    while(period1 == null || period2 == null || period3 == null || period4 == null) {
        // add the initial button press pulse
        queue.add(Triple("broadcaster", modules["broadcaster"]!!, Pulse.Low))
        lowPulses++

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current.first == "tx" && current.third == Pulse.Low && period1 == null)
                period1 = buttonPresses
            else if (current.first == "dd" && current.third == Pulse.Low && period2 == null)
                period2 = buttonPresses
            else if (current.first == "nz" && current.third == Pulse.Low && period3 == null)
                period3 = buttonPresses
            else if (current.first == "ph" && current.third == Pulse.Low && period4 == null)
                period4 = buttonPresses
            modules[current.first]?.processPulse(current.second, current.third)
        }
        buttonPresses++
    }
    return findLCM(findLCM(period1, period2), findLCM(period3, period4))
}

class Module(
    val name: String,
    val type: ModuleType,
    var isOn: Boolean = false,
    var lastPulses: HashMap<String, Pulse> = HashMap(),
    val destinations: List<String>
) {
    fun processPulse(fromModule: Module, pulse: Pulse) {
        when (type) {
            ModuleType.Flip -> {
                if (pulse == Pulse.Low) {
                    isOn = !isOn

                    if (isOn) {
                        for (destination in destinations) {
                            queue.add(Triple(destination, this, Pulse.High))
                            highPulses++
                        }
                    } else {
                        for (destination in destinations) {
                            queue.add(Triple(destination, this, Pulse.Low))
                            lowPulses++
                        }
                    }
                }
            }
            ModuleType.Conjunction -> {
                // update from this pulse
                lastPulses[fromModule.name] = pulse

                if (lastPulses.all { it.value == Pulse.High }) {
                    for (destination in destinations) {
                        queue.add(Triple(destination, this, Pulse.Low))
                        lowPulses++
                    }
                }
                else {
                    for (destination in destinations) {
                        queue.add(Triple(destination, this, Pulse.High))
                        highPulses++
                    }
                }
            }
            ModuleType.Broadcaster -> {
                for (destination in destinations) {
                    queue.add(Triple(destination, this, pulse))
                    if (pulse == Pulse.High) highPulses++ else lowPulses++
                }
            }
        }
    }

    override fun toString(): String {
        return name
    }
}

enum class ModuleType {
    Flip, Conjunction, Broadcaster
}

enum class Pulse {
    High, Low
}