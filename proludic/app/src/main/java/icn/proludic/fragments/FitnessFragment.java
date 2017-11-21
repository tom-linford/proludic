package icn.proludic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import icn.proludic.DashboardActivity;
import icn.proludic.R;
import icn.proludic.misc.Utils;

import static icn.proludic.misc.Constants.ADD_WORKOUT_FRAGMENT_TAG;
import static icn.proludic.misc.Constants.BROWSE_FRAGMENT_TAG;
import static icn.proludic.misc.Constants.EXERCISES_KEY;
import static icn.proludic.misc.Constants.NO_PARK_TYPE;
import static icn.proludic.misc.Constants.WORKOUTS_KEY;

/**
 * Author:  Bradley Wilson
 * Date: 11/04/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class FitnessFragment extends Fragment {

    Utils utils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);
        utils = new Utils(getActivity());
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        TextView btn_exercises = (TextView) view.findViewById(R.id.exercises_title);
        TextView btn_workouts = (TextView) view.findViewById(R.id.workouts_title);
        FrameLayout btn_create_routine = (FrameLayout) view.findViewById(R.id.create_routine);
        btn_exercises.setOnClickListener(customListener);
        btn_workouts.setOnClickListener(customListener);
        btn_create_routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((DashboardActivity) getActivity()).getParkType().equals(NO_PARK_TYPE)) {
                    utils.makeText("You must be at a park to create a workout.", Toast.LENGTH_SHORT);
                } else {
                    AddWorkoutFragment frag = new AddWorkoutFragment();
                    FragmentTransaction trans;
                    trans = getFragmentManager().beginTransaction();
                    trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out, R.anim.slide_in_left, R.anim.slide_out);
                    trans.addToBackStack(ADD_WORKOUT_FRAGMENT_TAG);
                    trans.replace(R.id.dashboard_fragment_container, frag);
                    trans.commit();
                }
            }
        });
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BrowseFragment frag = new BrowseFragment();
            FragmentTransaction trans;
            Bundle bundle = new Bundle();
            trans = getFragmentManager().beginTransaction();
            switch (v.getId()) {
                case R.id.exercises_title:
                    trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out, R.anim.slide_in_left, R.anim.slide_out);
                    trans.addToBackStack(BROWSE_FRAGMENT_TAG);
                    bundle.putString(BROWSE_FRAGMENT_TAG, EXERCISES_KEY);
                    break;
                case R.id.workouts_title:
                    trans.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_right, R.anim.slide_in, R.anim.slide_out_right);
                    trans.addToBackStack(BROWSE_FRAGMENT_TAG);
                    bundle.putString(BROWSE_FRAGMENT_TAG, WORKOUTS_KEY);
                    break;
            }
            frag.setArguments(bundle);
            trans.replace(R.id.dashboard_fragment_container, frag);
            trans.commit();
        }
    };

    public static FitnessFragment newInstance(int position) {
        final FitnessFragment f = new FitnessFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", position);
        f.setArguments(args);
        return f;
    }
}
