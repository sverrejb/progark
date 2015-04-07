package models.Roles;

import controller.GameLogic;
import models.Phases.AbstractPhase;
import models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Civillian extends AbstractRole {

    static int numberInPlay = 0;
    final static int MAX_NUMBER = 0;


    @Override
    int getNumberInplay() {
        return numberInPlay;
    }

    @Override
    int getMaxNumber() {
        return MAX_NUMBER;
    }

    public Civillian(GameLogic gl) {
        super(gl);
        this.id = "civillian";
        this.displayName = "Civillian";
    }

    @Override
    void createCopy() {
        Civillian newCopy = new Civillian(gl);
        newCopy.id = id;
        newCopy.displayName = displayName;


    }


}
