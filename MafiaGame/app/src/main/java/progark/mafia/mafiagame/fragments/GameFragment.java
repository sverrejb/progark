package progark.mafia.mafiagame.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;

import java.util.ArrayList;

import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.activities.IPlayStoreActivity;
import progark.mafia.mafiagame.controller.IClientController;
import progark.mafia.mafiagame.utils.Constants;

/**
 * Created by PerØyvind on 23/03/2015.
 */
public class GameFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = GameFragment.class.getSimpleName();

    Button btnShowRole;

    Button btnSendVote;


    LinearLayout voteLayout;

    TextView txtVote;

    TextView txtSoftVotes;
    TextView txtSoftVotesInfo;

    IClientController clientController;

    String currentSoftVote = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        btnShowRole = (Button)view.findViewById(R.id.btnShowRole);
        txtVote = (TextView)view.findViewById(R.id.txtVoteTxt);
        voteLayout = (LinearLayout)view.findViewById(R.id.linearLayoutVote);
        btnSendVote = (Button)view.findViewById(R.id.btnSendVote);
        txtSoftVotes = (TextView)view.findViewById(R.id.txtSoftVote);
        txtSoftVotesInfo = (TextView)view.findViewById(R.id.txtSoftVoteInfo);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach");

        super.onAttach(activity);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");

        btnSendVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSoftVote != null) {
                    clientController.informVotedOn(currentSoftVote);
                    btnSendVote.setVisibility(View.GONE);
                    txtVote.setVisibility(View.GONE);
                    voteLayout.setVisibility(View.GONE);
                    currentSoftVote = null;
                    showSoftVotes(null);
                }
            }
        });


        btnShowRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), clientController.getRole(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShowRole:

                break;
        }

    }


    public void voteOn(ArrayList<String> toVoteIds){
        txtVote.setVisibility(View.VISIBLE);
        voteLayout.setVisibility(View.VISIBLE);

        Log.v(TAG, "voteOn");
        voteLayout.removeAllViewsInLayout();

        for (int i = 0; i < toVoteIds.size(); i++) {
            String name = clientController.getCommunicator().getNameFromId(toVoteIds.get(i));
            addButton(toVoteIds.get(i), name);
        }
    }

    public void showSoftVotes(ArrayList<String> sVotes) {
        if(sVotes == null){
            txtSoftVotesInfo.setVisibility(View.GONE);
            txtSoftVotes.setVisibility(View.GONE);
            txtSoftVotes.setText("");
        } else {

            String txt = "";
            for(String s : sVotes)
                txt += s + "\n";

            txtSoftVotesInfo.setVisibility(View.VISIBLE);
            txtSoftVotes.setVisibility(View.VISIBLE);
            txtSoftVotes.setText(txt);
        }
    }


    private void addButton(final String id, final String name){
        Button myButton = new Button(getActivity());
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSendVote.setVisibility(View.VISIBLE);
                btnSendVote.setText("Send vote on: " + name);
                clientController.informSoftVote(id);
                currentSoftVote = id;
            }
        });
        myButton.setText(name);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        voteLayout.addView(myButton, lp);
    }

    public void setClientController(IClientController clientController) {
        this.clientController = clientController;
    }
}
