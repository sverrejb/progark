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

    IPlayStoreActivity mPlayStoreActivity;

    Button btnShowRole;


    IClientController clientController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        btnShowRole = (Button)view.findViewById(R.id.btnShowRole);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPlayStoreActivity = (IPlayStoreActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + IPlayStoreActivity.class.getSimpleName());
        }

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

    public void setClientController(IClientController clientController) {
        this.clientController = clientController;
    }
}
