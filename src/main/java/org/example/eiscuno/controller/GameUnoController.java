package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.List;

public class GameUnoController {

    @FXML
    private GridPane gridPaneCardsMachine;

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

    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
//        printCardsMachinePlayer();

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView);
        threadPlayMachine.start();
    }

    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
    }

    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                if (canPlayCard(card)) {
                    playCard(card);
                    printCardsHumanPlayer();
//                    printCardsMachinePlayer();
                    checkGameOver();
                } else {
                    showAlert("Invalid Move", "You cannot play this card.");
                }
            });

            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

//    private void printCardsMachinePlayer() {
//        this.gridPaneCardsMachine.getChildren().clear();
//        List<Card> machineCards = this.machinePlayer.getCardsPlayer();
//
//        for (int i = 0; i < machineCards.size(); i++) {
//            String imagePath = "/images/card_back.png";
//            ImageView cardBack = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
//            cardBack.setFitHeight(90);
//            cardBack.setFitWidth(70);
//            this.gridPaneCardsMachine.add(cardBack, i, 0);
//        }
//    }

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

    @FXML
    void onHandleTakeCard(ActionEvent event) {
        gameUno.eatCard(humanPlayer, 1);
        printCardsHumanPlayer();
    }

    @FXML
    void onHandleUno(ActionEvent event) {
        gameUno.haveSungOne("HUMAN_PLAYER");
    }

    private boolean canPlayCard(Card card) {
        try {
            Card topCard = this.table.getCurrentCardOnTheTable();
            return card.getColor().equals(topCard.getColor()) || card.getValue().equals(topCard.getValue()) || card.getColor().equals("WILD");
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    private void playCard(Card card) {
        gameUno.playCard(card);
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());
        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
        handleSpecialCard(card);
        threadPlayMachine.setHasPlayerPlayed(true);
    }

    private void handleSpecialCard(Card card) {
        switch (card.getValue()) {
            case "+2":
                gameUno.eatCard(machinePlayer, 2);
                break;
            case "+4":
                gameUno.eatCard(machinePlayer, 4);
                break;
            case "SKIP":
                // Skip the machine player's turn
                threadPlayMachine.setHasPlayerPlayed(true);
                break;
            case "REVERSE":
                // Reverse the turn order if necessary
                // Implement your own reverse logic here
                break;
            case "WILD":
                // Let the player choose a new color
//                chooseNewColor();
                break;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void checkGameOver() {
        if (this.humanPlayer.getCardsPlayer().isEmpty()) {
            showAlert("Game Over", "Congratulations! You have won the game.");
            resetGame();
        } else if (this.machinePlayer.getCardsPlayer().isEmpty()) {
            showAlert("Game Over", "The machine has won the game.");
            resetGame();
        }
    }

    private void resetGame() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
//        printCardsMachinePlayer();
    }
}
