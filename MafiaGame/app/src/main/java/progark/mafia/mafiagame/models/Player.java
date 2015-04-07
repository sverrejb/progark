package progark.mafia.mafiagame.models;

import progark.mafia.mafiagame.models.Roles.AbstractRole;

/**
 * Created by Daniel on 10.03.2015.
 */
public class Player {

    String id;
    String name;
    AbstractRole role;


    public Player(String id, String name) {
        this.name = name;
        this.id = id;

    }

    public void assignRole(String role) {
        this.role = AbstractRole.getMap().get(role);
    }

}
