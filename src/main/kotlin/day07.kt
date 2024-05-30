import java.io.File

val cardMap = HashMap<Char, Int>()

data class Game(val hand: String, var type: HandType, val bid: Int)

enum class HandType {
    FiveOfAKind, FourOfAKind, FullHouse, ThreeOfAKind, TwoPair, OnePair, HighCard
}

fun main() {
    cardMap['T'] = 10
    cardMap['J'] = 11
    cardMap['Q'] = 12
    cardMap['K'] = 13
    cardMap['A'] = 14

    val games = ArrayList<Game>()

    File("resources/day07Input.txt").forEachLine { line ->
        val contents = line.split(" ")
        games.add(Game(contents[0], getHandType(contents[0]), contents[1].toInt()))
    }

    sortGames(games, 0, games.size - 1)
    var totalWinnings = 0
    for (index in 0..<games.size) {
        totalWinnings += games[index].bid * (index + 1)
    }
    println("Total winnings: %d".format(totalWinnings))

    games.forEach {game ->
        handleWildcards(game)
    }
    // Set J to 0 for individual wildcard comparisons
    cardMap['J'] = 0
    sortGames(games, 0, games.size - 1)
    var totalWildcardWinnings = 0
    for (index in 0..<games.size) {
        totalWildcardWinnings += games[index].bid * (index + 1)
    }
    println("Total wildcard winnings: %d".format(totalWildcardWinnings))
}

fun getHandType(hand: String): HandType {
    val numbers = getIntArray(hand)

    // Use set to get the number of distinct cards
    val set = setOf(numbers[0], numbers[1], numbers[2], numbers[3], numbers[4])
    when (set.size) {
        1 -> return HandType.FiveOfAKind
        5 -> return HandType.HighCard
        4 -> return HandType.OnePair
        3 -> {
            // If any one number occurs three times, must be a three of a kind,
            // Otherwise can only be a two pair
            for (number in set) {
                if (numbers.count { it == number } == 3)
                    return HandType.ThreeOfAKind
            }
            return HandType.TwoPair
        }
        else -> {
            // If any one number occurs four times, must be a four of a kind,
            // Otherwise can only be a full house
            for (number in set) {
                if (numbers.count { it == number } == 4)
                    return HandType.FourOfAKind
            }
            return HandType.FullHouse
        }
    }
}

// Convert hand to 5 numerical values capable of comparison
private fun getIntArray(hand: String): IntArray {
    val cards = hand.toCharArray()
    val numbers = IntArray(5)
    for (i in 0..<5) {
        if (cards[i].isLetter())
            // This will never be null unless there is an invalid game (invalid letter)
            numbers[i] = cardMap[cards[i]]!!
        else
            numbers[i] = cards[i].digitToInt()
    }
    return numbers
}

// Comparator for a hand of cards
fun compareHands(game1: Game, game2: Game): Int {
    if (game1.type.ordinal < game2.type.ordinal)
        return 1
    else if (game1.type.ordinal > game2.type.ordinal)
        return -1

    // Must have the same hand type, start comparing cards individually
    val numbers1 = getIntArray(game1.hand)
    val numbers2 = getIntArray(game2.hand)

    for (i in 0..<5) {
        if (numbers1[i] < numbers2[i])
            return -1
        else if (numbers1[i] > numbers2[i])
            return 1
    }

    // Hands are exactly the same
    return 0
}

// Quicksort for sorting games
fun sortGames(list: ArrayList<Game>, low: Int, high: Int) {
    if (low < high) {
        val partition = partition(list, low, high)

        sortGames(list, low, partition -1)
        sortGames(list, partition + 1, high)
    }
}

// Partition used for quicksort
fun partition(list: ArrayList<Game>, low: Int, high: Int): Int {
    val pivot = list[high]
    var i = low - 1
    for (j in low..<high) {
        if (compareHands(list[j], pivot) == -1) {
            i++
            val temp = list[i]
            list[i] = list[j]
            list[j] = temp
        }
    }
    val temp = list[i + 1]
    list[i + 1] = list[high]
    list[high] = temp
    return i + 1
}

// Increase a cards hand type if it has one or more wildcards. Some combinations are not
// possible, so just ignore them
fun handleWildcards(game: Game) {
    val cards = game.hand.toCharArray()
    if (!cards.contains('J'))
        return

    val numberOfWildcards = cards.count { it == 'J' }
    when (numberOfWildcards) {
        1 -> {
            when (game.type) {
                HandType.HighCard -> game.type = HandType.OnePair
                HandType.OnePair -> game.type = HandType.ThreeOfAKind
                HandType.TwoPair -> game.type = HandType.FullHouse
                HandType.ThreeOfAKind -> game.type = HandType.FourOfAKind
                HandType.FourOfAKind -> game.type = HandType.FiveOfAKind
                else -> {}
            }
        }
        2 -> {
            when (game.type) {
                HandType.OnePair -> game.type = HandType.ThreeOfAKind
                HandType.TwoPair -> game.type = HandType.FourOfAKind
                HandType.FullHouse -> game.type = HandType.FiveOfAKind
                else -> {}
            }
        }
        3 -> {
            when (game.type) {
                HandType.ThreeOfAKind -> game.type = HandType.FourOfAKind
                HandType.FullHouse -> game.type = HandType.FiveOfAKind
                else -> {}
            }
        }
        4 -> {
            when (game.type) {
                HandType.FourOfAKind -> game.type = HandType.FiveOfAKind
                else -> {}
            }
        }
    }
}
