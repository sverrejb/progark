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
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.util.ArrayList;
import java.util.List;

import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.activities.IPlayStoreActivity;
import progark.mafia.mafiagame.connection.MessageReceiver;
import progark.mafia.mafiagame.utils.Constants;

/**
 * Created by Perÿyvind on 23/03/2015.
 */
public class MainMenuFragment extends Fragment implements
        View.OnClickListener,
        RoomUpdateListener,
        RoomStatusUpdateListener{

    private static final String TAG = MainMenuFragment.class.getSimpleName();

    IPlayStoreActivity mPlayStoreActivity;

    /**
     * The Game room, null if we are not playing.
     */
    String mRoomId = null;

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

        switch (requestCode) {

            case Constants.REQUEST_CODE_SELECT_PLAYERS:
                if (resultCode != Activity.RESULT_OK) {
                    // user canceled
                    return;
                }

                // get the invitee list
                Bundle extras = data.getExtras();
                final ArrayList<String> invitees =
                        data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // get auto-match criteria
                Bundle autoMatchCriteria = null;
                int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

                if (minAutoMatchPlayers > 0) {
                    autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                            minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                } else {
                    autoMatchCriteria = null;
                }

                // create the room and specify a variant if appropriate
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.addPlayersToInvite(invitees);
                if (autoMatchCriteria != null) {
                    roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
                }
                RoomConfig roomConfig = roomConfigBuilder.build();

                Log.v(TAG, "Invited " + invitees.size() + " players. Now setting up room");
                Games.RealTimeMultiplayer.create(mPlayStoreActivity.getGoogleApiClient(), roomConfig);

                // prevent screen from sleeping during handshake
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                break;
            case Constants.REQUEST_CODE_WAITING_ROOM:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v(TAG, "Time to start the game!");
                    //todo swtich fragment to game fragment
                    // (start game)
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    // Waiting room was dismissed with the back button. The meaning of this
                    // action is up to the game. You may choose to leave the room and cancel the
                    // match, or do something else like minimize the waiting room and
                    // continue to connect in the background.

                    // in this example, we take the simple approach and just leave the room:
                    Games.RealTimeMultiplayer.leave(mPlayStoreActivity.getGoogleApiClient(), null, mRoomId);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player wants to leave the room.
                    Games.RealTimeMultiplayer.leave(mPlayStoreActivity.getGoogleApiClient(), null, mRoomId);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                break;
        }
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(new MessageReceiver())
                .setRoomStatusUpdateListener(this);
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
                startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_PLAYERS);
                break;
        }

    }

    // --------------------------------------------------
    // Room updates callbacks
    // --------------------------------------------------


    @Override
    public void onRoomCreated(int statusCode, Room room) {

        switch (statusCode){
            case GamesStatusCodes.STATUS_OK:
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                Log.v(TAG, "onRoomCreated: Creating waiting room");

                // get waiting room intent
                Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mPlayStoreActivity.getGoogleApiClient(), room, Integer.MAX_VALUE);
                startActivityForResult(i, Constants.REQUEST_CODE_WAITING_ROOM);

                mRoomId = room.getRoomId();

                break;
            // Something is wrong
            default:
                // show error message, return to main screen.
                Log.e(TAG, "onRoomCreated: " + statusCode);
        }

    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        switch (statusCode){
            case GamesStatusCodes.STATUS_OK:
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


                // get waiting room intent
                Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mPlayStoreActivity.getGoogleApiClient(), room, Integer.MAX_VALUE);
                startActivityForResult(i, Constants.REQUEST_CODE_WAITING_ROOM);

                break;
            // Something is wrong
            default:
                // show error message, return to main screen.
                Log.e(TAG, "onJoinedRoom: " + statusCode);
        }
    }

    @Override
    public void onLeftRoom(int statusCode, String s) {
        switch (statusCode){
            case GamesStatusCodes.STATUS_OK:
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            // Something is wrong
            default:
                // show error message, return to main screen.
                Log.e(TAG, "onLeftRoom: " + statusCode);
        }
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        switch (statusCode){
            case GamesStatusCodes.STATUS_OK:
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            // Something is wrong
            default:
                // show error message, return to main screen.
                Log.e(TAG, "onRoomConnected: " + statusCode);
        }
    }


    // --------------------------------------------------
    // Room status changes
    // --------------------------------------------------

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {

    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {

    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {

    }

    @Override
    public void onConnectedToRoom(Room room) {

    }

    @Override
    public void onDisconnectedFromRoom(Room room) {

    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {

    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {

    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }
}
