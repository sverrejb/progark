package models.Phases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.GameLogic;
import models.Player;
import models.Roles.AbstractRole;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class AbstractPhase {

    // GameLogic controller. This should be added when any child is created to receive global
    // settings

    GameLogic gl;

    // A list of all phases added in the game
    public static ArrayList<AbstractPhase> phases = new ArrayList<AbstractPhase>();

    // A map of all phases, use phase id for key.
    static Map<String, AbstractPhase> phaseMap = new HashMap<String, AbstractPhase>();

    // A list of roles (id) that can participate (Vote) in this phase.
    // Special case: Use 'all' in the first (0) index to allow all players to participate in the phase
    ArrayList<String> participateRoles;

    // A list of roles (id) that can observe the voting in this phase.
    // Note that all participants should automatically be considered observants
    ArrayList<String> observeRoles;

    String phaseId;

    String phaseName;

    // Value for including this phase in the game.
    boolean enabled = true;

    // Value for checking mandatory. Mandatory phases cannot be disabled.
    boolean mandatory = false;

    float order;

    // This is a special value and if set to true requires that a performer is not null when
    // performing an action

    boolean notePerformer;

    // A list of players that has been marked for elimination during this phase

    // The displayName of the phase. Can be used in-game description
    String displayName;

    public AbstractPhase(String phaseId, String phaseName, float order) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.order = order;
        this.participateRoles = participateRoles;

    }
    // Default constructor.
    public AbstractPhase(GameLogic gl) {
        this.gl = gl;
        participateRoles = new ArrayList<String>();
        observeRoles = new ArrayList<String>();
    }

    // Creates a copy of the current class and returns it.
    public abstract AbstractPhase createCopy();

    // Executes the sub-class-specific action.
    // performer is the player who has voted to perform the action.
    // target is the targeted (the one with most votes) player.
    abstract void performAction(Player performer, Player target);


    public static ArrayList<AbstractPhase> getPhases() {
        return phases;
    }

    public ArrayList<String> getParticipatingRoles() {
        return participateRoles;
    }

    public ArrayList<String> getObserveRoles() {
        return observeRoles;
    }

    public String getId() {
        return this.phaseId;
    }







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
        if(participateRoles.contains("all")) {
            return true;
        }

        for(String x : participateRoles) {
            if(x.equals(role.getId())) {
                return true;
            }
        }
        return false;

    }

    // Methods for disabling and enabling roles
    public void disable() throws Exception {
        if(!mandatory) {
            this.enabled = false;
        }
        else {
            throw new Exception("Attempt to disable mandatory phase. This is not allowed");
        }
    }

    public void enable() {
        this.enabled = true;
    }


    // When notePerformer is true, performer can not be null.
    public void ensurePerformerNotNull(Player performer) {
        if(notePerformer && performer == null) {
            throw new NullPointerException("Error in Phase: " + this.phaseId +
                    "Performer can not be null for a performer-required action");
        }
    }


    // Returns if the given role can or cannot observe the current phase.

    public boolean canObserve(AbstractRole role) {
        if(observeRoles.contains("all")) {
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
