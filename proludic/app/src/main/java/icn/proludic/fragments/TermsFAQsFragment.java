package icn.proludic.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import icn.proludic.R;

import static icn.proludic.misc.Constants.TERMS_FAQS_FRAGMENT_TAG;
import static icn.proludic.misc.Constants.TERMS_KEY;

/**
 * Author:  Bradley Wilson
 * Date: 31/07/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class TermsFAQsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        Bundle bundle = getArguments();
        assert bundle != null;
        if (bundle.getString(TERMS_FAQS_FRAGMENT_TAG).equals(TERMS_KEY)) {
            view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_faqs, container, false);
        }
        initViews(view);
        return view;
    }

    private void initViews(View view) {
    }
}
