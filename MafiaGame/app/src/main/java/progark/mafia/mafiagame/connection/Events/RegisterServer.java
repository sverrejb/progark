package progark.mafia.mafiagame.connection.Events;

import progark.mafia.mafiagame.connection.Event;

/**
 * Created by Daniel on 21.04.2015.
 */
public class RegisterServer extends Event {

    public RegisterServer(Object data) {
        super("RegisterServer", data);
    }
}
