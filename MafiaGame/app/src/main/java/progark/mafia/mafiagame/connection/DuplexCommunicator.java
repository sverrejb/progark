package progark.mafia.mafiagame.connection;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by PerØyvind on 23/03/2015.
 *
 * todo Make object on session created and give to gae logic?
 */
public class DuplexCommunicator implements RealTimeMessageReceivedListener {
    // todo, going to be only one listener..
    ArrayList<IMessageListener> mMessageListeners;

    GoogleApiClient googleApiClient;
    String roomId;
    ArrayList<Participant> participants;
    String me;



    /**
     *
     * @param googleApiClient
     * @param roomId
     * @param participants
     */
    public DuplexCommunicator(GoogleApiClient googleApiClient) {
        mMessageListeners = new ArrayList<>();
        this.googleApiClient = googleApiClient;
    }

    private static final String TAG = DuplexCommunicator.class.getSimpleName();

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] data = realTimeMessage.getMessageData();

        Event event = null;

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            event = (Event)in.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        if(event == null) return;

        for(IMessageListener listener : mMessageListeners) {
            listener.OnEventReceived(event);
        }
    }

    public void sendMessageToAll(Event e) {
        byte[] data = marshallEvent(e);

        if(data != null) {
            for (Participant p : participants) {
                sendMessage(data, p.getParticipantId());
            }
        } else
            System.err.println("Serious sendMessageToAllError");
    }

    public void sendMessageTo(Event e, String participant) {
        byte[] data = marshallEvent(e);

        sendMessage(data, participant);
    }

    private byte[] marshallEvent(Event e) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(e);

            return bos.toByteArray();

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return null;
    }

    private void sendMessage(byte[] data, String participantId){
        Games.RealTimeMultiplayer.sendReliableMessage(
                googleApiClient,
                null,
                data,
                roomId,
                participantId);
    }

    public void addMessageListener(IMessageListener messageListener){
        mMessageListeners.add(messageListener);
    }

    public void removeMessageListener(IMessageListener messageListener){
        mMessageListeners.remove(messageListener);
    }

    // --------------------------
    // Getters and setters
    // --------------------------


    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getMe() {
        return me;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }
}
