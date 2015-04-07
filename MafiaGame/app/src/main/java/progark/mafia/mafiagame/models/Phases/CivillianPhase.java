package progark.mafia.mafiagame.models.Phases;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class CivillianPhase extends AbstractPhase {



    public CivillianPhase(GameLogic gl) {
        super(gl);
        phaseId = "civillian";
        phaseName = "Civillian Phase";
        order = 200f;
        participateRoles.add("all");
        mandatory = true;
        phaseMap.put(this.phaseId, this);

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

    @Override
    void performAction(Player performer, Player target) {


    }

    public void onPhaseEnd() {
        gl.commitRound();
    }


}
