package models.Roles;

import controller.GameLogic;
import models.Phases.AbstractPhase;
import models.Player;

/**
 * Created by Daniel on 20.03.2015.
 */
public class Civillian extends AbstractRole {

    public Civillian(GameLogic gl) {
        super(gl);
        this.id = "civillian";
        this.displayName = "Civillian Phase";
        this.notePerformer = false;
    }

    void performAction(Player performer, Player target) {
        ensurePerformerNotNull(performer);

    }

    @Override
    void createCopy() {

    }
}
