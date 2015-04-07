package progark.mafia.mafiagame.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.util.ArrayList;
import java.util.List;

import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.activities.IPlayStoreActivity;
import progark.mafia.mafiagame.utils.Constants;

/**
 * Created by Perÿyvind on 23/03/2015.
 */
public class MainMenuFragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = MainMenuFragment.class.getSimpleName();

    IPlayStoreActivity mPlayStoreActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_menu, container, false);

        ((Button)view.findViewById(R.id.btnFindPlayers)).setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPlayStoreActivity = (IPlayStoreActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + IPlayStoreActivity.class.getSimpleName());
        }

    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFindPlayers:
                if(!mPlayStoreActivity.getConnectionStatus()) {
                    Log.v(TAG, "Google API not connected");
                    return;
                }

                // launch the player selection screen
                // minimum: 1 other player; maximum: 3 other players
                Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mPlayStoreActivity.getGoogleApiClient(), 1, 3);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_PLAYERS);
                break;
        }

    }

}
