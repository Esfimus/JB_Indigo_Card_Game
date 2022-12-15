package indigo

import java.util.*
import kotlin.Exception
import kotlin.random.Random

/**
 * All standard cards class
 */
enum class Cards(val rank: String, val suit: Char) {
    SPADES_A("A", '♠'),
    SPADES_2("2", '♠'),
    SPADES_3("3", '♠'),
    SPADES_4("4", '♠'),
    SPADES_5("5", '♠'),
    SPADES_6("6", '♠'),
    SPADES_7("7", '♠'),
    SPADES_8("8", '♠'),
    SPADES_9("9", '♠'),
    SPADES_10("10", '♠'),
    SPADES_J("J", '♠'),
    SPADES_Q("Q", '♠'),
    SPADES_K("K", '♠'),

    HEARTS_A("A", '♥'),
    HEARTS_2("2", '♥'),
    HEARTS_3("3", '♥'),
    HEARTS_4("4", '♥'),
    HEARTS_5("5", '♥'),
    HEARTS_6("6", '♥'),
    HEARTS_7("7", '♥'),
    HEARTS_8("8", '♥'),
    HEARTS_9("9", '♥'),
    HEARTS_10("10", '♥'),
    HEARTS_J("J", '♥'),
    HEARTS_Q("Q", '♥'),
    HEARTS_K("K", '♥'),

    DIAMONDS_A("A", '♦'),
    DIAMONDS_2("2", '♦'),
    DIAMONDS_3("3", '♦'),
    DIAMONDS_4("4", '♦'),
    DIAMONDS_5("5", '♦'),
    DIAMONDS_6("6", '♦'),
    DIAMONDS_7("7", '♦'),
    DIAMONDS_8("8", '♦'),
    DIAMONDS_9("9", '♦'),
    DIAMONDS_10("10", '♦'),
    DIAMONDS_J("J", '♦'),
    DIAMONDS_Q("Q", '♦'),
    DIAMONDS_K("K", '♦'),

    CLUBS_A("A", '♣'),
    CLUBS_2("2", '♣'),
    CLUBS_3("3", '♣'),
    CLUBS_4("4", '♣'),
    CLUBS_5("5", '♣'),
    CLUBS_6("6", '♣'),
    CLUBS_7("7", '♣'),
    CLUBS_8("8", '♣'),
    CLUBS_9("9", '♣'),
    CLUBS_10("10", '♣'),
    CLUBS_J("J", '♣'),
    CLUBS_Q("Q", '♣'),
    CLUBS_K("K", '♣');

    override fun toString(): String {
        return "$rank$suit"
    }
}

/**
 * Creates card decks
 */
open class CardDeck {
    val cardsInDeck = mutableListOf<Cards>()

    fun newDeck() {
        cardsInDeck.clear()
        for (enum in Cards.values()) {
            cardsInDeck.add(enum)
        }
        cardsInDeck.shuffle()
    }

    fun displayDeck() {
        for (i in cardsInDeck) {
            print("$i ")
        }
        println()
    }

    fun removeCard(cardNumber: Int) = cardsInDeck.removeAt(cardNumber - 1)

    fun getOneCard(cardNumber: Int) = cardsInDeck[cardNumber - 1]

    fun add(card: Cards) = cardsInDeck.add(card)

    fun size(): Int = cardsInDeck.size

    fun get1Last(): Cards = cardsInDeck[cardsInDeck.size - 1]

    fun get2Last(): Cards = cardsInDeck[cardsInDeck.size - 2]

    fun getFirst(): Cards = cardsInDeck[0]
}

/**
 * Subclass from decks for players' cards only
 */
class PlayerCards(val name: String) : CardDeck() {
    var score = 0
    var lastWon = false
    // displays current cards for the object with the requested formatting
    fun cardsInHand() {
        print("Cards in hand: ")
        for (i in cardsInDeck.indices) {
            print("${i + 1})${cardsInDeck[i].rank}${cardsInDeck[i].suit} ")
        }
        println()
    }
}

var exitCommand = false
val originalDeck = CardDeck()
val bufferDeck = CardDeck()
val playerHuman = PlayerCards("Player")
val playerComputer = PlayerCards("Computer")
val winDeckHuman = CardDeck()
val winDeckComputer = CardDeck()

/**
 * Moves a number of cards from one deck to another
 */
fun passCards(deckFrom: CardDeck, deckTo: CardDeck, cards: Int) {
    if (cards in 1..52) {
        if (cards <= deckFrom.size()) {
            for (i in 1..cards) {
                deckTo.add(deckFrom.getFirst())
                deckFrom.removeCard(1)
            }
        } else {
            println("The remaining cards are insufficient to meet the request.")
        }
    } else {
        throw Exception("Invalid number of cards.")
    }
}

/**
 * Moves one particular card from one deck to another
 * using the number of card, which is index + 1 for the deck structure
 */
fun passOneCard(deckFrom: CardDeck, deckTo: CardDeck, numberOfCard: Int) {
    deckTo.add(deckFrom.getOneCard(numberOfCard))
    deckFrom.removeCard(numberOfCard)
}

