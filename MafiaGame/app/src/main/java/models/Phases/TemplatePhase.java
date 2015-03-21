package models.Phases;

import controller.GameLogic;
import models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class TemplatePhase {

    // This is a base template that you can copy from to create new phases.
    // ### DISCLAIMER - THIS TEMPLATE IS NOT UP TO DATE. DO NOT USE AT THIS TIME ###

    public class CivillianPhase extends AbstractPhase {



        public CivillianPhase(GameLogic gl) {
            super(gl);
            // The unique identifier of the phase. Required;
            phaseId = null;
            // The displayName of the phase. Required;
            phaseName = null;
            // The order of the phase. A smaller number will make it appear earlier in the phase queue. Required
            order = 0f;
            // roleid for any role that can participate in the phase. Required
            participateRoles.add("");
            //participateRoles[x]= "";

            // roleID for any role that can observe the phase voting. Optional;
            // observeRoles[0] = "";
        }

        @Override
        public AbstractPhase createCopy() {
            AbstractPhase newCopy = null;
        //  TemplatePhase newCopy = new TemplatePhase newCopy(gl);
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









}
