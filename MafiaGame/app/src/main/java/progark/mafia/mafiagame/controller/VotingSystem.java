package progark.mafia.mafiagame.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import progark.mafia.mafiagame.connection.Event;
import progark.mafia.mafiagame.models.Phases.AbstractPhase;
import progark.mafia.mafiagame.models.Player;

/**
 * Created by Magnus on 20.04.2015.
 */
public class VotingSystem{
    private static final String TAG = VotingSystem.class.getSimpleName();

    ArrayList<Player> playersAlive;         // All players that are still alive
    AbstractPhase phase;                    // The phase the game is in
    GameLogic gl;

    HashMap<String, Player> whoCanVote;

    HashMap<String, Integer> votes;         // vote count
    HashMap<String, String> whatWasVoted;   // Who voted on who

    public VotingSystem(GameLogic gameLogic, ArrayList<Player> playersInGame, AbstractPhase phase){
        this.playersAlive = playersInGame;
        this.phase = phase;
        gl = gameLogic;

        whoCanVote = new HashMap<>();
        votes = new HashMap<>();
        whatWasVoted = new HashMap<>();

        sortVotersOnCurrentPhase();
        initiateVoting();

    }

    // List of players in, list of eligible players out
    private void sortVotersOnCurrentPhase(){

        for (int i = 0; i < playersAlive.size(); i++){
            if(phase.getParticipatingRoles().contains(playersAlive.get(i).getRole().getId())) {
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
        Log.v(TAG, "Vote done: " + h);
        // TODO NULL?
        gl.voteComplete(h, null);    // needs to send the player that got the majority of the votes
                                                // null if more than one player voted, if only one player voted "doctor" he will be in the array..
        gl.beginNextPhase();
    }

    // sends a message to all clients with a list with the IDs to all who can vote
    private void initiateVoting(){

        String[] canVote = new String[whoCanVote.size()];

        int c = 0;
        for (String s : whoCanVote.keySet()) {
            canVote[c++] = s;
        }

        for (String s : whoCanVote.keySet()) {
            Event e = new Event();
            e.type = Event.Type.VOTE;
            e.fieldTwo = canVote;

            gl.getCommunicator().sendMessageTo(e, s);
        }
    }

    // wait on votes and initiate count when all votes have been received or time runs out.
    public void receiveVote(Event vote){

        // fieldOne: the target of the vote
        // [] fieldTwo: index 0 = the vote performer

        if(votes.containsKey(vote.fieldOne) && whoCanVote.size() > 0){
            votes.put(vote.fieldOne, votes.get(vote.fieldOne + 1));
            whoCanVote.remove(vote.fieldTwo[0]);
            // todo what? countVotes når ferdig
            if(whoCanVote.size() > 0)
                countVotes();
        }
        else
            countVotes();

    }

}