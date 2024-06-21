package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Represents the table in the Uno game where cards are played.
 */
public class Table {
    private ArrayList<Card> cardsTable;
    private String currentColor;
    private String wildColor;

    /**
     * Constructs a new Table object with no cards on it.
     */
    public Table(){
        this.cardsTable = new ArrayList<Card>();
        this.currentColor = null;
    }

    /**
     * Adds a card to the table.
     *
     * @param card The card to be added to the table.
     */
    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
        if (!card.isWildCard()) {
            this.currentColor = card.getColor(); // Actualizar el color actual si no es Wild
        }
    }
    /**
     * Retrieves the current card on the table.
     *
     * @return The card currently on the table.
     * @throws IndexOutOfBoundsException if there are no cards on the table.
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
        return this.cardsTable.get(this.cardsTable.size()-1);
    }
    public void setWildColor(String color) {
        this.wildColor = color;
    }
    public String getWildColor() {
        return wildColor;
    }
    public String getCurrentColor() {
        return currentColor;
    }

    /**
     * Sets the current color on the table.
     *
     * @param currentColor The color to set as current on the table.
     */
    public void setCurrentColor(String currentColor) {
        this.currentColor = currentColor;
    }

    public Card getTopCard() {
        if (!cardsTable.isEmpty()) {
            return cardsTable.get(cardsTable.size() - 1);
        } else {
            return null;
        }
    }
}
