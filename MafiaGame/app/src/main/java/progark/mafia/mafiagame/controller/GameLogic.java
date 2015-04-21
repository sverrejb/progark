package progark.mafia.mafiagame.controller;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.models.Phases.AbstractPhase;
import progark.mafia.mafiagame.models.Phases.CivillianPhase;
import progark.mafia.mafiagame.models.Phases.DoctorPhase;
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

    VotingSystem votingSystem;
    ArrayList<Player> playersInGame = new ArrayList<Player>();
    boolean includeGameMaster;
    ArrayList<AbstractPhase> gamePhases;
    AbstractPhase currentPhase;
    boolean gameStarted;


    // These lists serve as storage for players that relate to different actions performed in Commits.
    // These can be extended as new phases or roles are created.

    // Contains the player who will be killed on the next commit.
    public static ArrayList<Player> killList = new ArrayList<Player>();

    // Contains the player that - if in the kill list - will be removed from it during the next commit
    public static ArrayList<Player> saveList = new ArrayList<Player>();


// Dette m� forandres p�
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

    public void addVotingSystem(VotingSystem s) {
        this.votingSystem = s;
    }

    public boolean getGameStarted() {
        return this.gameStarted;

    }

    public void startGame() {
        initializeGameData();
        gameStarted = true;
        beginNextPhase();

    }


    public void generateRolesAndPhases() {

        // Might consider putting this into its own class. Initializes the Server of the game. At least Phases and Roles.

        //Initialize all phases
        AbstractPhase.getPhases().add(new CivillianPhase(this));
        AbstractPhase.getPhases().add(new MafiaPhase(this));
        AbstractPhase.getPhases().add(new DoctorPhase(this));

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

    private void removePlayer(Player id) {
        int pos = PlayerArraySearcher.SearchArray(playersInGame, id);
        playersInGame.remove(pos);

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

        for(AbstractPhase x : AbstractPhase.getPhases()) {
            System.out.println("Checking all roles of phase: " + x.getId());
            for(String role : x.getParticipatingRoles()) {
                System.out.println(role + " can participate in this phase");
            }
        }
    }






    public void beginNextRound() {
            if(checkVictoryConditions() == 1)
            {
                // Game over. Civillians win
            }
            else if (checkVictoryConditions() == -1) {
                // Game over. Mafia wins
            }
            else {
                gamePhases = AbstractPhase.getActivePhasesInOrder();
                beginNextRound();
        }

    }

    public void beginNextPhase() {
        if(!gamePhases.isEmpty()) {
            currentPhase = gamePhases.remove(0);
            System.out.println("Now beginning " + currentPhase.getId());
            currentPhase.onPhaseBegin();
        }
        else {
            beginNextRound();
        }
    }


    // These methods are here to add and remove players from different lists.
    // TODO: Add more general methods to add to list, so new methods for each seperate list does not need to be done

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


    // These methods serve as callbacks for the voting system.

    // Called when a voting is successful and a majority vote for one player has been made.

    public void voteComplete(Player target, Player[] performer) {
        currentPhase.performAction(performer, target);
        currentPhase.onPhaseEnd();
    }

    // Called when a vote failed, for example if time is out
    public void voteFailed() {

    }

    // Called when a voting is not successful, and a new vote has to be done
    public void reVote() {


    }

    // Checks the victory conditions of the game and returns a corresponding integer. 1 is returned
    // if civillian wins, -1 returns if mafia wins and 0 is returned if no victory conditions have
    // been fulfilled.

    public int checkVictoryConditions() {
        int mafiaInGame = 0;
        int civillianInGame = 0;
        for(Player p : playersInGame) {
            if(p.getRole().getTeam() == "mafia") {
                mafiaInGame += 1;
            }
            if(p.getRole().getTeam() == "civillian") {
                civillianInGame +=1;
            }

        }
        if(mafiaInGame == 0) {
            return 1;
        }

        else if(mafiaInGame >= civillianInGame) {
            return -1;
        }

        else {
            return 0;
        }



    }


    // For each phase in the game, get the roles connected through that game.
    // Explicitly add that phase to the role for future access.
    // This is done in this way to maintain concurrency between roles and phases connectivity.
    // And to ease the process of enabling or disabling both roles and phases.
    private void connectRolesToPhase() {

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

