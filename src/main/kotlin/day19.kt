import java.io.File

var ratingSum = 0
var possibleAccepts = 0L
var rejects = 0L

fun main() {
    val parts = ArrayList<Part>()
    val workflows = HashMap<String, Workflow>()

    val file = File("src/main/resources/day19Input.txt").readText()
    val workflowsList = file.substringBefore("\n\n").split("\n")
    val partsList = file.substringAfter("\n\n").split("\n")

    parseWorkflows(workflowsList, workflows)
    parseParts(partsList, parts)

    for (part in parts) {
        // process workflows, starting with in
        processWorkflow("in", part, workflows)
    }
    println("Sum of the rating numbers: %d".format(ratingSum))

    countPossibilities("in", 1..4000, 1..4000, 1..4000, 1..4000, workflows)
    println("Number of acceptable combinations: %d".format(possibleAccepts))
}

data class Part(val x: Int, val m: Int, val a: Int, val s: Int)

data class Workflow(val rules: List<Rule>, val finalRule: String)

data class Rule(val category: Char, val operation: Char, val number: Int, val result: String)

private fun parseParts(partsList: List<String>, parts: ArrayList<Part>) {
    for (part in partsList) {
        // Remove braces from edges
        val stripped = part.substring(1, part.lastIndex)
        val categories = stripped.split(",")
        val x = categories[0].substring(2).toInt()
        val m = categories[1].substring(2).toInt()
        val a = categories[2].substring(2).toInt()
        val s = categories[3].substring(2).toInt()
        parts.add(Part(x, m, a, s))
    }
}

private fun parseWorkflows(workflowsList: List<String>, workflows: HashMap<String, Workflow>) {
    for (workflow in workflowsList) {
        val rulesList = ArrayList<Rule>()
        val separateName = workflow.split("{", "}")
        val name = separateName[0]
        val rules = separateName[1].split(",")
        for (i in 0..<rules.lastIndex) {
            val category = rules[i][0]
            val operation = rules[i][1]
            val numberAndResult = rules[i].substring(2).split(":")
            val number = numberAndResult[0].toInt()
            val result = numberAndResult[1]
            rulesList.add(Rule(category, operation, number, result))
        }
        // Last condition has no check, just add it
        workflows[name] = Workflow(rulesList, rules[rules.lastIndex])
    }
}

private fun processWorkflow(workflowName: String, part: Part, workflows: HashMap<String, Workflow>) {
    // Base cases
    if (workflowName == "A") {
        ratingSum += part.x + part.m + part.a + part.s
        return
    }
    if (workflowName == "R")
        return

    val workflow = workflows[workflowName]!!
    for (rule in workflow.rules) {
        // Get the relevant category
        val category = when (rule.category) {
            'x' -> part.x
            'm' -> part.m
            'a' -> part.a
            's' -> part.s
            else -> 0
        }
        when (rule.operation) {
            '>' -> {
                if (category > rule.number) {
                    processWorkflow(rule.result, part, workflows)
                    return
                }
            }
            '<' -> {
                if (category < rule.number) {
                    processWorkflow(rule.result, part, workflows)
                    return
                }
            }
        }
    }
    // Made it through all the rules, so do the default
    processWorkflow(workflow.finalRule, part, workflows)
}

private fun countPossibilities(workflowName: String, xRange: IntRange, mRange: IntRange, aRange: IntRange, sRange: IntRange,
                               workflows: HashMap<String, Workflow>) {
    // Base cases
    if (workflowName == "A") {
        possibleAccepts += (xRange.toList().size.toLong()) * mRange.toList().size.toLong() * aRange.toList().size.toLong() * sRange.toList().size.toLong()
        return
    }
    if (workflowName == "R") {
        rejects += (xRange.toList().size.toLong()) * mRange.toList().size.toLong() * aRange.toList().size.toLong() * sRange.toList().size.toLong()
        return
    }

    val workflow = workflows[workflowName]!!
    var x = xRange
    var m = mRange
    var a = aRange
    var s = sRange
    for (rule in workflow.rules) {
        // Get the relevant category
        val range = when (rule.category) {
            'x' -> x
            'm' -> m
            'a' -> a
            's' -> s
            else -> 0..0
        }

        when (rule.operation) {
            '>' -> {
                val rangeOne = range.first..rule.number
                val rangeTwo = rule.number + 1..range.last
                when (rule.category) {
                    'x' -> x = rangeTwo
                    'm' -> m = rangeTwo
                    'a' -> a = rangeTwo
                    's' -> s = rangeTwo
                }
                countPossibilities(rule.result, x, m, a, s, workflows)
                when (rule.category) {
                    'x' -> x = rangeOne
                    'm' -> m = rangeOne
                    'a' -> a = rangeOne
                    's' -> s = rangeOne
                }
            }
            '<' -> {
                val rangeOne = range.first..<rule.number
                val rangeTwo = rule.number..range.last
                when (rule.category) {
                    'x' -> x = rangeOne
                    'm' -> m = rangeOne
                    'a' -> a = rangeOne
                    's' -> s = rangeOne
                }
                countPossibilities(rule.result, x, m, a, s, workflows)
                when (rule.category) {
                    'x' -> x = rangeTwo
                    'm' -> m = rangeTwo
                    'a' -> a = rangeTwo
                    's' -> s = rangeTwo
                }
            }
        }
    }
    // Made it through all the rules, so do the default
    countPossibilities(workflow.finalRule, x, m, a, s, workflows)
    return
}