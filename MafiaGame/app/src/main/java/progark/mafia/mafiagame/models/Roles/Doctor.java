package progark.mafia.mafiagame.models.Roles;

import progark.mafia.mafiagame.controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Doctor extends AbstractRole {

    final static int MAX_NUMBER = 1;

    public final static String PERM_ID = "doctor";
    public final static String DISPLAY_NAME = "Doctor";

    public Doctor(GameLogic gl) {
        super(gl, PERM_ID, DISPLAY_NAME);
    }

    @Override
    public void createCopy() {
        Doctor newCopy = new Doctor(gl);
        newCopy.id = id;
        newCopy.displayName = displayName;
    }
}
