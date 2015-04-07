package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.fragments.GameFragment;
import progark.mafia.mafiagame.models.Phases.AbstractPhase;
import progark.mafia.mafiagame.models.Phases.CivillianPhase;
import progark.mafia.mafiagame.models.Phases.MafiaPhase;
import progark.mafia.mafiagame.models.Player;
import progark.mafia.mafiagame.models.Roles.AbstractRole;
import progark.mafia.mafiagame.models.Roles.Civillian;

/**
 * Created by Daniel on 10.03.2015.
 */
public class GameLogic {
    Player[] playersInGame;
    boolean includeGameMaster;

    public static Player[] killList;

    // WeakRef so avoid weird mem leaks
    WeakReference<Activity> parentActivity;

    DuplexCommunicator communicator;


    public GameLogic(Activity parent, DuplexCommunicator communicator, boolean isServer){
        parentActivity = new WeakReference<>(parent);
        this.communicator = communicator;

        if(isServer) {// Do stuff
        }


        // Setup view

        GameFragment gameFragment = new GameFragment();

        FragmentTransaction transaction = parentActivity.get().getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_placeholder, gameFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    public void assignPlayers() {
        ArrayList<Player> unAssignedPlayers = new ArrayList<Player>();

    }

    public void addPlayer(String id, String name) {
        Player p = new Player(id, name);
    }


    // Method used to update all current tentative actions, such as killing off players and updating players
    // on progress. commitRound() should either be performed after all phases of a round has finished OR
    // on special occasions during the onePhaseEnd method in the phase class.

    public void commitRound() {

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
