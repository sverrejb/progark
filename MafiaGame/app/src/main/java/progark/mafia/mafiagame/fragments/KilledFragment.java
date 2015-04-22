package progark.mafia.mafiagame.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import progark.mafia.mafiagame.R;
import progark.mafia.mafiagame.controller.IClientController;

/**
 * Created by PerØyvind on 23/03/2015.
 */
public class KilledFragment extends Fragment {

    private static final String TAG = KilledFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view =  inflater.inflate(R.layout.fragment_killed, container, false);

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

    }
}
