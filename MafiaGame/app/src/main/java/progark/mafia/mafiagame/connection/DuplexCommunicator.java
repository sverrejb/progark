package progark.mafia.mafiagame.connection;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Perÿyvind on 23/03/2015.
 *
 * todo Make object on session created and give to gae logic?
 */
public class DuplexCommunicator implements RealTimeMessageReceivedListener {
    // todo, going to be only one listener..
    ArrayList<IMessageListener> mMessageListeners;

    GoogleApiClient googleApiClient;
    String roomId;
    Participant[] participants;

    /**
     *
     * @param googleApiClient
     * @param roomId
     * @param participants
     */
    public DuplexCommunicator(GoogleApiClient googleApiClient, String roomId, Participant[] participants) {
        mMessageListeners = new ArrayList<>();
    }

    private static final String TAG = DuplexCommunicator.class.getSimpleName();

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] data = realTimeMessage.getMessageData();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            Event e = (Event)objectInputStream.readObject();
            Log.v(TAG, "DATA RECEIVED: " + e);


            for(IMessageListener listener : mMessageListeners) {
                listener.OnEventReceived(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(Event e) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] data;

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(e);

            data = new byte[byteArrayOutputStream.size()];
            byteArrayOutputStream.write(data);

            for(Participant p : participants) {
                Games.RealTimeMultiplayer.sendReliableMessage(
                        googleApiClient,
                        null,
                        data,
                        roomId,
                        p.getParticipantId());
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void addMessageListener(IMessageListener messageListener){
        mMessageListeners.add(messageListener);
    }

    public void removeMessageListener(IMessageListener messageListener){
        mMessageListeners.remove(messageListener);
    }
}