/**
 * Machine move
 */
fun computerMove() {
    if (bufferDeck.size() > 0) {
        println("\n${bufferDeck.size()} cards on the table, and the top card is ${bufferDeck.get1Last()}")
    } else {
        println("\nNo cards on the table")
    }
    // checking the machine's cards and dealing another 6 cards or ending the game
    if (playerComputer.size() == 0 && originalDeck.size() >=6) {
        passCards(originalDeck, playerComputer, 6)
    } else if (playerComputer.size() == 0 && originalDeck.size() == 0) {
        return
    }
    // AI moving
    playerComputer.displayDeck()
    val rankMap = playerComputer.cardsInDeck.map { it.rank }.groupingBy { it }.eachCount().filter { it.value >= 1 }
    val suitMap = playerComputer.cardsInDeck.map { it.suit }.groupingBy { it }.eachCount().filter { it.value >= 1 }
    val candidatesSuit = CardDeck()
    val candidatesRank = CardDeck()
    var selectedCard = Cards.HEARTS_A
    // computer has one card in hands
    if (playerComputer.size() == 1) {
        selectedCard = playerComputer.getOneCard(1)
    // buffer deck (table) is empty
    } else if (bufferDeck.size() == 0) {
        // adding multiple suit candidates
        for ((k, v) in suitMap) {
            if (v > 1) {
                for (c in playerComputer.cardsInDeck) {
                    if (c.suit == k) {
                        candidatesSuit.add(c)
                    }
                }
            }
        }
        // adding multiple rank candidates
        for ((k, v) in rankMap) {
            if (v > 1) {
                for (c in playerComputer.cardsInDeck) {
                    if (c.rank == k) {
                        candidatesRank.add(c)
                    }
                }
            }
        }
        // suits priority > rank priority > random choice
        selectedCard = if (candidatesSuit.size() > 0) {
            candidatesSuit.cardsInDeck[Random.nextInt(0, candidatesSuit.size())]
        } else if (candidatesRank.size() > 0) {
            candidatesRank.cardsInDeck[Random.nextInt(0, candidatesRank.size())]
        } else {
            playerComputer.cardsInDeck[Random.nextInt(0, playerComputer.size())]
        }
    // buffer deck (table) is not empty
    } else if (bufferDeck.size() > 0) {
        var candidatesCount = 0
        // counting main candidates
        for (c in playerComputer.cardsInDeck) {
            if (c.suit == bufferDeck.get1Last().suit) {
                candidatesSuit.add(c)
                candidatesCount++
            }
            if (c.rank == bufferDeck.get1Last().rank) {
                candidatesRank.add(c)
                candidatesCount++
            }
        }
        // no main candidates
        if (candidatesCount == 0) {
            // adding multiple suit candidates
            for ((k, v) in suitMap) {
                if (v > 1) {
                    for (c in playerComputer.cardsInDeck) {
                        if (c.suit == k) {
                            candidatesSuit.add(c)
                        }
                    }
                }
            }
            // adding multiple rank candidates
            for ((k, v) in rankMap) {
                if (v > 1) {
                    for (c in playerComputer.cardsInDeck) {
                        if (c.rank == k) {
                            candidatesRank.add(c)
                        }
                    }
                }
            }
            // suits priority > rank priority > random choice
            selectedCard = if (candidatesSuit.size() > 0) {
                candidatesSuit.cardsInDeck[Random.nextInt(0, candidatesSuit.size())]
            } else if (candidatesRank.size() > 0) {
                candidatesRank.cardsInDeck[Random.nextInt(0, candidatesRank.size())]
            } else {
                playerComputer.cardsInDeck[Random.nextInt(0, playerComputer.size())]
            }
        // one main candidate
        } else if (candidatesCount == 1) {
            if (candidatesSuit.cardsInDeck.isNotEmpty()) {
                selectedCard = candidatesSuit.cardsInDeck[0]
            } else if (candidatesRank.cardsInDeck.isNotEmpty()) {
                selectedCard = candidatesRank.cardsInDeck[0]
            }
        // two main candidates
        } else if (candidatesCount == 2) {
            selectedCard = if (candidatesSuit.size() > 0) {
                candidatesSuit.cardsInDeck[Random.nextInt(0, candidatesSuit.size())]
            } else if (candidatesRank.size() > 0) {
                candidatesRank.cardsInDeck[Random.nextInt(0, candidatesRank.size())]
            } else {
                playerComputer.cardsInDeck[Random.nextInt(0, playerComputer.size())]
            }
        // more than two main candidates
        } else if (candidatesCount > 2) {
            selectedCard = if (candidatesSuit.size() > 1) {
                candidatesSuit.cardsInDeck[Random.nextInt(0, candidatesSuit.size())]
            } else if (candidatesRank.size() > 1) {
                candidatesRank.cardsInDeck[Random.nextInt(0, candidatesRank.size())]
            } else {
                playerComputer.cardsInDeck[Random.nextInt(0, playerComputer.size())]
            }
        }
    }
    println("Computer plays $selectedCard")
    passOneCard(playerComputer, bufferDeck, playerComputer.cardsInDeck.indexOf(selectedCard) + 1)
}

