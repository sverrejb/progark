package progark.mafia.mafiagame.models.Phases;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class CivillianPhase extends AbstractPhase {

    public static final String PHASE_ID = "civillian";
    public static final String PHASE_NAME = "Civillian PHase";
    public static final float ORDER = 200f;


    public CivillianPhase(GameLogic gl) {
        super(gl, PHASE_ID, PHASE_NAME, ORDER);
        participateRoles.add("all");
        mandatory = true;

        // Uncomment this to allow the little girl to be a witness at observing if "littleGirl" is
        // a valid role
        // observeRoles.add("LittleGirl");
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

    @Override
    public void performAction(Player[] performer, Player target) {
        gl.addToKillList(target);
    }

    public void onPhaseEnd() {
        gl.commitRound();
    }

    @Override
    public boolean onPhaseBegin() {
        return gl.commitRound();
    }
}
