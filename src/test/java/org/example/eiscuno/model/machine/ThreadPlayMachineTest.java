package org.example.eiscuno.model.machine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPlayMachineTest {
    @Test
    void setHasPlayerPlayedTrue() {
        ThreadPlayMachine threadPlayMachine = new ThreadPlayMachine(null,null,null,null,null,null,null);
        threadPlayMachine.setHasPlayerPlayed(true);
        assertTrue(threadPlayMachine.getHasPlayerPlayed());
    }
    @Test
    void setHasPlayerPlayedFalse() {
        ThreadPlayMachine threadPlayMachine = new ThreadPlayMachine(null,null,null,null,null,null,null);
        threadPlayMachine.setHasPlayerPlayed(false);
        assertFalse(threadPlayMachine.getHasPlayerPlayed());
    }
    @Test
    void setHasPlayerPlayedTrueAndFalse() {
        ThreadPlayMachine threadPlayMachine = new ThreadPlayMachine(null,null,null,null,null,null,null);
        threadPlayMachine.setHasPlayerPlayed(false);
        assertTrue(threadPlayMachine.getHasPlayerPlayed());
        threadPlayMachine.setHasPlayerPlayed(true);
        assertFalse(threadPlayMachine.getHasPlayerPlayed());
    }
}