package org.example.eiscuno.model.machine;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    boolean execute = true;
    private IThreadSingUNOMachine listener;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer,IThreadSingUNOMachine listener){
        this.cardsPlayer = cardsPlayer;
        this.listener=listener;
    }

    @Override
    public void run(){
        while (execute){
            try {
                Thread.sleep((long) (Math.random() * 5000));
                hasOneCardTheHumanPlayer();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void hasOneCardTheHumanPlayer(){
        if(cardsPlayer.size() == 1){
            System.out.println("UNO");
        }
    }
    public void setCondition(boolean condition) {
        execute = condition;
    }
}
