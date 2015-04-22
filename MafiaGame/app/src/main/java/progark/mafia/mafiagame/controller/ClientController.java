package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import progark.mafia.mafiagame.GameOverFragment;
import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.connection.DuplexCommunicator;
import progark.mafia.mafiagame.connection.Event;
import progark.mafia.mafiagame.fragments.GameFragment;

/**
 * Created by Per�yvind on 21/04/2015.
 */
public class ClientController implements IClientController{
    private static final String TAG = ClientController.class.getSimpleName();

    private String serverId;

    private String role;

    private WeakReference<Activity> activity;

    private DuplexCommunicator duplexCommunicator;

    String me;

    GameFragment gameFragment;

    // Alive participants for this round. Set to all start players minus your self.
    private ArrayList<String> aliveParticipantsIds;

    private boolean isCivilianPhase = false;

    public ClientController(Activity activity, DuplexCommunicator duplexCommunicator){
        this.activity = new WeakReference<>(activity);
        this.duplexCommunicator = duplexCommunicator;

        me = duplexCommunicator.getMe();
        aliveParticipantsIds = new ArrayList<>();

        for (int i = 0; i < this.duplexCommunicator.getParticipants().size(); i++) {
            if(!this.duplexCommunicator.getParticipants().get(i).getParticipantId().equals(me)){
                aliveParticipantsIds.add(this.duplexCommunicator.getParticipants().get(i).getParticipantId());
            }

        }
    }

    ArrayList<String> playersToVoteOn;
    String[] idPhaseTeamMates;
    public void startVotingProcess(String[] idPhaseTeamMates){
        this.idPhaseTeamMates = idPhaseTeamMates;
        otherPlayersSoftVote = new HashMap<>();

        // Set players to vote on. Everyone in aliveParticipantsIds minus idPhaseTeamMates
        playersToVoteOn = new ArrayList<>(aliveParticipantsIds);
        for (int i = 0; i < idPhaseTeamMates.length; i++) {
            playersToVoteOn.remove(idPhaseTeamMates[i]);
        }

        // Update gui
        gameFragment.voteOn(playersToVoteOn);
    }

    Map<String, String> otherPlayersSoftVote;
    public void otherPlayersSoftVote(String playerId, String playerVoteOn){
        otherPlayersSoftVote.put(playerId, playerVoteOn);

        // todo update gui
    }

    /**
     * We have chosen our vote and send it to the server.
     *
     * @param id
     */
    public void informVotedOn(String id){
        //gui updated itself

        Event e = new Event();
        e.type = Event.Type.VOTED;
        e.fieldOne = id;
        e.fieldTwo = new String[1];
        e.fieldTwo[0] = getCommunicator().getMe();
        duplexCommunicator.sendMessageTo(e, serverId);


        // tear down data
        this.idPhaseTeamMates = null;
        otherPlayersSoftVote = null;
        playersToVoteOn = null;
    }

    public void informSoftVote(String cSoftVote){
        for (int i = 0; i < this.idPhaseTeamMates.length; i++) {
            // Do not inform soft vote to youreself
            if(this.idPhaseTeamMates[i].equals(this.duplexCommunicator.getMe()))
                continue;

            Event e = new Event();
            e.type = Event.Type.SOFTVOTE;
            e.fieldTwo = new String[2];
            e.fieldTwo[0] = this.duplexCommunicator.getMe();
            e.fieldTwo[1] = cSoftVote;
            this.duplexCommunicator.sendMessageTo(e, this.idPhaseTeamMates[i]);
        }
    }

    @Override
    public DuplexCommunicator getCommunicator() {
        return duplexCommunicator;
    }


    public void commit(String[] killed) {
        isCivilianPhase = !isCivilianPhase; // After first time receive then it is civilian phase


        // Remove killed
        for (int i = 0; i < killed.length; i++) {
            aliveParticipantsIds.remove(killed[0]);
        }

        if(isCivilianPhase){
            // todo start grafisk
            // todo er implisitt vote process. Returner hvem man velger.
        }
    }

    public void victory(String winnerTeam){
        GameOverFragment gameOverFragment = GameOverFragment.newInstance(winnerTeam);

        gameOverFragment.setClientController(this);

        FragmentTransaction transaction = activity.get().getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, gameOverFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        gameFragment = null;
    }


    /**
     * Called when all is ready
     */
    public void start(){
        Log.v(TAG, "Client is starting!");
        gameFragment = new GameFragment();

        gameFragment.setClientController(this);

        FragmentTransaction transaction = activity.get().getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_placeholder, gameFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    // ===============================
    // Getters and setters
    // ===============================

    public void setRole(String role) {
        this.role = role;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String getRole() {
        return role;
    }
}
