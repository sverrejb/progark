package progark.mafia.mafiagame.models.Roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import progark.mafia.mafiagame.controller.GameLogic;

/**
 * Created by Daniel on 10.03.2015.
 */
public abstract class AbstractRole {

    GameLogic gl;
    String id;
    String displayName;

    int numberInPlay;
    int maxNumber = 1;

    static ArrayList<AbstractRole> roles = new ArrayList<AbstractRole>();

    static Map<String, AbstractRole> roleMap = new HashMap<String, AbstractRole>();

    // The value stores all phases that this particular role is used in
    ArrayList<String> usedIn = new ArrayList<>();

    boolean enabled;
    boolean mandatory;

    public int getNumberInPlay() {
        return numberInPlay;
    }

    public int getMax_number() {
        return maxNumber;
    }

    public void increaseNumberInPlay() {
        numberInPlay += 1;
    }

    public void decreaseNumberInPlay() {
        if(numberInPlay <= 0) {
            numberInPlay -= 1;
        }
    }

    AbstractRole(GameLogic gl, String id, String displayName) {
        this.gl = gl;
        this.id = id;
        this.displayName = displayName;
        roles.add(this);
        System.out.println("Creating entry for " + id + "in maps");
        roleMap.put(id, this);
    }

    public AbstractRole(GameLogic gl) {
        this.gl = gl;
        roles.add(this);


    }

    public void attachToMap(String id) {
        System.out.println("Adding record for role: " + id);
        roleMap.put(id, this);
    }

    public Map<String,AbstractRole> getRoleMap() {
        return roleMap;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static ArrayList<AbstractRole> getRoles() {
        return AbstractRole.roles;
    }

    public ArrayList<String> getPhases() {
        return this.usedIn;
    }

    public static Map<String, AbstractRole> getMap() {
        return AbstractRole.roleMap;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() throws Exception {
        if(!mandatory) {
            this.enabled = false;
        }
        else {
            throw new Exception("Attempt to disable mandatory role. This is not allowed");
        }
    }

    // Creates a copy of the sub-class and returns it.
    abstract void createCopy();






}
