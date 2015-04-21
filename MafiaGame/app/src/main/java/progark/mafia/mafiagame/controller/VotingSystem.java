package progark.mafia.mafiagame.controller;

import java.util.ArrayList;
import java.util.HashMap;

import progark.mafia.mafiagame.connection.Event;
import progark.mafia.mafiagame.models.Phases.AbstractPhase;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Magnus on 20.04.2015.
 */
public class VotingSystem{

    ArrayList<Player> playersAlive;         // All players that are still alive
    AbstractPhase phase;                    // The phase the game is in
    GameLogic gl;

    HashMap<String, Player> whoCanVote;

    HashMap<String, Integer> votes;         // vote count
    HashMap<String, String> whatWasVoted;   // Who voted on who

    String target;

    public VotingSystem(GameLogic gameLogic, ArrayList playersInGame, AbstractPhase phase){
        this.playersAlive = playersInGame;
        this.phase = phase;
        gl = gameLogic;

        whoCanVote = new HashMap<>();
        votes = new HashMap<>();
        whatWasVoted = new HashMap<>();

        sortVotersOnCurrentPhase();
        initiateVoting();

        target = "";
    }

    // List of players in, list of eligible players out
    private void sortVotersOnCurrentPhase(){

        for (int i = 0; i < playersAlive.size(); i++){
            if(phase.getParticipatingRoles().contains(playersAlive.get(i).getRole())) {
                whoCanVote.put(playersAlive.get(i).getId(), playersAlive.get(i));
            }
            votes.put(playersAlive.get(i).getId(), 0); // adds all player IDs to the hashmap
        }

    }

    private void countVotes(){

        int highest = -1;
        String h = "";

        for(String k : votes.keySet()){
            if(votes.get(k)> highest) {
                highest = votes.get(k);
                h = k;
            }

        }

        gl.voteComplete(PlayerObject, null);    // needs to send the player that got the majority of the votes
                                                // null if more than one player voted, if only one player voted "doctor" he will be in the array..
    }

    // sends a message to all clients with a list with the IDs to all who can vote
    private void initiateVoting(){

    }

    // wait on votes and initiate count when all votes have been received or time runs out.
    private void receiveVote(Event vote){

        // fieldOne: the target of the vote
        // [] fieldTwo: index 0 = the vote performer

        if(votes.containsKey(vote.fieldOne) && whoCanVote.size() > 0){
            votes.put(vote.fieldOne, votes.get(vote.fieldOne + 1));
            whoCanVote.remove(vote.fieldTwo[0]);
            if(whoCanVote.size() > 0)
                countVotes();
        }
        else
            countVotes();

    }

}