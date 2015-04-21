package progark.mafia.mafiagame.connection;

import java.io.Serializable;

/**
 * Created by PerØyvind on 23/03/2015.
 */
public class Event implements Serializable {

    public Type type;

    public String fieldOne;
    public String[] fieldTwo;

    public enum Type {
        SETUP, ROLE, VOTE, COMMIT, VOTED, SOFTVOTE, VICTORY
    }
}
