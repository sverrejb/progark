package progark.mafia.mafiagame.models.Phases;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class MafiaPhase extends AbstractPhase {



    public MafiaPhase(GameLogic gl) {
        super(gl);
        phaseId = "mafia";
        phaseName = "Mafia Phase";
        order = 199f;
        participateRoles.add("mafia");
        mandatory = true;
        phaseMap.put(this.phaseId, this);
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
    void performAction(Player performer, Player target) {

    }
}
