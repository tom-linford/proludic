package icn.proludic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 16/05/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class ForumFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    public static ForumFragment newInstance(int position) {
        final ForumFragment f = new ForumFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", position);
        f.setArguments(args);
        return f;
    }
}
