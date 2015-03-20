package models.Phases;

import controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class CivillianPhase extends AbstractPhase {



    public CivillianPhase(GameLogic gl) {
        super(gl);
        phaseId = "civillian";
        phaseName = "Civillian Phase";
        order = 200f;
        participateRoles[0] = "all";

        // Uncomment this to allow the little girl to be a witness at observing if "littleGirl" is
        // a valid role

        // observeRoles[0] = "LittleGirl";
    }

    @Override
    public AbstractPhase createCopy() {
        CivillianPhase newCopy = new CivillianPhase(gl);
        newCopy.phaseId = phaseId;
        newCopy.phaseName = phaseName;
        newCopy.order = order;
        newCopy.participateRoles = participateRoles;
        newCopy.gl = gl;

        return newCopy;
    }
}
