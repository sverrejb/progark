package progark.mafia.mafiagame.utils;

import java.util.Map;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class Event  {

    static Map<String, Event> eventMap;

    String eventID;
    String targetID;
    String senderID;
    //Add timestamp that can be used for messages?

    public abstract void receiveEvent();
    public abstract void sendEvent();
    public abstract void serialize();
    public abstract void deserialize();





}
