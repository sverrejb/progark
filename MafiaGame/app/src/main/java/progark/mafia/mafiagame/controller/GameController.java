package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.connection.Event;
import progark.mafia.mafiagame.connection.IMessageListener;


/**
 * Created by Perÿyvind on 21/04/2015.
 */
public class GameController implements IMessageListener{

    public static final String TAG = GameController.class.getSimpleName();

    // WeakRef so avoid weird mem leaks
    private WeakReference<Activity> parentActivity;

    private DuplexCommunicator communicator;

    private GameLogic gameLogic;

    public GameController(Activity parent, DuplexCommunicator communicator, boolean isServer){
        parentActivity = new WeakReference<>(parent);
        this.communicator = communicator;
        this.communicator.addMessageListener(this);

        System.out.println("Participants:");
        for (int i = 0; i < this.communicator.getParticipants().size(); i++) {
            Log.v(TAG, this.communicator.getParticipants().get(i).getDisplayName());
        }

        if (isServer) {// Do stuff
            Log.v(TAG, "IAM SERVER");
            gameLogic = new GameLogic(this.communicator);

            // msg example..
//            Event e = new Event();
//            e.msg = "lol";
//
//            this.communicator.sendMessageToAll(e);
        }


//        // Setup view
//
//        GameFragment gameFragment = new GameFragment();
//
//        FragmentTransaction transaction = parentActivity.get().getFragmentManager().beginTransaction();
//
//        // Replace whatever is in the fragment_container view with this fragment,
//        // and add the transaction to the back stack if needed
//        transaction.replace(R.id.fragment_placeholder, gameFragment);
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        transaction.commit();
    }

    @Override
    public void OnEventReceived(Event e) {
        // Check type and pipe forward.
        // If GameLogic then also server

    }

    public DuplexCommunicator getCommunicator() {
        return communicator;
    }
}
