package progark.mafia.mafiagame.utils;

import java.util.Comparator;

import progark.mafia.mafiagame.models.Phases.AbstractPhase;

/**
 * Created by Daniel on 15.04.2015.
 */
public class ComparePhases implements Comparator<AbstractPhase> {

    public int compare(AbstractPhase o1, AbstractPhase o2) {
        if(o1.getOrder() > o2.getOrder()) {
            return 1;
        }
        if(o1.getOrder() < o2.getOrder()) {
            return -1;
        }
        else {
            return 0;
        }

    }

}
