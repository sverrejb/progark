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
import progark.mafia.mafiagame.models.Roles.Doctor;
import progark.mafia.mafiagame.models.Roles.Mafia;
import progark.mafia.mafiagame.utils.PlayerArraySearcher;
import progark.mafia.mafiagame.utils.Randomizer;

/**
 * Created by Daniel on 10.03.2015.
 */
public class GameLogic {

    ArrayList<Player> playersInGame = new ArrayList<Player>();
    boolean includeGameMaster;
    ArrayList<AbstractPhase> gamePhases;
    AbstractPhase currentPhase;

    // Contains the player who will be killed on the next commit.
    public static ArrayList<Player> killList = new ArrayList<Player>();

    // Contains the player that - if in the kill list - will be removed from it during the next commit
    public static ArrayList<Player> saveList = new ArrayList<Player>();


// Dette må forandres på
//    public static void main(String[] args) {
//        GameLogic gl = new GameLogic();
//        gl.createTestSetData();
//        gl.initializeGameData();
//    }




    // WeakRef so avoid weird mem leaks
    WeakReference<Activity> parentActivity;

    DuplexCommunicator communicator;


    public GameLogic(Activity parent, DuplexCommunicator communicator, boolean isServer) {
        parentActivity = new WeakReference<>(parent);
        this.communicator = communicator;

        if (isServer) {// Do stuff
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

    public void generateRolesAndPhases() {

        // Might consider putting this into its own class. Initializes the Server of the game. At least Phases and Roles.

        //Initialize all phases
        AbstractPhase.getPhases().add(new CivillianPhase(this));
        AbstractPhase.getPhases().add(new MafiaPhase(this));

        // ...

        // Create a list of all active phases sorted in the correct order (low number first);
        gamePhases = AbstractPhase.getActivePhasesInOrder();


        // Initialize all roles
        Civillian.getRoles().add(new Civillian(this));
        AbstractRole.getRoles().add(new Mafia(this));
        Doctor.getRoles().add(new Doctor(this));


        gamePhases = AbstractPhase.getActivePhasesInOrder();
        System.out.print("Current active phases: ");
        for(AbstractPhase x : gamePhases) {
            System.out.print(x.getId() + " | ");
        }
        System.out.println();

        connectRolesToPhase();
    }


    public void assignPlayers() {
        ArrayList<Player> unAssignedPlayers = new ArrayList<Player>(playersInGame);
        for(AbstractRole role : AbstractRole.getRoles()) {
            while(role.getNumberInPlay() < role.getMax_number()) {
                int randomPlayer = Randomizer.getRandomInt(0, unAssignedPlayers.size());
                Player p = unAssignedPlayers.remove(randomPlayer);
                p.assignRole(role.getId());
                System.out.println("Player " + p.getName() + " got the role of " + role.getDisplayName());
                role.increaseNumberInPlay();
            }
        }

    }

    public void addPlayer(String id, String name) {
        Player p = new Player(id, name);
        playersInGame.add(p);
    }


    // Method used to update all current tentative actions, such as killing off players and updating players
    // on progress. commitRound() should either be performed after all phases of a round has finished OR
    // on special occasions during the onePhaseEnd method in the phase class.
    public void commitRound() {
        for(Player p : saveList) {
            int savedIndex = PlayerArraySearcher.SearchArray(killList, p);
            if(savedIndex != -1) {
                killList.remove(p);
            }
        }

        for(Player p : killList) {
           // Perform method to kill player;
        }

        killList.clear();
        saveList.clear();
    }

    public void initializeGameData() {



        generateRolesAndPhases();
        assignPlayers();

        for(AbstractPhase x :AbstractPhase.getPhases()) {
            System.out.println("Checking all roles of phase: " + x.getId());
            for(String role : x.getParticipatingRoles()) {
                System.out.println(role + " can participate in this phase");
            }
        }

        beginNextPhase();
    }



    public void beginNextPhase() {
        currentPhase = gamePhases.remove(0);
        System.out.println("Now beginning " + currentPhase.getId());
        currentPhase.onPhaseBegin();
    }

    public void addToKillList(Player p) {
        if(PlayerArraySearcher.SearchArray(killList, p) == -1) {
            killList.add(p);
        }

    }

    public void removeFromKillList(Player p) {
        int playerFound = PlayerArraySearcher.SearchArray(killList, p);
        if(playerFound != -1) {
            killList.remove(playerFound);
        }
     }

    public void addToSaveList(Player p) {
        if(PlayerArraySearcher.SearchArray(saveList, p) == -1) {
            saveList.add(p);
        }

    }

    public void removeFromSaveList(Player p) {
        int playerFound = PlayerArraySearcher.SearchArray(saveList, p);
        if(playerFound != -1) {
            saveList.remove(playerFound);
        }
    }



    public void voteComplete(Player target, Player performer) {
        currentPhase.performAction(target, performer);
        currentPhase.onPhaseEnd();
    }




    // For each phase in the game, get the roles connected through that game.
    // Explicitly add that phase to the role for future access.
    // This is done in this way to maintain concurrency between roles and phases connectivity.
    // And to ease the process of enabling or disabling both roles and phases.
    public void connectRolesToPhase() {

        for (AbstractPhase phase : AbstractPhase.getPhases()) {
            System.out.println("Now finding all roles for phase: " + phase.getId());

            for (String role : phase.getObserveRoles()) {
                System.out.println("Getting all observer phases of role: " + role);
                ArrayList<String> allPhasesOfRole = AbstractRole.getMap().get(role).getPhases();
                if (!allPhasesOfRole.contains(role)) {
                    allPhasesOfRole.add(phase.getId());
                }
            }
            for (String role : phase.getParticipatingRoles()) {
                if (role != "all") {
                    System.out.println("Getting all phases of role: " + role);
                    ArrayList<String> allPhasesOfRole = AbstractRole.getMap().get(role).getPhases();
                    if (!allPhasesOfRole.contains((role))) {
                        allPhasesOfRole.add(phase.getId());
                    }
                }
            }

            }

        }


    public void createTestSetData() {
        Player p = new Player("ASJDOAJD", "Daniel");
        Player o = new Player("ASDKAOSD", "Bob");
        Player q = new Player("ASDKALSD", "Robert");
        playersInGame.add(p);
        playersInGame.add(o);
        playersInGame.add(q);

    }







    }

