package models.Roles;

import java.util.ArrayList;

import controller.GameLogic;
import models.Phases.AbstractPhase;
import models.Player;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class AbstractRole {

    GameLogic gl;
    String id;
    String displayName;
    ArrayList<AbstractRole> roles = new ArrayList<AbstractRole>();

    // This is a special value and if set to true requires that a performer is not null when
    // performing an action

    boolean notePerformer;

    public AbstractRole(GameLogic gl, String id, String displayName) {
        this.gl = gl;
        this.id = id;
        this.displayName = displayName;
        roles.add(this);
    }

    public AbstractRole(GameLogic gl) {
        this.gl = gl;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ArrayList<AbstractRole> getRoles() {
        return this.roles;
    }

    // Executes the sub-class-specific action.
    // performer is the player who has voted to perform the action.
    // target is the targeted (the one with most votes) player.
    void performAction(Player performer, Player target) {
        ensurePerformerNotNull(performer);
    }

    // Creates a copy of the sub-class and returns it.
    abstract void createCopy();

    public void ensurePerformerNotNull(Player performer) {
        if(notePerformer == true && performer == null) {
            throw new NullPointerException("Error in Role: " + this.id +
                    "Performer can not be null for a performer-required action");
        }
    }




}
