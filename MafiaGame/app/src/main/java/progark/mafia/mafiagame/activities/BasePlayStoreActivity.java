package progark.mafia.mafiagame.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
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

import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.utils.Constants;

/**
 * Created by PerØyvind on 23/03/2015.
 *
 * Handles main Play Store connection
 *
 */
public abstract class BasePlayStoreActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RoomStatusUpdateListener,
        IPlayStoreActivity {

    private static final String TAG = BasePlayStoreActivity.class.getSimpleName();

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    protected boolean mClientIsConnected = false;

    /**
     * The Game room, null if we are not playing.
     */
    private String mRoomId = null;

    // The participants in the currently active game
    private ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    private String mMyId = null;

    DuplexCommunicator duplexCommunicator;

    GameLogic gameLogic;

    boolean isServer = true;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                            // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();

        duplexCommunicator = new DuplexCommunicator(mGoogleApiClient);

    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        Log.v(TAG, "onStop: client disconnected.");
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
            case Constants.REQUEST_CODE_SELECT_PLAYERS:
                if (resultCode != Activity.RESULT_OK) {
                    Log.v(TAG, "User cancelled");
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
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder(duplexCommunicator);
                roomConfigBuilder.addPlayersToInvite(invitees);
                if (autoMatchCriteria != null) {
                    roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
                }
                RoomConfig roomConfig = roomConfigBuilder.build();

                Log.v(TAG, "Invited " + invitees.size() + " players. Now setting up room");
                Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                break;
            case Constants.REQUEST_CODE_WAITING_ROOM:
                if (resultCode == Activity.RESULT_OK) {
                    Log.v(TAG, "Time to start the game!");
                    //todo swtich fragment to game fragment
                    // (start game)

                    gameLogic = new GameLogic(this, duplexCommunicator, isServer);
                }

                else if (resultCode == Activity.RESULT_CANCELED) {
                    // Waiting room was dismissed with the back button. The meaning of this
                    // action is up to the game. You may choose to leave the room and cancel the
                    // match, or do something else like minimize the waiting room and
                    // continue to connect in the background.

                    // in this example, we take the simple approach and just leave the room:
                    Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player wants to leave the room.
                    // todo "this" could to be null
                    Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                break;
        }
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder(DuplexCommunicator duplexCommunicator) {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(duplexCommunicator)
                .setRoomStatusUpdateListener(this);
    }

    // --------------------------------------------------
    // Connection callbacks to Google Play API
    // --------------------------------------------------

    private void retryConnecting() {
        mIsInResolution = false;

        if (!mGoogleApiClient.isConnecting()) {
            Log.v(TAG, "retryConnecting(): Trying to connect");
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");

        // TODO: Start making API requests.
        mClientIsConnected = true;

        // Are we allready invited?
        if (connectionHint != null) {
            Invitation inv =
                    connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);

            if (inv != null) {
                isServer = false;
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder(duplexCommunicator);
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());

                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        mClientIsConnected = false;
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        mClientIsConnected = false;

        switch (result.getErrorCode()){
            case ConnectionResult.SIGN_IN_REQUIRED:
                Log.v(TAG, "Sign in required.. Is this debug? Then need to add you!");
                break;
        }

        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }

        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }

        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    // --------------------------------------------------
    // Callbacks for fragments
    // --------------------------------------------------

    @Override
    public boolean getConnectionStatus() {
        return mClientIsConnected;
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    // --------------------------------------------------
    // Room updates callbacks
    // --------------------------------------------------

    @Override
    public void onRoomCreated(int statusCode, Room room) {

        switch (statusCode){
            case GamesStatusCodes.STATUS_OK:
                // let screen go to sleep
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                Log.v(TAG, "onRoomCreated: Creating waiting room");

                // get waiting room intent
                Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
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
                Log.v(TAG, "onJoinedRoom: STATUS OK");
                // let screen go to sleep
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // get waiting room intent
                Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
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
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.v(TAG, "\"Left Room\"");
                // todo return to main menu
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
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                updateRoom(room);

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
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        // get room ID, participants and my ID:
        mRoomId = room.getRoomId();
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
// print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        mRoomId = null;
        //todo return to main screen.
    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onP2PConnected(String s) {}

    @Override
    public void onP2PDisconnected(String s) {}


    // --------------------------------------------------
    // Other helper methods
    // --------------------------------------------------

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        //if (mParticipants != null) {
        //    updatePeerScoresDisplay();
        //}
    }
}
