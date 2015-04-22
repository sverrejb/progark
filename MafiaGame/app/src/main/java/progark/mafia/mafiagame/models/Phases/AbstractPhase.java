package progark.mafia.mafiagame.models.Phases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Player;
import progark.mafia.mafiagame.models.Roles.AbstractRole;
import progark.mafia.mafiagame.utils.ComparePhases;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class AbstractPhase implements Comparable<AbstractPhase>{

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

    // The id of the phase.
    String phaseId;

    // The name of the phase.
    String phaseName;

    // Value for including this phase in the game.
    boolean enabled = true;

    // Value for checking mandatory. Mandatory phases cannot be disabled.
    boolean mandatory = false;

    // The order this phase happens in the list of phases.
    float order;

    // This is a special value and if set to true requires that a performer is not null when
    // performing an action
    boolean mandatoryPerformer;

    // A list of players that has been marked for elimination during this phase

    // The displayName of the phase. Can be used in-game description
    String displayName;

    // Default constructor.
    public AbstractPhase(GameLogic gl, String phaseId, String phaseName, float order) {
        this.gl = gl;
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.order = order;
        participateRoles = new ArrayList<String>();
        observeRoles = new ArrayList<String>();
        phaseMap.put(this.phaseId, this);
    }


    AbstractPhase(GameLogic gl) {
        this.gl = gl;
    }

    AbstractPhase() {

    }

    public Float getOrder() {
        return this.order;
    }



    // Creates a copy of the current class and returns it.
    public abstract AbstractPhase createCopy();

    // Executes the sub-class-specific action.
    // performer is the player who has voted to perform the action.
    // target is the targeted (the one with most votes) player.
    public abstract void performAction(Player[] performer, Player target);

    // Returns all phases in play.
    public static ArrayList<AbstractPhase> getPhases() {
        return phases;
    }

    public static ArrayList<AbstractPhase> getActivePhasesInOrder() {

        ArrayList<AbstractPhase> allPhases = getPhases();
        ArrayList<AbstractPhase> activePhases = new ArrayList<AbstractPhase>();

        for(int i = 0; i < allPhases.size(); i++) {
            AbstractPhase currentPhase = allPhases.get(i);
            if(currentPhase.enabled) {
                activePhases.add(currentPhase);
            }

        }

        AbstractPhase[] activeArray = new AbstractPhase[activePhases.size()];
        activeArray = activePhases.toArray(activeArray);
        System.out.println(activeArray);
        Arrays.sort(activeArray, new ComparePhases());
        activePhases = new ArrayList<>(Arrays.asList(activeArray));
        return activePhases;

    }

    // Returns all roles that can participate in this phase
    public ArrayList<String> getParticipatingRoles() {
        return participateRoles;
    }

    // Returns all roles that can observe (but not participate) in this phase
    public ArrayList<String> getObserveRoles() {
        return observeRoles;
    }

    public String getId() {
        return this.phaseId;
    }

    public String getPhaseName() { return this.phaseName;}

    // Parent method that fires when the phase begins. Add any code to any child that requires some kind
    // of check here.
    public void onPhaseBegin() {

    }

    // Parent method that fires when the phase ends. Add any code to any child that requires som kind
    // of check here.
    public void onPhaseEnd() {

    }

    public void beginVote() {

    }

    // Check to see if a specific role can participate in a given phase
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

    // Methods for disabling and enabling roles. Disabling mandatory phases are not allowed.
    public void disable() throws Exception {
        if(!mandatory) {
            for(String role : participateRoles) {
                AbstractRole roleObject = AbstractRole.getMap().get(role);
                ArrayList<String> AssoPhases = roleObject.getPhases();
                int searchIndex = AssoPhases.indexOf(roleObject);
                AssoPhases.remove(searchIndex);
                if(AssoPhases.isEmpty()) {
                    roleObject.disable();
                }
            }

        }

        else {
            throw new Exception("Attempt to disable mandatory phase. This is not allowed");
        }
    }

    public void enable() {
        this.enabled = true;
    }


    // When mandatoryPerformer is true, performer can not be null.
    public void ensurePerformerNotNull(Player performer) {
        if(mandatoryPerformer && performer == null) {
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

    public boolean hasMandatoryPerformer() {
        return mandatoryPerformer;
    }


    public int compareTo(AbstractPhase otherPhase) {

        if(otherPhase.order > this.order) {
            return 1;

        }

        else if (otherPhase.order < this.order) {
            return -1;
        }

        else {
            return 0;
        }


        }

    }