/**
 * Player move
 */
fun userMove() {
    if (bufferDeck.size() > 0) {
        println("\n${bufferDeck.size()} cards on the table, and the top card is ${bufferDeck.get1Last()}")
    } else {
        println("\nNo cards on the table")
    }
    // checking the player's cards and dealing another 6 cards or ending the game
    if (playerHuman.size() == 0 && originalDeck.size() >=6) {
        passCards(originalDeck, playerHuman, 6)
    } else if (playerHuman.size() == 0 && originalDeck.size() == 0) {
        return
    }
    // displaying player's cards
    playerHuman.cardsInHand()
    do {
        try {
            // selecting the card for a move or exiting the game
            println("Choose a card to play (1-${playerHuman.size()}):")
            val userInput = readln()
            if (userInput == "exit") {
                exitCommand = true
                return
            }
            val inputNumber = userInput.toInt()
            if (inputNumber in 1..playerHuman.size()) {
                passOneCard(playerHuman, bufferDeck, inputNumber)
                return
            }
        } catch (_: Exception) {
        }
    } while (true)
}

/**
 * Checks for win conditions and displays the result
 */
fun winState(player1: PlayerCards, player2: PlayerCards, winDeck: CardDeck) {
    // final win condition when all left in buffer cards move to the player who won last
    if (bufferDeck.size() > 0 &&
        originalDeck.size() == 0 &&
        playerHuman.size() == 0 &&
        playerComputer.size() == 0
    ) {
        // counting and moving last points to the player who won last
        for (card in bufferDeck.cardsInDeck) {
            if (card.rank == "A" ||
                card.rank == "K" ||
                card.rank == "Q" ||
                card.rank == "J" ||
                card.rank == "10"
            ) {
                if (playerHuman.lastWon) {
                    playerHuman.score++
                } else {
                    playerComputer.score++
                }
            }
        }
        println("\n${bufferDeck.size()} cards on the table, and the top card is ${bufferDeck.get1Last()}")
        if (playerHuman.lastWon) {
            passCards(bufferDeck, winDeckHuman, bufferDeck.size())
        } else {
            passCards(bufferDeck, winDeckComputer, bufferDeck.size())
        }
        // counting and moving last 3 points to the player who has more cards
        if (winDeckHuman.size() >= winDeckComputer.size()) {
            playerHuman.score += 3
        } else {
            playerComputer.score += 3
        }
        println("Score: Player ${playerHuman.score} - Computer ${playerComputer.score}")
        println("Cards: Player ${winDeckHuman.size()} - Computer ${winDeckComputer.size()}")
        exitCommand = true
        return
    // regular win condition while moving the winning card
    } else if (bufferDeck.size() >= 2 &&
        (bufferDeck.get2Last().rank == bufferDeck.get1Last().rank ||
        bufferDeck.get2Last().suit == bufferDeck.get1Last().suit)
    ) {
        // counting and moving points to the winner
        for (card in bufferDeck.cardsInDeck) {
            if (card.rank == "A" ||
                card.rank == "K" ||
                card.rank == "Q" ||
                card.rank == "J" ||
                card.rank == "10"
            ) {
                player1.score++
            }
        }
        // marking the player who just won
        player1.lastWon = true
        player2.lastWon = false
        passCards(bufferDeck, winDeck, bufferDeck.size())
        println("${player1.name} wins cards")
        println("Score: Player ${playerHuman.score} - Computer ${playerComputer.score}")
        println("Cards: Player ${winDeckHuman.size()} - Computer ${winDeckComputer.size()}")
    }
}

/**
 * Cycles the moves of both players depending on who moves first
 */
fun playersMoves(humanFirst: Boolean) {
    print("Initial cards on the table: ")
    bufferDeck.displayDeck()
    // defining who moves first and cycling through all possible moves in the game
    if (!humanFirst) {
        do {
            computerMove()
            winState(playerComputer, playerHuman, winDeckComputer)
            if (exitCommand) {
                return
            }
            userMove()
            winState(playerHuman, playerComputer, winDeckHuman)
        } while(!exitCommand)
    } else {
        do {
            userMove()
            winState(playerHuman, playerComputer, winDeckHuman)
            if (exitCommand) {
                return
            }
            computerMove()
            winState(playerComputer, playerHuman, winDeckComputer)
        } while(!exitCommand)
    }
}

/**
 * Card game with menu
 */
fun indigoGame() {
    println("Indigo Card Game")
    originalDeck.newDeck()
    passCards(originalDeck, bufferDeck, 4)
    passCards(originalDeck, playerHuman, 6)
    passCards(originalDeck, playerComputer, 6)
    // first move selection
    do {
        println("Play first?")
        when (readln().lowercase(Locale.getDefault())) {
            "yes" -> playersMoves(true)
            "no" -> playersMoves(false)
            "exit" -> exitCommand = true
        }
    } while (!exitCommand)
    println("Game Over")
}

fun main() {
    indigoGame()
}