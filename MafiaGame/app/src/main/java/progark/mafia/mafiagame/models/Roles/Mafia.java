package progark.mafia.mafiagame.models.Roles;

import progark.mafia.mafiagame.controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Mafia extends AbstractRole {

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

    Mafia(GameLogic gl) {
        super(gl);
        this.id = "Mafia";
        this.displayName = "Mafia";
    }

    @Override
    void createCopy() {
        Mafia newCopy = new Mafia(gl);
        newCopy.id = id;
        newCopy.displayName = displayName;
    }
}
