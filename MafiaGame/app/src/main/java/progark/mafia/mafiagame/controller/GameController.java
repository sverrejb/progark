package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.app.FragmentTransaction;

import java.lang.ref.WeakReference;

import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.connection.Event;
import progark.mafia.mafiagame.connection.IMessageListener;
import progark.mafia.mafiagame.connection.SYUPALOl;
import progark.mafia.mafiagame.fragments.GameFragment;

/**
 * Created by Perÿyvind on 21/04/2015.
 */
public class GameController implements IMessageListener{

    // WeakRef so avoid weird mem leaks
    private WeakReference<Activity> parentActivity;

    private DuplexCommunicator communicator;

    private GameLogic gameLogic;

    public GameController(Activity parent, DuplexCommunicator communicator, boolean isServer){
        parentActivity = new WeakReference<>(parent);
        this.communicator = communicator;
        this.communicator.addMessageListener(this);

        if (isServer) {// Do stuff
            gameLogic = new GameLogic();


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





    @Override
    public void OnEventReceived(Event e) {

        if(e instanceof SYUPALOl){
            gameLogic.SNUBLISCHANBLIIBIBIF((SYUPALOl)e);
        }

    }

    public DuplexCommunicator getCommunicator() {
        return communicator;
    }
}
