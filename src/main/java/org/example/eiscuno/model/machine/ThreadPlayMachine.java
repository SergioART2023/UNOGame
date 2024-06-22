package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.alertbox.AlertBox;

import java.util.HashMap;
import java.util.Map;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private Player humanPlayer;
    private GameUno gameUno;
    private ThreadPlayMachine threadPlayMachine;
    private Deck deck;
    public AlertBox alertBox = new AlertBox();

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView,Deck deck, GameUno gameUno, ThreadPlayMachine threadPlayMachine,Player humanPlayer) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.deck = deck;
        this.gameUno = gameUno;
        this.threadPlayMachine = threadPlayMachine;
        this.humanPlayer = humanPlayer;
    }

    public void run() {
        while (true){
            if(hasPlayerPlayed){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                putCardOnTheTable();
                hasPlayerPlayed = false;
            }
        }
    }

    private void putCardOnTheTable(){
        try {
            Card topCard = table.getCurrentCardOnTheTable();
            String currentColor = topCard.getColor();
            String currentValue = topCard.getValue();

            Card playableCard = machinePlayer.findPlayableCard(currentColor, currentValue);

            if (playableCard != null) {
                table.addCardOnTheTable(playableCard);
                tableImageView.setImage(playableCard.getImage());
                machinePlayer.getCardsPlayer().remove(playableCard);
                isWildCards(playableCard, humanPlayer);
            } else {
                gameUno.eatCard(machinePlayer, 1);
                alertBox.showMessage("Tu turno", "La máquina comió, sigue tu turno \uD83C\uDFC3");
            }

            unoMachine();
            System.out.println("\nMazo de la máquina: ");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No hay cartas en el tablero.");
        }
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
    private String chooseBestColor() {
        // Maps to count cards by color
        Map<String, Integer> colorCount = new HashMap<>();
        colorCount.put("Azul", 0);
        colorCount.put("Verde", 0);
        colorCount.put("Rojo", 0);
        colorCount.put("Amarillo", 0);

        // Counts cards of each color
        for (Card card : machinePlayer.getCardsPlayer()) {
            String color = card.getColor();
            if (colorCount.containsKey(color)) {
                colorCount.put(color, colorCount.get(color) + 1);
            }
        }

        // Finds the most cards of said color
        int maxCount = -1;
        String bestColor = "";
        for (Map.Entry<String, Integer> entry : colorCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestColor = entry.getKey();
            } else if (entry.getValue() == maxCount) {
                // When tied, chooses randomly between the tied colors
                if (Math.random() < 0.5) {
                    bestColor = entry.getKey();
                }
            }
        }

        return bestColor;
    }

    public void unoMachine() {
        if (machinePlayer.getCardsPlayer().size() == 1) {
            alertBox.showMessage("Enemigo", "El rival cantó UNO ");
        }
    }
    public void isWildCards(Card card, Player player){
        if (card.getValue().equals("SKIP")) {
            run();
            System.out.println("\nEl rival usó Skip.\n");
            setHasPlayerPlayed(true);
        } else if (card.getValue().equals("REVERSE")) {
            run();
            setHasPlayerPlayed(false);
            System.out.println("\nEl rival usó Reverse.\n");
        } else if (card.getValue().equals("TWO_WILD_DRAW")) {
            gameUno.eatCard(player, 2);
            System.out.println("\nEl rival usó TWO_WILD_DRAW, " + player.getTypePlayer() + " comió 2 cartas.\n");
            setHasPlayerPlayed(true);
        } else if (card.getValue().equals("WILD")) {
            // Select color based on most cards of that color
            String chosenColor = chooseBestColor();
            table.setCurrentColor(chosenColor);
            alertBox.showMessage("Wild", "El rival escogió " + chosenColor);
            setHasPlayerPlayed(true);
        } else if (card.getValue().equals("FOUR_WILD_DRAW")) {
            // Select color based on most cards of that color
            String chosenColor = chooseBestColor();
            table.setCurrentColor(chosenColor);
            alertBox.showMessage("+4", "El rival escogió " + chosenColor);
            gameUno.eatCard(player, 4); // Hace que el jugador humano coma 4 cartas
            System.out.println("\nEl rival escogió FOUR_WILD_DRAW, " + player.getTypePlayer() + " comió 4 cartas.\n");
            setHasPlayerPlayed(true);
        } else {
            setHasPlayerPlayed(true);
        }
    }

    public boolean getHasPlayerPlayed() {
        return hasPlayerPlayed;
    }
}
