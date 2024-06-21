package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.IThreadSingUNOMachine;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.view.alertbox.AlertBox;


import java.util.List;
/**
 * Controller class for UNO.
 */
public class GameUnoController implements IThreadSingUNOMachine {

    @FXML
    private GridPane gridPaneCardsPlayer;
    @FXML
    private ImageView tableImageView;
    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    private boolean primeraCartaPuesta = false;
    private long machineTime;
    private long playerTime;
    private boolean machineSaidUno;
    private boolean playerSaidUno;
    public AlertBox alertBox = new AlertBox();
    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();


        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer(), this);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.deck, this.gameUno, this.threadPlayMachine, this.humanPlayer);
        threadPlayMachine.start();
    }
    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
    }
    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                try {
                    if (!primeraCartaPuesta) {
                        if (canPlayNumberCard(card)) {
                            tableImageView.setImage(card.getImage());
                            int cardIndex = findPosCardsHumanPlayer(card);
                            if (cardIndex != -1) {
                                humanPlayer.removeCard(cardIndex);
                                threadPlayMachine.setHasPlayerPlayed(true);
                                gameUno.playCard(card);
                                printCardsHumanPlayer();
                                primeraCartaPuesta = true;
                                System.out.println("\nTus cartas: ");
                            } else {
                                alertBox.showMessageError("Error", "Carta no encontrada. Intenta otra vez. \uD83C\uDCCF");
                            }
                        } else {
                            alertBox.showMessageError("Error", "Carta inválida. Intenta otra vez. \uD83C\uDCCF");
                        }
                    } else if (gameUno.canPlayCard(card)) {
                        tableImageView.setImage(card.getImage());
                        int cardIndex = findPosCardsHumanPlayer(card);
                        if (cardIndex != -1) {
                            humanPlayer.removeCard(cardIndex);
                            gameUno.isWildCards(card, threadPlayMachine, machinePlayer);
                            gameUno.playCard(card);
                            printCardsHumanPlayer();
                            deck.discardCard(card);
                        } else {
                            alertBox.showMessageError("Error", "Carta no encontrada. Intenta otra vez. \uD83C\uDCCF");
                        }
                    } else {
                        alertBox.showMessageError("Error", "Carta inválida. Intenta otra vez. \uD83C\uDCCF");
                    }
                } catch (IllegalArgumentException e) {
                    alertBox.showMessageError("Error", e.getMessage());
                }
            });
            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

    private boolean canPlayNumberCard(Card card) {
        return !card.isWildCard() && !card.isReverseCard() && !card.isSkipCard();
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }
    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        if (!deck.isEmpty()) {
            Card newCard = deck.takeCard();
            humanPlayer.addCard(newCard);
            deck.discardCard(newCard);
            printCardsHumanPlayer();
            System.out.println("\n Tus cartas: ");
            threadPlayMachine.setHasPlayerPlayed(true);
            System.out.println("\nTurno de la maquina");
        } else {
            deck.refillDeckFromDiscardPile();
            alertBox.showMessage("Mazo","El mazo se acabo!\nPero fue llenado nuevamente. \uD83C\uDCCF");
        }
    }
    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            System.out.println("\nEl jugador dijo ¡UNO!\n");
            playerTime = System.currentTimeMillis();
            playerSaidUno = true;
            checkUno();
            // Aquí puedes añadir lógica adicional si hay reglas específicas para cuando se dice "UNO"
        } else {
            alertBox.showMessageError("Error", "No puedes decir 'UNO' porque no tienes exactamente una carta. Toma una carta como penitencia. \uD83D\uDE14");
            // Penalización: por ejemplo, el jugador debe tomar 2 cartas
            gameUno.eatCard(humanPlayer, 1);
            System.out.println(" Tus cartas: ");
        }
    }
    @Override
    public void onMachineSaysUno()  {
        machineTime = System.currentTimeMillis();
        machineSaidUno = true;
        checkUno();
    }
    @FXML
    void OnHanbleExitButton(ActionEvent event) {
        GameUnoStage.deleteInstance();
    }
    private void checkUno() {
        System.out.println("\nMaquina lo dijo en "+machineTime);
        System.out.println("\njugador lo dijo en "+playerTime);
        if (machineSaidUno && playerSaidUno) {
            if (machineTime < playerTime || playerTime == 0) {
                alertBox.showMessage("UNO","¡Máquina dijo UNO más rápido! Toma una carta. \uD83C\uDCCF");
                System.out.println("\n¡Máquina dijo UNO más rápido! Jugador debe comer una carta.\n");
                String playerMachime = machinePlayer.getTypePlayer();
                gameUno.haveSingOne(playerMachime);
            } else {
                System.out.println("\n¡Jugador dijo UNO más rápido!\n");
                alertBox.showMessage("UNO","¡Has dicho UNO más rápido! \uD83D\uDE04");
            }
            machineTime = 0;
            playerTime = 0;
            machineSaidUno = false;
            playerSaidUno = false;
            threadSingUNOMachine.setCondition(true);
        }
    }
}
