package progark.mafia.mafiagame.models.Phases;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class DoctorPhase extends AbstractPhase {

    public static final String PHASE_ID = "doctor";
    public static final String PHASE_NAME = "Doctor Phase";
    public static final float ORDER = 198f;

    public DoctorPhase(GameLogic gl) {
        super(gl, PHASE_ID, PHASE_NAME, ORDER);
        participateRoles.add("doctor");
        mandatory = true;
    }

    @Override
    public AbstractPhase createCopy() {
        DoctorPhase newCopy = new DoctorPhase(gl);
        newCopy.phaseId = phaseId;
        newCopy.phaseName = phaseName;
        newCopy.order = order;
        newCopy.participateRoles = participateRoles;
        newCopy.gl = gl;

        return newCopy;
    }

    @Override
    public void performAction(Player[] performer, Player target) {
        gl.addToSaveList(target);
    }
}
