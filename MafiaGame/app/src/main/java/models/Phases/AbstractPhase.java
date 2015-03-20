package models.Phases;

import java.util.ArrayList;

import controller.GameLogic;
import models.Player;
import models.Roles.AbstractRole;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class AbstractPhase {

    // GameLogic controller. This should be added when any child is created to receive global
    // settings

    GameLogic gameLogic;

    // A list of roles (id) that can participate (Vote) in this phase.
    // Special case: Use 'all' in the first (0) index to allow all players to participate in the phase
    String[] ParticipateRole;

    // A list of roles (id) that can observe the voting in this phase.
    // Note that all participants should automatically be considered observants
    String[] ObserveRole;

    // A list of players that has been marked for elimination during this phase
    ArrayList<Player> killList;

    // The name of the phase. Can be used in-game description
    String name;

    public abstract void createInstance();

    public abstract void createCopy();


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
        if(ParticipateRole[0].equals("all")) {
            return true;
        }

        for(AbstractRole x : ParticipateRole) {
            if(x.equals(role.getId())) {
                return true;
            }
        }
        return false;

    }

    public boolean canObserve(AbstractRole role) {
        if(ObserveRole[0].equals("all")) {
            return true;
        }

        for(AbstractRole x : ObserveRole) {
            if(x.equals(role.getId())) {
                return true;
            }
        }
        return false;

    }

    public ArrayList getKillList() {
        return killList;
    }

    public void addToKillList(Player player) {
        this.killList.add(player);
    }


}
