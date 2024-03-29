package progark.mafia.mafiagame.controller;

import android.app.Activity;
import android.app.FragmentTransaction;
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
import progark.mafia.mafiagame.fragments.KilledFragment;

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

    private boolean isKilled = false;

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
        if(isKilled) return;

        this.idPhaseTeamMates = idPhaseTeamMates;
        otherPlayersSoftVote = new HashMap<>();

        playersToVoteOn = new ArrayList<>(aliveParticipantsIds);

        if(isCivilianPhase) {

        } else {
            // Set players to vote on. Everyone in aliveParticipantsIds minus idPhaseTeamMates
            for (int i = 0; i < idPhaseTeamMates.length; i++) {
                playersToVoteOn.remove(idPhaseTeamMates[i]);
            }
        }

        // Update gui
        gameFragment.voteOn(playersToVoteOn);
    }

    Map<String, String> otherPlayersSoftVote;
    public void otherPlayersSoftVote(String playerId, String playerVoteOn){
        if(isKilled) return;

        otherPlayersSoftVote.put(playerId, playerVoteOn);

        // update gui
        ArrayList<String> sVotes = new ArrayList<>();
        for(String s : otherPlayersSoftVote.values())
            sVotes.add(s);

        gameFragment.showSoftVotes(sVotes);
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
        if(isKilled) return;

        isCivilianPhase = !isCivilianPhase; // After first time receive then it is civilian phase

        // Remove killed
        for (int i = 0; i < killed.length; i++) {
            aliveParticipantsIds.remove(killed[0]);

            // Am I killed? If so show it.
            if(killed[i].equals(getCommunicator().getMe())){
                isKilled = true;
                KilledFragment killedFragment = new KilledFragment();

                FragmentTransaction transaction = activity.get().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_placeholder, killedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                gameFragment = null;
            }
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
