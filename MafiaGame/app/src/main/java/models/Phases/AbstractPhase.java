package models.Phases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.GameLogic;
import models.Roles.AbstractRole;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class AbstractPhase {

    // GameLogic controller. This should be added when any child is created to receive global
    // settings

    GameLogic gl;

    // A list of all phases
    static ArrayList<AbstractPhase> phases = new ArrayList<AbstractPhase>();

    // A map of all phases, use phase id for key.
    static Map<String, AbstractPhase> phaseMap = new HashMap<String, AbstractPhase>();

    // A list of roles (id) that can participate (Vote) in this phase.
    // Special case: Use 'all' in the first (0) index to allow all players to participate in the phase
    String[] participateRoles;

    // A list of roles (id) that can observe the voting in this phase.
    // Note that all participants should automatically be considered observants
    String[] observeRoles;

    String phaseId;

    String phaseName;

    float order;

    // A list of players that has been marked for elimination during this phase

    // The displayName of the phase. Can be used in-game description
    String displayName;

    public AbstractPhase(String phaseId, String phaseName, float order, String[] participateRoles) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.order = order;
        this.participateRoles = participateRoles;
        AbstractPhase.phases.add(this);

    }
    // Default constructor.
    public AbstractPhase(GameLogic gl) {
        this.gl = gl;
        phaseMap.put(this.phaseId, this);
    }

    // Creates a copy of the current class and returns it.
    public abstract AbstractPhase createCopy();

    // Parent method that fires when the phase begins. Add any code to any child that requires some kind
    // of check here.
    public void onPhaseBegin() {

    }

    // Parent method that fires when the phase ends. Add any code to any child that requires som kind
    // of check here.
    public void onPhaseEnd() {

    }

    // Check to see if a specific role can participate in this phase
    public boolean canParticipate(AbstractRole role) {
        if(participateRoles[0].equals("all")) {
            return true;
        }

        for(String x : participateRoles) {
            if(x.equals(role.getId())) {
                return true;
            }
        }
        return false;

    }


    // Returns if the given role can or cannot observe the current phase.

    public boolean canObserve(AbstractRole role) {
        if(observeRoles[0].equals("all")) {
            return true;
        }

        for(String x : observeRoles) {
            if(x.equals(role.getId())) {
                return true;
            }
        }
        return false;

    }
}
