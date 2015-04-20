package progark.mafia.mafiagame.models.Roles;

import progark.mafia.mafiagame.controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Mafia extends AbstractRole {

    final static int MAX_NUMBER = 3;
    public static final String PERM_ID = "mafia";
    public static final String DISPLAY_NAME = "Mafia";
    public static final String TEAM = "mafia";


    public Mafia(GameLogic gl) {
        super(gl, PERM_ID, DISPLAY_NAME, TEAM);
    }

    @Override
    public void createCopy() {
        Mafia newCopy = new Mafia(gl);
        newCopy.id = id;
        newCopy.displayName = displayName;
    }
}
