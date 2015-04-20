package progark.mafia.mafiagame.controller;

import java.util.ArrayList;

import progark.mafia.mafiagame.controller.GameLogic;
import progark.mafia.mafiagame.models.Phases.AbstractPhase;
import progark.mafia.mafiagame.models.Player;
import progark.mafia.mafiagame.models.Vote;

/**
 * Created by Magnus on 20.04.2015.
 */
public class VotingSystem{

    ArrayList<Player> playersAlive;
    ArrayList<AbstractPhase> phases;
    AbstractPhase phase;
    GameLogic gl;

    ArrayList<Player> voters;
    ArrayList<Vote> votes;
    int[] voteCount;

    public VotingSystem(GameLogic gl, ArrayList playersInGame, ArrayList phases, AbstractPhase phase){
        this.gl = gl;
        this.playersAlive = playersInGame;
        this.phases = phases;
        this.phase = phase;

        voters = new ArrayList<>();
        votes = new ArrayList<>();
        voteCount = new int[this.playersAlive.size()];
    }

    // List of players in, list of eligible players out
    private void sortVotersOnCurrentPhase(ArrayList<Player> playersAlive){

        for (int i = 0; i < playersAlive.size(); i++){
            if(phase.getParticipatingRoles().contains(playersAlive.get(i).getRole()))
                voters.add(playersAlive.get(i));
        }

    }

    private void countVotes(){

        int majority = 0;
        int majoritiesIndex = 0;

        if(votes.size()>1)
            gl.voteComplete(votes.get(0).getTarget(), votes.get(0).getPerformer());

        else{
            for(int i = 0; i < votes.size(); i++){
                voteCount[playersAlive.indexOf(votes.get(i).getTarget())] += 1;
            }

            for(int i = 0; i < voteCount.length; i++){
                if(voteCount[i] > majority){
                    majority = voteCount[i];
                    majoritiesIndex = i;
                }
                else if(voteCount[i] == majority)
                    System.out.print("Error: Vote Draw! Perform a revote.");
            }

            gl.voteComplete(playersAlive.get(majoritiesIndex), null);

        }

    }

    // push voting screen on all devices.
    private void initiateVoting(){

    }

    // wait on votes and initiate count when all votes have been received or time runs out.
    private void receiveVote(Vote vote){

        if(voters.contains(vote.getPerformer())){
            votes.add(vote);
            voters.remove(vote.getPerformer());
        }

    }

}