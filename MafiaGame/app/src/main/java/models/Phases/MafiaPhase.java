package models.Phases;

import controller.GameLogic;

/**
 * Created by Daniel on 20.03.2015.
 */
public class MafiaPhase extends AbstractPhase {



    public MafiaPhase(GameLogic gl) {
        super(gl);
        phaseId = "mafia";
        phaseName = "Mafia Phase";
        order = 199f;
        participateRoles[0] = "mafia";
    }

    @Override
    public AbstractPhase createCopy() {
        MafiaPhase newCopy = new MafiaPhase(gl);
        newCopy.phaseId = phaseId;
        newCopy.phaseName = phaseName;
        newCopy.order = order;
        newCopy.participateRoles = participateRoles;
        newCopy.gl = gl;

        return newCopy;


    }
}
