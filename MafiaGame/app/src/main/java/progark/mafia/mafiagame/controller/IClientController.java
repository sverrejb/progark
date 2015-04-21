package progark.mafia.mafiagame.controller;

import progark.mafia.mafiagame.connection.DuplexCommunicator;

/**
 * Created by Perÿyvind on 21/04/2015.
 */
public interface IClientController {


    String getRole();
    void informSoftVote(String cSoftVote);

    DuplexCommunicator getCommunicator();
}
