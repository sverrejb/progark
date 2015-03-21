package controller;

import java.util.ArrayList;

import models.Phases.AbstractPhase;
import models.Phases.CivillianPhase;
import models.Phases.MafiaPhase;
import models.Player;
import models.Roles.AbstractRole;
import models.Roles.Civillian;

/**
 * Created by Daniel on 10.03.2015.
 */
public class GameLogic {
    Player[] playersInGame;

    Player[] killList;


    public void assignPlayers() {
        ArrayList<Player> unAssignedPlayers = new ArrayList<Player>();

    }

    public void initializeServer() {

        // Might consider putting this into its own class. Initializes the Server of the game. At least Phases and Roles.

        //Initialize all phases
        AbstractPhase.getPhases().add(new CivillianPhase(this));
        AbstractPhase.getPhases().add(new MafiaPhase(this));


        // Initialize all roles
        Civillian.getRoles().add(new Civillian(this));


        // For each phase in the game, get the roles connected through that game.
        // Explicitly add that phase to the role for future access.
        // This is done in this way to maintain concurrency between roles and phases connectivity.
        // And to ease the process of enabling or disabling both roles and phases.

        for(AbstractPhase phase : AbstractPhase.getPhases()) {
            for(String role : phase.getObserveRoles()) {
                AbstractRole.getMap().get(role).getPhases().add(phase.getId());
            }
            for(String role : phase.getParticipatingRoles()) {
                AbstractRole.getMap().get(role).getPhases().add(phase.getId());
            }

        }

    }





}
