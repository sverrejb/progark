package progark.mafia.mafiagame.utils;

import java.util.ArrayList;

import progark.mafia.mafiagame.models.Player;

/**
 * Created by Daniel on 15.04.2015.
 */ // Helperclass for searching an array for a specific player
public class PlayerArraySearcher {

    public static int SearchArray(ArrayList<Player> array, Player find) {
        for(int i = 0; i < array.size(); i++) {
            if(array.get(i).getId().equals(find.getId())) {
                return i;
            }
        }
        return -1;
    }
}
