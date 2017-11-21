package icn.proludic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import icn.proludic.CommunityActivity;
import icn.proludic.R;
import icn.proludic.adapters.RecyclerViewLeaderboardAdapter;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.models.LeaderboardModel;

import static icn.proludic.misc.Constants.ACHIEVEMENTS_LOCAL;
import static icn.proludic.misc.Constants.ACHIEVEMENTS_NATIONAL;
import static icn.proludic.misc.Constants.HEARTS_LOCAL;
import static icn.proludic.misc.Constants.HEARTS_NATIONAL;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.LOCATION_NAME_KEY;
import static icn.proludic.misc.Constants.USER_HEARTS;

/**
 * Author:  Bradley Wilson
 * Date: 11/04/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class CommunityFragment extends Fragment {

    RecyclerView heartsRecyclerView, achievementsRecyclerView;
    private TextView localTV, nationalTV, forumTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        localTV = (TextView) view.findViewById(R.id.tv_local);
        localTV.setOnClickListener(customListener);
        localTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        nationalTV = (TextView) view.findViewById(R.id.tv_national);
        nationalTV.setOnClickListener(customListener);
        forumTV = (TextView) view.findViewById(R.id.tv_forum);
        forumTV.setOnClickListener(customListener);
        localTV.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        heartsRecyclerView = view.findViewById(R.id.hearts_leaderboard);
        heartsRecyclerView.setHasFixedSize(false);
        LinearLayoutManager hLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        heartsRecyclerView.setLayoutManager(hLayoutManager);
        populateRecyclerView(HEARTS_LOCAL);

        achievementsRecyclerView = view.findViewById(R.id.achievement_leaderboard);
        achievementsRecyclerView.setHasFixedSize(false);
        LinearLayoutManager aLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        achievementsRecyclerView.setLayoutManager(aLayoutManager);
        populateRecyclerView(ACHIEVEMENTS_LOCAL);

    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_local:
                    nationalTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    forumTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    populateRecyclerView(HEARTS_LOCAL);
                    populateRecyclerView(ACHIEVEMENTS_LOCAL);
                    break;
                case R.id.tv_national:
                    localTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    forumTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    populateRecyclerView(HEARTS_NATIONAL);
                    populateRecyclerView(ACHIEVEMENTS_NATIONAL);
                    break;
                case R.id.tv_forum:
                    nationalTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    localTV.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    openForum();
                    break;
            }
        }
    };

    private void openForum() {
        Intent intent = new Intent(getActivity(), CommunityActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void populateRecyclerView(final String type) {
        final ArrayList<LeaderboardModel> leaderboardsList = new ArrayList<>();
        ParseQuery<ParseObject> query = getCorrespondingQuery(type);
        query.setLimit(10);
        switch (type) {
            case HEARTS_LOCAL:
                query.orderByDescending(USER_HEARTS);
                query.whereEqualTo(HOME_PARK_KEY, ParseUser.getCurrentUser().getString(HOME_PARK_KEY));
                break;
            case ACHIEVEMENTS_LOCAL:
                query.orderByDescending("TotalAchievements");
                query.whereEqualTo(HOME_PARK_KEY, ParseUser.getCurrentUser().getString(HOME_PARK_KEY));
                break;
            case HEARTS_NATIONAL:
                query.orderByDescending(USER_HEARTS);
                break;
            case ACHIEVEMENTS_NATIONAL:
                query.whereEqualTo("isFrench", SharedPreferencesManager.getString(getContext(), "locale").equals("fr"));
                query.orderByDescending("TotalAchievements");
                break;
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        switch (type) {
                            case HEARTS_LOCAL:
                                leaderboardsList.add(new LeaderboardModel(j.getString("name"), j.getInt("Hearts"), j.getString("username"), type));
                                break;
                            case HEARTS_NATIONAL:
                                ParseObject homePark = ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, j.getString(HOME_PARK_KEY));
                                try {
                                    leaderboardsList.add(new LeaderboardModel(j.getString("name") + "\n" + homePark.fetchIfNeeded().getString(LOCATION_NAME_KEY), j.getInt("Hearts"), j.getString("username"), type));
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                break;
                            case ACHIEVEMENTS_LOCAL:
                                leaderboardsList.add(new LeaderboardModel(j.getString("name"), j.getInt("TotalAchievements"), j.getString("username"), type));
                                break;
                            case ACHIEVEMENTS_NATIONAL:
                                leaderboardsList.add(new LeaderboardModel(j.getString(LOCATION_NAME_KEY), j.getInt("TotalAchievements"), j.getString("username"), type));
                                break;
                        }
                    }
                    RecyclerViewLeaderboardAdapter adapter = new RecyclerViewLeaderboardAdapter(getActivity(), leaderboardsList);
                    switch (type) {
                        case HEARTS_LOCAL:
                        case HEARTS_NATIONAL:
                            heartsRecyclerView.setAdapter(adapter);
                            break;
                        case ACHIEVEMENTS_LOCAL:
                        case ACHIEVEMENTS_NATIONAL:
                            achievementsRecyclerView.setAdapter(adapter);
                            break;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("failed", "Failed" + e.getLocalizedMessage());
                }
            }
        });

    }

    private ParseQuery<ParseObject> getCorrespondingQuery(String type) {
        switch (type) {
            case ACHIEVEMENTS_NATIONAL:
                return ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
            default:
                return ParseQuery.getQuery("_User");
        }
    }

    public static CommunityFragment newInstance(int position) {
        final CommunityFragment f = new CommunityFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", position);
        f.setArguments(args);
        return f;
    }
}
