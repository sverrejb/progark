package progark.mafia.mafiagame.models.Phases;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class MafiaPhase extends AbstractPhase {

    public static final String PHASE_ID = "mafia";
    public static final String PHASE_NAME = "Mafia Phase";
    public static final float ORDER = 199f;

    public MafiaPhase(GameLogic gl) {
        super(gl, PHASE_ID, PHASE_NAME, ORDER);
        participateRoles.add("mafia");
        mandatory = true;
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

    @Override
    public void performAction(Player[] performer, Player target) {
        gl.addToKillList(target);
    }
}
