package progark.mafia.mafiagame.connection;

import android.util.Log;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

/**
 * Created by Perÿyvind on 23/03/2015.
 */
public class MessageReceiver implements RealTimeMessageReceivedListener {
    private static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] data = realTimeMessage.getMessageData();

        Log.v(TAG, "DATA RECEIVED: " + data);
    }
}
