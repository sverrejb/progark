package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.util.Log;

import java.lang.ref.WeakReference;

import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.fragments.GameFragment;

/**
 * Created by Perÿyvind on 21/04/2015.
 */
public class ClientController {
    private static final String TAG = ClientController.class.getSimpleName();


    String serverId;

    String role;

    WeakReference<Activity> activity;

    DuplexCommunicator duplexCommunicator;

    public ClientController(Activity activity, DuplexCommunicator duplexCommunicator){
        this.activity = new WeakReference<>(activity);
        this.duplexCommunicator = duplexCommunicator;
    }

    /**
     * Called when all is ready
     */
    public void start(){
        Log.v(TAG, "Client is starting!");
        GameFragment gameFragment = new GameFragment();

        FragmentTransaction transaction = activity.get().getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_placeholder, gameFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    public void setRole(String role) {
        this.role = role;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
