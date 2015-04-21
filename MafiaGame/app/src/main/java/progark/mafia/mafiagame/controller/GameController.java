package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.connection.Event;
import progark.mafia.mafiagame.connection.IMessageListener;
import progark.mafia.mafiagame.models.Player;


/**
 * Created by Perÿyvind on 21/04/2015.
 */
public class GameController implements IMessageListener{

    public static final String TAG = GameController.class.getSimpleName();

    // WeakRef so avoid weird mem leaks
    private WeakReference<Activity> parentActivity;

    private DuplexCommunicator communicator;

    private GameLogic gameLogic;

    private ClientController clientController;

    public GameController(Activity parent, DuplexCommunicator communicator, boolean isServer){
        parentActivity = new WeakReference<>(parent);
        this.communicator = communicator;
        this.communicator.addMessageListener(this);

        // ===============================
        // Client setup
        // ===============================
        clientController = new ClientController(parent, communicator);

        // ===============================
        // Server setup
        // ===============================
        if (isServer) {// Do stuff
            Log.v(TAG, "IAM SERVER");
            gameLogic = new GameLogic(this.communicator);

            this.communicator.setGameController(this);

            // ===============================
            // Step 1 send setup
            // ===============================
            Event msg = new Event();
            msg.type = Event.Type.SETUP;
            msg.fieldOne = communicator.getMe();

            this.communicator.sendMessageToAll(msg);


            // ===============================
            // Step 2 Send assigned roles
            // ===============================
            ArrayList<Player> players = gameLogic.getPlayersInGame();
            for(Player p : players) {
                Event e = new Event();
                e.type = Event.Type.ROLE;
                e.fieldOne = p.getRole().getId();

                this.communicator.sendMessageTo(e, p.getId());
            }
        }
    }

    /**
     * Run after constructor
     */
    public void start(){

    }

    @Override
    public void OnEventReceived(Event e) {
        // Check type and pipe forward.
        // If GameLogic then also server

        Log.v(TAG, "OnEventReceived: " + e.type.name());

        switch (e.type){
            case SETUP:
                clientController.setServerId(e.fieldOne);
                break;
            case ROLE:
                clientController.setRole(e.fieldOne);
                // We are now ready
                clientController.start();
                break;
            case VOTE:
                clientController.startVotingProcess(e.fieldTwo);
                break;
            case SOFTVOTE:
                clientController.otherPlayersSoftVote(e.fieldTwo[0], e.fieldTwo[1]);
                break;
            case VOTED:
                // todo inform votesystem of vote
                break;
            case COMMIT:
                clientController.commit(e.fieldTwo);
                break;
            case VICTORY:
                clientController.victory(e.fieldOne);
                break;

        }

    }

    public DuplexCommunicator getCommunicator() {
        return communicator;
    }
}
