package progark.mafia.mafiagame.utils;

import java.util.Random;

/**
 * Created by Daniel on 10.04.2015.
 */
public class Randomizer {

    static Random r = new Random();

    public static int getRandomInt(int x, int y) {
        return r.nextInt(y) + x;
    }
}
