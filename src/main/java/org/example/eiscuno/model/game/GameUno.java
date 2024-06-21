package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.alertbox.AlertBox;

/**
 * Represents a game of Uno.
 * This class manages the game logic and interactions between players, deck, and the table.
 */
public class GameUno implements IGameUno {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    public AlertBox alertBox = new AlertBox();

    /**
     * Constructs a new GameUno instance.
     *
     * @param humanPlayer   The human player participating in the game.
     * @param machinePlayer The machine player participating in the game.
     * @param deck          The deck of cards used in the game.
     * @param table         The table where cards are placed during the game.
     */
    public GameUno(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
    }

    /**
     * Starts the Uno game by distributing cards to players.
     * The human player and the machine player each receive 10 cards from the deck.
     */
    @Override
    public void startGame() {
        for (int i = 0; i < 10; i++) {
            humanPlayer.addCard(this.deck.takeCard());
            machinePlayer.addCard(this.deck.takeCard());
            Card currentCard = deck.takeCard();
            deck.discardCard(currentCard);
        }
    }

    /**
     * Allows a player to draw a specified number of cards from the deck.
     *
     * @param player        The player who will draw cards.
     * @param numberOfCards The number of cards to draw.
     */
    @Override
    public void eatCard(Player player, int numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            player.addCard(this.deck.takeCard());
        }
    }

    /**
     * Places a card on the table during the game.
     *
     * @param card The card to be placed on the table.
     */
    @Override
    public void playCard(Card card) {
        this.table.addCardOnTheTable(card);
        String playerType = humanPlayer.getTypePlayer();
        String playerMachine = machinePlayer.getTypePlayer();
        // Agregar la carta a la mesa
        this.table.addCardOnTheTable(card);
        // Realizar acciones posteriores al movimiento
        postMoveActions(playerType);
        postMoveActions(playerMachine);
    }

    /**
     * Handles the scenario when a player shouts "Uno", forcing the other player to draw a card.
     *
     * @param playerWhoSang The player who shouted "Uno".
     */
    @Override
    public void haveSingOne(String playerWhoSang) {
        if (playerWhoSang.equals("HUMAN_PLAYER")) {
            machinePlayer.addCard(this.deck.takeCard());
        } else {
            humanPlayer.addCard(this.deck.takeCard());
            humanPlayer.printCardsPlayer();
        }
    }

    /**
     * Retrieves the current visible cards of the human player starting from a specific position.
     *
     * @param posInitCardToShow The initial position of the cards to show.
     * @return An array of cards visible to the human player.
     */
    @Override
    public Card[] getCurrentVisibleCardsHumanPlayer(int posInitCardToShow) {
        int totalCards = this.humanPlayer.getCardsPlayer().size();
        int numVisibleCards = Math.min(4, totalCards - posInitCardToShow);
        Card[] cards = new Card[numVisibleCards];

        for (int i = 0; i < numVisibleCards; i++) {
            cards[i] = this.humanPlayer.getCard(posInitCardToShow + i);
        }

        return cards;
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the deck is empty, indicating the game is over; otherwise, false.
     */
    @Override
    public Boolean isGameOver() {
        GameUnoStage.deleteInstance();
        ThreadPlayMachine.currentThread().interrupt();
        return true;
    }
    public boolean canPlayCard(Card card) {
        Card topCard = table.getTopCard();
        return card.getColor().equals(topCard.getColor()) ||
                card.getValue().equals(topCard.getValue()) ||
                card.isWildCard();
    }

    public void isWildCards(Card card, ThreadPlayMachine threadPlayMachine, Player player){
        if (card.getValue() == "SKIP"){
            threadPlayMachine.setHasPlayerPlayed(false);
            System.out.println("\nUtilizaste una carta de Skip.\n");
        } else if (card.getValue() =="RESERVE") {
            threadPlayMachine.setHasPlayerPlayed(false);
            System.out.println("\nUtilizaste una carta de Reverse.\n");
        } else if (card.getValue() =="TWO_WILD_DRAW") {
            eatCard(player, 2);
            System.out.println("\nUtilizaste un TWO_WILD_DRAW, " +player.getTypePlayer()+ " comio 2 cartas\n");
            threadPlayMachine.setHasPlayerPlayed(true);
        } else if (card.getValue() =="WILD") {
            //openColorSelectionDialog();//Falta que el color elegido por el jugador se use para que el jugador tenga que poner el mismo
            System.out.println("\nUtilizaste un WILD, ");
        }else if (card.getValue() == "FOUR_WILD_DRAW") {
            eatCard(player, 4);
            //openColorSelectionDialog();//Falta que el color elegido por el jugador se use para que el jugador tenga que poner el mismo
            System.out.println("\nUtilizaste un FOUR_WILD_DRAW, " +player.getTypePlayer()+ " comio 2 cartas\n");
            threadPlayMachine.setHasPlayerPlayed(true);
        }
        else {
            threadPlayMachine.setHasPlayerPlayed(true);
        }
    }

    /**
     * Verifica si un jugador ha ganado después de jugar una carta.
     *
     * @param playerType El tipo de jugador que realizó el movimiento.
     */
    private void postMoveActions(String playerType) {
        if (playerType.equals(humanPlayer.getTypePlayer())) {
            if (humanPlayer.getCardsPlayer().isEmpty()) {
                alertBox.showMessage("GANADOR", "Has ganado! \uD83C\uDFC6");
                System.out.println("\nFin de la partida!\n");
                isGameOver();
            }
        } else if (playerType.equals(machinePlayer.getTypePlayer())) {
            if (machinePlayer.getCardsPlayer().isEmpty()) {
                alertBox.showMessage("GAME OVER", "La maquina ha ganado! \uD83E\uDD16 ");
                System.out.println("\nFin de la partida!\n");
                isGameOver();
            }
        }
    }
}
