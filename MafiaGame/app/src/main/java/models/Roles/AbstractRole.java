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

    // Creates a copy of the sub-class and returns it.
    abstract void createCopy();






}
