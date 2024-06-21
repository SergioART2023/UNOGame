package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a card in the Uno game.
 */
public class Card {
    private String url;
    private String value;
    private String color;
    private Image image;
    private ImageView cardImageView;

    /**
     * Constructs a Card with the specified image URL and name.
     *
     * @param url the URL of the card image
     * @param value of the card
     * @param color the color of the card
     */
    public Card(String url, String value, String color) {
        if (value == null || color == null) {
            throw new IllegalArgumentException("Value and color cannot be null");
        }
        this.url = url;
        this.value = value;
        this.color = color;
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }

    /**
     * Creates and configures the ImageView for the card.
     *
     * @return the configured ImageView of the card
     */
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    /**
     * Gets the ImageView representation of the card.
     *
     * @return the ImageView of the card
     */
    public ImageView getCard() {
        return cardImageView;
    }

    public boolean isNumberCard() {
        return value.matches("[0-9]");
    }
    /**
     * Gets the image of the card.
     *
     * @return the Image of the card
     */
    public Image getImage() {
        return image;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public boolean isReverseCard() {
        return value.equals("REVERSE");
    }

    public boolean isSkipCard() {
        return value.equals("SKIP");
    }

    public boolean isWildCard() {
        return value.equals("WILD") || value.equals("FOUR_WILD_DRAW") ||value.equals("TWO_WILD_DRAW");
    }
    @Override
    public String toString() {
        return "Color: " + this.color + ", Valor: " + this.value;
    }
}
