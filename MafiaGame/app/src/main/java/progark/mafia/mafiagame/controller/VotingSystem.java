package progark.mafia.mafiagame.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
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

    List<Player> performers;

    public VotingSystem(GameLogic gameLogic, ArrayList<Player> playersInGame, AbstractPhase phase){
        this.playersAlive = playersInGame;
        this.phase = phase;
        gl = gameLogic;

        whoCanVote = new HashMap<>();
        votes = new HashMap<>();
        whatWasVoted = new HashMap<>();

        sortVotersOnCurrentPhase();
        initiateVoting();

        performers = new ArrayList<>();

    }

    // List of players in, list of eligible players out
    private void sortVotersOnCurrentPhase(){
        Log.v(TAG, "Sorting roles");

        for (int i = 0; i < phase.getParticipatingRoles().size(); i++) {
            Log.v(TAG, "Phase: " + phase.getParticipatingRoles().get(i));
        }

        for (int i = 0; i < playersAlive.size(); i++){
            if(phase.getParticipatingRoles().contains("all") || // All alive are joining..
                    phase.getParticipatingRoles().contains(playersAlive.get(i).getRole().getId())) { // Only specific are joining
                whoCanVote.put(playersAlive.get(i).getId(), playersAlive.get(i));
                Log.v(TAG, "Can vote: " + playersAlive.get(i).getRole().getDisplayName() + " " + playersAlive.get(i).getName());
                performers.add(playersAlive.get(i));
            }

            // adds all player IDs to the hash map
            votes.put(playersAlive.get(i).getId(), 0);
        }
    }

    private void countVotes(){

        Player[] arrayOfPerformers = new Player[performers.size()];
        arrayOfPerformers = performers.toArray(arrayOfPerformers);

        Log.v(TAG, "Counting votes");
        int highest = -1;
        String targetID = "";

        for(String k : votes.keySet()){
            Log.v(TAG, k + ": " + votes.get(k));
        }

        for(String k : votes.keySet()){
            if(votes.get(k) > highest) {
                highest = votes.get(k);
                targetID = k;
            }
        }

        Log.v(TAG, "Vote done: " + targetID);

        gl.voteComplete(targetID, arrayOfPerformers);

        gl.beginNextPhase();
    }

    // sends a message to all clients with a list with the IDs to all who can vote
    private void initiateVoting(){
        Log.v(TAG, "Initiate voting");

        String[] canVote = new String[whoCanVote.size()];

        int c = 0;
        for (String s : whoCanVote.keySet()) {
            canVote[c++] = s;
            Log.v(TAG, "Players who can vote: " + s);
        }

        for (String s : whoCanVote.keySet()) {
            Event e = new Event();
            e.type = Event.Type.VOTE;
            e.fieldTwo = canVote;

            gl.getCommunicator().sendMessageTo(e, s);
        }
        Log.v(TAG, "Voters informed");
    }

    // wait on votes and initiate count when all votes have been received.
    public void receiveVote(Event vote){

        // fieldOne: the target of the vote
        // [] fieldTwo: index 0 = the vote performer

        if(votes.containsKey(vote.fieldOne) && whoCanVote.size() > 0){
            votes.put(vote.fieldOne, votes.get(vote.fieldOne) + 1);
            whoCanVote.remove(vote.fieldTwo[0]);
            // If no more voters then count the votes
            if(whoCanVote.size() <= 0)
                countVotes();
        }

    }

}