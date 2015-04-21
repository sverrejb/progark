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

    LinearLayout voteLayout;

    TextView txtVote;


    IClientController clientController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        btnShowRole = (Button)view.findViewById(R.id.btnShowRole);
        txtVote = (TextView)view.findViewById(R.id.txtVoteTxt);
        voteLayout = (LinearLayout)view.findViewById(R.id.linearLayoutVote);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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



    public void voteOn(final String[] toVoteIds){
        voteLayout.removeAllViewsInLayout();

        for (int i = 0; i < toVoteIds.length; i++) {
            String name = clientController.getCommunicator().getNameFromId(toVoteIds[i]);
            addButton(toVoteIds[i], name);
        }
    }

    private void addButton(final String id, final String name){
        Button myButton = new Button(getActivity());
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientController.informSoftVote(id);
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
