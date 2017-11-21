package icn.proludic.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import icn.proludic.R;
import icn.proludic.misc.CircleTransform;
import icn.proludic.misc.Utils;
import icn.proludic.models.FriendsModel;

import static icn.proludic.misc.Constants.DATE;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_ACCEPTED;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_CLASS_NAME;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_IS_CHALLENGE;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_IS_COMPLETE;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_IS_PENDING;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_LENGTH;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_USER_REQUESTED;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_USER_REQUESTING;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_WEIGHT;
import static icn.proludic.misc.Constants.LENGTH_LONG;
import static icn.proludic.misc.Constants.NO_DATE;
import static icn.proludic.misc.Constants.NO_PICTURE;
import static icn.proludic.misc.Constants.TRACKED_EVENTS_CLASS_NAME;
import static icn.proludic.misc.Constants.TRACKED_HEARTS;
import static icn.proludic.misc.Constants.TRACKED_TOTAL_EXERCISES;
import static icn.proludic.misc.Constants.USER;
import static icn.proludic.misc.Constants.USER_DRAW;
import static icn.proludic.misc.Constants.USER_HEARTS;
import static icn.proludic.misc.Constants.USER_LOSSES;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;
import static icn.proludic.misc.Constants.USER_WINS;

/**
 * Author:  Bradley Wilson
 * Date: 15/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewFriendsListAdapter extends RecyclerView.Adapter<RecyclerViewFriendsListAdapter.FriendsRecyclerViewHolder> {

    private Context mContext;
    private ArrayList<FriendsModel> friendsList;
    private Utils utils;
    private boolean isProfile;
    private boolean isWeight;
    private onFriendsListItemClickListener mClickListener;

    public RecyclerViewFriendsListAdapter(Context context, ArrayList<FriendsModel> friendsList, boolean isProfile) {
        this.mContext = context;
        this.friendsList = friendsList;
        this.isProfile = isProfile;
        utils = new Utils(mContext);
    }

    @Override
    public FriendsRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_friends, viewGroup, false);
        return new FriendsRecyclerViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(FriendsRecyclerViewHolder holder, int position) {
        FriendsModel model = friendsList.get(position);
//        String name = model.getName().substring(0, 1).toUpperCase()+model.getName().substring(1);
        determineProfilePicture(model.getProfilePicture(), holder.profilePicture);
        if (!isProfile) {
            holder.name.setText(model.getName());
            holder.username.setVisibility(View.VISIBLE);
            holder.username.setText(model.getUsername());
        } else {
            holder.name.setText(model.getUsername());
        }
    }

    private void determineProfilePicture(Object profilePicture, ImageView iv) {
        if (profilePicture instanceof String) {
            if (profilePicture.equals(NO_PICTURE)) {
                Picasso.with(mContext).load(R.drawable.no_profile).transform(new CircleTransform()).into(iv);
            } else {
                Picasso.with(mContext).load((String) profilePicture).transform(new CircleTransform()).into(iv);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.no_profile).transform(new CircleTransform()).into(iv);
        }
    }

    public void setOnItemClickListener(onFriendsListItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface onFriendsListItemClickListener {
        void onItemClickListener(View view, int position, FriendsModel model);
    }

    @Override
    public int getItemCount() {
        return (null != friendsList ? friendsList.size() : 0);
    }

    public void update(ArrayList<FriendsModel> updateList) {
        friendsList.clear();
        friendsList.addAll(updateList);
        notifyDataSetChanged();
    }

    private int getDays(String s) {
        int iend = s.indexOf(" ");
        String str = null;
        if (iend != -1)
            str = s.substring(0, iend);
        return Integer.valueOf(str);
    }

    class FriendsRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profilePicture;
        public TextView name, username;

        FriendsRecyclerViewHolder(View itemView) {
            super(itemView);
            this.profilePicture = (ImageView) itemView.findViewById(R.id.friends_profile_picture);
            this.name = (TextView) itemView.findViewById(R.id.friends_name);
            this.username = (TextView) itemView.findViewById(R.id.friends_username);
            itemView.setOnClickListener(this);
        }

        private long mLastClickTime = System.currentTimeMillis();
        private static final long CLICK_TIME_INTERVAL = 300;
        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClickListener(v, getAdapterPosition(), friendsList.get(getAdapterPosition()));
            }
        }
    }

    private void showProgressDialog(final ParseObject po) {
        final Dialog dialog = new Dialog(mContext, R.style.customAlertDialog);
        dialog.setContentView(R.layout.dialog_progress_challenge_friend);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        TextView startDate = (TextView) dialog.findViewById(R.id.start_date);
        final TextView endDate = dialog.findViewById(R.id.end_date);
        TextView username = dialog.findViewById(R.id.current_username);
        ImageView profilePicture = dialog.findViewById(R.id.current_profile_picture);
        final TextView amount = dialog.findViewById(R.id.current_amount);
        final TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        populateDetails(ParseUser.getCurrentUser(), username, profilePicture, amount, po, startDate, endDate, cancelButton);

        TextView friendsUsername = dialog.findViewById(R.id.friends_username);
        ImageView friendsPicture = dialog.findViewById(R.id.friends_profile_picture);
        final TextView friendsAmount = dialog.findViewById(R.id.friends_current_amount);
        populateDetails(po, friendsUsername, friendsPicture, friendsAmount, po, startDate, endDate, cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelButton.getText().toString().equals(mContext.getResources().getString(R.string.finish))) {
                    int myAmount = Integer.valueOf(amount.getText().toString());
                    int theirAmount = Integer.valueOf(friendsAmount.getText().toString());
                    if (myAmount > theirAmount) {
                        showScoreDialog(mContext.getString(R.string.you_won), po);
                        dialog.dismiss();
                    } else if (myAmount == theirAmount) {
                        showScoreDialog(mContext.getString(R.string.you_drew), po);
                        dialog.dismiss();
                    } else if (theirAmount > myAmount) {
                        showScoreDialog(mContext.getString(R.string.you_lost), po);
                        dialog.dismiss();
                    }
                } else {
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }

    private void showScoreDialog(String winType, final ParseObject po) {
        final Dialog dialog = new Dialog(mContext, R.style.customAlertDialog);
        dialog.setContentView(R.layout.dialog_challenge_score);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        TextView winTitle = dialog.findViewById(R.id.winTitle);
        ImageView userProfilePicture = dialog.findViewById(R.id.user_profile_picture);
        if (ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE).equals(NO_PICTURE)) {
            Picasso.with(mContext).load(R.drawable.no_profile).transform(new CircleTransform()).into(userProfilePicture);
        } else {
            Picasso.with(mContext).load(ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE)).transform(new CircleTransform()).into(userProfilePicture);
        }
        TextView winDescription = dialog.findViewById(R.id.challenge_description);
        TextView finishButton = dialog.findViewById(R.id.finish_button);

        if (winType.equals(mContext.getString(R.string.you_won))) {
            winTitle.setText(winType);
            winDescription.setText(mContext.getResources().getString(R.string.won_challenge));
            int totalHearts = ParseUser.getCurrentUser().getInt(USER_HEARTS);
            totalHearts = totalHearts + 500;
            int totalWins = ParseUser.getCurrentUser().getInt(USER_WINS) + 1;
            ParseUser.getCurrentUser().put(USER_WINS, totalWins);
            ParseUser.getCurrentUser().put(USER_HEARTS, totalHearts);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            try {
                                updateOpponent(USER_LOSSES, po);
                                updateChallenge(po);
                            } catch (ParseException e1) {
                                Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e1.getLocalizedMessage());
                            }
                        }
                    }
                });
        } else if (winType.equals(mContext.getString(R.string.you_drew))) {
            winTitle.setText(winType);
            winDescription.setText(mContext.getResources().getString(R.string.drew_challenge));
            int totalDraw = ParseUser.getCurrentUser().getInt(USER_DRAW) + 1;
            ParseUser.getCurrentUser().put(USER_DRAW, totalDraw);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            try {
                                updateOpponent(USER_DRAW, po);
                                updateChallenge(po);
                            } catch (ParseException e1) {
                                Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e1.getLocalizedMessage());
                            }
                        }
                    }
                });
        } else if (winType.equals(mContext.getString(R.string.you_lost))) {
            winTitle.setText(winType);
            winDescription.setText(mContext.getResources().getString(R.string.lost_challenge));
            int totalLosses = ParseUser.getCurrentUser().getInt(USER_LOSSES) + 1;
            ParseUser.getCurrentUser().put(USER_LOSSES, totalLosses);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            try {
                                updateOpponent(USER_WINS, po);
                                updateChallenge(po);
                            } catch (ParseException e1) {
                                Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e1.getLocalizedMessage());
                            }
                        }
                    }
                });
        }

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateOpponent(String winType, ParseObject po) throws ParseException {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("senderUserId", ParseUser.getCurrentUser().getObjectId());
        params.put("recipientUserId", po.fetchIfNeeded().getObjectId());
        params.put("winType", winType);
        params.put("total", po.fetchIfNeeded().getInt(winType));
        params.put("totalHearts", po.fetchIfNeeded().getInt(USER_HEARTS));
        switch (winType) {
            case USER_WINS:
                ParseCloud.callFunctionInBackground("UpdateUserScore", params, new FunctionCallback<String>() {
                    public void done(String success, ParseException e) {

                    }
                });
                ParseCloud.callFunctionInBackground("AddHeartsToUser", params, new FunctionCallback<String>() {
                    public void done(String success, ParseException e) {

                    }
                });
                break;
            case USER_LOSSES:
            case USER_DRAW:
                ParseCloud.callFunctionInBackground("UpdateUserScore", params, new FunctionCallback<String>() {
                    public void done(String success, ParseException e) {

                    }
                });
                break;
        }

    }

    private void updateChallenge(ParseObject otherUser) throws ParseException {
        final ParseQuery<ParseObject> query;
        if (ParseQuery.getQuery(FRIEND_REQUESTS_CLASS_NAME).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTED, ParseUser.getCurrentUser()).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTING, otherUser).whereEqualTo(FRIEND_REQUESTS_ACCEPTED, true)
                .whereEqualTo(FRIEND_REQUESTS_IS_PENDING, false).whereEqualTo(FRIEND_REQUESTS_IS_CHALLENGE, true).whereEqualTo(FRIEND_REQUESTS_IS_COMPLETE, false).find().size() > 0) {
            query = utils.getQuery(ParseUser.getCurrentUser(), otherUser, true, true, false, true);
        } else {
            query = utils.getQuery(ParseUser.getCurrentUser(),  otherUser, true, true, false, false);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    objects.get(0).put(FRIEND_REQUESTS_IS_COMPLETE, true);
                    objects.get(0).saveInBackground();
                }
            }
        });
    }

    private void populateDetails(final ParseObject currentUser, final TextView username, final ImageView profilePicture, final TextView amount, ParseObject otherUser, final TextView startDateTV, final TextView endDateTV, final TextView cancelButton) {
        try {
            final ParseQuery<ParseObject> query;
            if (ParseQuery.getQuery(FRIEND_REQUESTS_CLASS_NAME).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTED, ParseUser.getCurrentUser()).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTING, otherUser).whereEqualTo(FRIEND_REQUESTS_ACCEPTED, true)
                    .whereEqualTo(FRIEND_REQUESTS_IS_PENDING, false).whereEqualTo(FRIEND_REQUESTS_IS_CHALLENGE, true).whereEqualTo(FRIEND_REQUESTS_IS_COMPLETE, false).find().size() > 0) {
                query = utils.getQuery(ParseUser.getCurrentUser(), otherUser, true, true, false, true);
            } else {
                query = utils.getQuery(ParseUser.getCurrentUser(),  otherUser, true, true, false, false);
            }
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        final int totalDays = objects.get(0).getInt(FRIEND_REQUESTS_LENGTH);
                        Log.e("totalDays", String.valueOf(totalDays));
                        String todaysDate = objects.get(0).getString(DATE);
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                        Date startDate = null;
                        try {
                            startDate = df.parse(todaysDate);
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }
                        DateTime sDT = new DateTime(startDate).plusDays(totalDays);
                        Date endDate = sDT.toDate();
                        startDateTV.setText(mContext.getString(R.string.start) + todaysDate);
                        endDateTV.setText(mContext.getString(R.string.end) + df.format(endDate));

                        if (endDateTV.getText().toString().substring(5).equals(utils.getTodaysDateString())) {
                            cancelButton.setText(mContext.getResources().getString(R.string.finish));
                        }

                        List<String> dates = utils.getDates(todaysDate, df.format(endDate));
                        final int[] tAmount = {0};
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRACKED_EVENTS_CLASS_NAME);
                        query.whereEqualTo(USER, currentUser);
                        query.whereContainedIn(DATE, dates);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> newObjects, ParseException e) {
                                if (e == null) {
                                    try {
                                        username.setText(currentUser.fetchIfNeeded().getString("username"));
                                        if (currentUser.fetchIfNeeded().getString(USER_PROFILE_PICTURE).equals(NO_PICTURE)) {
                                            Picasso.with(mContext).load(R.drawable.no_profile).transform(new CircleTransform()).into(profilePicture);
                                        } else {
                                            Picasso.with(mContext).load(currentUser.fetchIfNeeded().getString(USER_PROFILE_PICTURE)).transform(new CircleTransform()).into(profilePicture);
                                        }

                                        if (objects.get(0).getBoolean(FRIEND_REQUESTS_WEIGHT)) {
                                            for (ParseObject j : newObjects) {
                                                tAmount[0] = tAmount[0] + j.getInt(TRACKED_TOTAL_EXERCISES);
                                            }
                                            amount.setText(String.valueOf(tAmount[0]));
                                            amount.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_weight_white), null,null,null);
                                        } else {
                                            for (ParseObject j : newObjects) {
                                                tAmount[0] = tAmount[0] + j.getInt(TRACKED_HEARTS);
                                            }
                                            amount.setText(String.valueOf(tAmount[0]));
                                            amount.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_white), null, null, null);
                                        }
                                    } catch (ParseException e1) {
                                        Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e1.getLocalizedMessage());
                                    }
                                } else {
                                    Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e.getLocalizedMessage());
                                }
                            }
                        });
                    } else {
                        Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e.getLocalizedMessage());
                    }
                }
            });
        } catch (ParseException e1) {
            Log.e(RecyclerViewFriendsListAdapter.class.getSimpleName(), "Failed " + e1.getLocalizedMessage());
        }
    }

    private boolean determineIfChallengeOrNot(ParseObject friend) throws ParseException {
        return ParseQuery.getQuery(FRIEND_REQUESTS_CLASS_NAME).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTED, ParseUser.getCurrentUser()).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTING, friend).whereEqualTo(FRIEND_REQUESTS_IS_CHALLENGE, true).whereEqualTo(FRIEND_REQUESTS_IS_PENDING, false).whereEqualTo(FRIEND_REQUESTS_ACCEPTED, true).whereEqualTo(FRIEND_REQUESTS_IS_COMPLETE, false).count() > 0 ||
                ParseQuery.getQuery(FRIEND_REQUESTS_CLASS_NAME).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTED, friend).whereEqualTo(FRIEND_REQUESTS_USER_REQUESTING, ParseUser.getCurrentUser()).whereEqualTo(FRIEND_REQUESTS_IS_CHALLENGE, true).whereEqualTo(FRIEND_REQUESTS_IS_PENDING, false).whereEqualTo(FRIEND_REQUESTS_ACCEPTED, true).whereEqualTo(FRIEND_REQUESTS_IS_COMPLETE, false).count() > 0;
    }

    private void showChallengeDialog(final String name, Object profilePicture, final ParseObject po) {
        final Dialog dialog = new Dialog(mContext, R.style.customAlertDialog);
        dialog.setContentView(R.layout.dialog_challenge_friend);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ImageView friendsProfilePicture = (ImageView) dialog.findViewById(R.id.friends_profile_picture);
        determineProfilePicture(profilePicture, friendsProfilePicture);

        TextView friendDescription = (TextView) dialog.findViewById(R.id.challenge_description);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String start = mContext.getResources().getString(R.string.startchallenge);
        SpannableString startSpannable = new SpannableString(start);
        startSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, start.length(), 0);
        builder.append(startSpannable);

        SpannableString nameSpannable = new SpannableString(" " + name + " ");
        nameSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimary)), 0, name.length() + 1, 0);
        builder.append(nameSpannable);

        String end = mContext.getResources().getString(R.string.endchallenge);
        SpannableString endSpannable = new SpannableString(end);
        endSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, end.length(), 0);
        builder.append(endSpannable);

        friendDescription.setText(builder);

        TextView noButton = (TextView) dialog.findViewById(R.id.no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView yesButton = (TextView) dialog.findViewById(R.id.yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showSelectionDialog(po, name);
            }
        });
        dialog.show();
    }

    private void showSelectionDialog(final ParseObject po, final String name) {
        final Dialog dialog = new Dialog(mContext, R.style.customAlertDialog);
        dialog.setTitle(mContext.getResources().getString(R.string.challengeTitle));
        dialog.setCancelable(false);
        LinearLayout ll = new LinearLayout(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(lp);
        ImageView iv = new ImageView(mContext);
        iv.setImageResource(R.drawable.btn_weight);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.weight = (float) 0.5;
        lp1.setMargins(utils.convertDpToPixels(20), utils.convertDpToPixels(5), utils.convertDpToPixels(20), utils.convertDpToPixels(5));
        iv.setLayoutParams(lp1);
        ll.addView(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showTimePeriodDialog(true, po, name);
            }
        });
        ImageView iv1 = new ImageView(mContext);
        iv1.setImageResource(R.drawable.btn_hearts);
        iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams lp11 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp11.weight = (float) 0.5;
        lp11.setMargins(utils.convertDpToPixels(20), utils.convertDpToPixels(5), utils.convertDpToPixels(20), utils.convertDpToPixels(5));
        iv1.setLayoutParams(lp1);
        ll.addView(iv1);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showTimePeriodDialog(false, po, name);
            }
        });
        dialog.addContentView(ll, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dialog.show();
    }

    private void showTimePeriodDialog(final boolean isWeight, final ParseObject po, final String name) {
        final Dialog dialog = new Dialog(mContext, R.style.customAlertDialog);
        dialog.setContentView(R.layout.dialog_slider);
        dialog.setTitle(mContext.getResources().getString(R.string.choosetimechallenge));
        dialog.setCancelable(false);

        SeekBar time_seekbar = (SeekBar) dialog.findViewById(R.id.timer_seekbar);
        time_seekbar.setProgress(10);
        time_seekbar.incrementProgressBy(10);
        time_seekbar.setMax(140);
        final TextView seekBarValue = (TextView) dialog.findViewById(R.id.value_tv);
        seekBarValue.setText(seekBarValue.getText().toString());
        time_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int time = progress / 10;
                switch (progress) {
                    case 10:
                        seekBarValue.setText(time + " " + mContext.getResources().getString(R.string.day));
                        break;
                    case 20:
                    case 30:
                    case 40:
                    case 50:
                    case 60:
                    case 70:
                    case 80:
                    case 90:
                    case 100:
                    case 110:
                    case 120:
                    case 130:
                    case 140:
                        seekBarValue.setText(time + " " + mContext.getResources().getString(R.string.days));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView confirmButton = (TextView) dialog.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject newRequest = new ParseObject(FRIEND_REQUESTS_CLASS_NAME);
                newRequest.put(FRIEND_REQUESTS_USER_REQUESTING, ParseUser.getCurrentUser());
                newRequest.put(FRIEND_REQUESTS_USER_REQUESTED, po);
                newRequest.put(FRIEND_REQUESTS_ACCEPTED, false);
                newRequest.put(FRIEND_REQUESTS_IS_PENDING, true);
                newRequest.put(FRIEND_REQUESTS_IS_CHALLENGE, true);
                newRequest.put(FRIEND_REQUESTS_IS_COMPLETE, false);
                newRequest.put(FRIEND_REQUESTS_WEIGHT, isWeight);
                newRequest.put(DATE, NO_DATE);
                newRequest.put(FRIEND_REQUESTS_LENGTH, getDays(seekBarValue.getText().toString()));
                newRequest.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        utils.makeText(mContext.getResources().getString(R.string.successchallengerequest) + name + ".", LENGTH_LONG);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
}
