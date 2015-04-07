package models.Roles;

import controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Doctor extends AbstractRole {

    static int numberInPlay = 0;
    final static int MAX_NUMBER = 1;


    @Override
    int getNumberInplay() {
        return numberInPlay;
    }

    @Override
    int getMaxNumber() {
        return MAX_NUMBER;
    }

    Doctor(GameLogic gl) {
        super(gl);
        this.id = "doctor";
        this.displayName = "Doctor";
    }

    @Override
    void createCopy() {
        Doctor newCopy = new Doctor(gl);
        newCopy.id = id;
        newCopy.displayName = displayName;
    }
}
