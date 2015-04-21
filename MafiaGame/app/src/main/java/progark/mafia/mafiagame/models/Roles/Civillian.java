package progark.mafia.mafiagame.models.Roles;

import progark.mafia.mafiagame.controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Civillian extends AbstractRole {

    final static int MAX_NUMBER = 0;
    public static final String PERM_ID = "civillian";
    public static final String DISPLAY_NAME = "Civillian";
    public static final String TEAM = "civillian";

    String id = "civillian";
    String displayName = "Civillian";


    @Override
    public int getNumberInPlay() {
        return numberInPlay;
    }

    public Civillian(GameLogic gl) {
        super(gl, PERM_ID, DISPLAY_NAME, TEAM);

    }

    @Override
    public void createCopy() {
        Civillian newCopy = new Civillian(gl);
        newCopy.id = id;
        newCopy.displayName = displayName;
    }

}
