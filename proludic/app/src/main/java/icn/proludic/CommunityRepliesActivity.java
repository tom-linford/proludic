package icn.proludic;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import icn.proludic.adapters.RecyclerViewCommunityReplyAdapter;
import icn.proludic.misc.CircleTransform;
import icn.proludic.misc.ProfanityFilter;
import icn.proludic.misc.SashidoHelper;
import icn.proludic.misc.Utils;
import icn.proludic.models.CommunityPost;
import icn.proludic.widgets.CustomTrebucheTextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_CLASS_NAME;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_REPLIES;
import static icn.proludic.misc.Constants.COMMUNITY_REPLIES_CLASS_NAME;
import static icn.proludic.misc.Constants.COMMUNITY_REPLIES_POST;
import static icn.proludic.misc.Constants.COMMUNITY_REPLIES_REPLYING_USER;
import static icn.proludic.misc.Constants.COMMUNITY_REPLIES_REPLY_CONTENT;
import static icn.proludic.misc.Constants.COMMUNITY_REPLY_REPLIES_CLASS_NAME;
import static icn.proludic.misc.Constants.KEEPING_THE_PEACE_COL;
import static icn.proludic.misc.Constants.LENGTH_LONG;
import static icn.proludic.misc.Constants.LENGTH_SHORT;
import static icn.proludic.misc.Constants.NO_PICTURE;
import static icn.proludic.misc.Constants.POST_KEY;
import static icn.proludic.misc.Constants.REPORTED_POSTS_CLASS_NAME;
import static icn.proludic.misc.Constants.REPORTED_POSTS_CONTENT;
import static icn.proludic.misc.Constants.REPORTED_POSTS_MODERATED;
import static icn.proludic.misc.Constants.REPORTED_POSTS_REASON;
import static icn.proludic.misc.Constants.REPORTED_POSTS_USER;
import static icn.proludic.misc.Constants.SOCIAL_BUZZ_COL;
import static icn.proludic.misc.Constants.USER;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;

/**
 * Author:  Bradley Wilson
 * Date: 27/06/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class CommunityRepliesActivity extends AppCompatActivity {

    private CommunityPost post;
    private Utils utils;
    private CoordinatorLayout parentLayout;
    private Context context = this;
    private RecyclerView threadRepliesRecyclerView;
    private FloatingActionButton replyFab;
    private Dialog dialog;
    private RecyclerViewCommunityReplyAdapter adapter;
    private ArrayList<CommunityPost> threadReplies;
    private String reason = NO_PICTURE;
    private EditText et;
    boolean USER_ENTERING_COMMENT = false, COMMENT_CONTAINER_VISIBLE = false;
    private EditText COMMENTS_ET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_replies);
        utils = new Utils(context);
        post = (CommunityPost) getIntent().getExtras().getSerializable(POST_KEY);
        new AsyncProfanity().execute();
        initToolbar();
        initFab();
        initViews();
    }

    private void initFab() {
        replyFab = (FloatingActionButton) findViewById(R.id.reply_fab);
        replyFab.setOnClickListener(customListener);
    }

    private void initViews() {
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);
        ImageView postProfilePicture = (ImageView) findViewById(R.id.user_profile_picture);
        if (post.getProfileImageURL().equals(NO_PICTURE)) {
            Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(postProfilePicture);
        } else {
            Picasso.with(context).load(post.getProfileImageURL()).transform(new CircleTransform()).into(postProfilePicture);
        }

        TextView postUsername = (TextView) findViewById(R.id.user_name);
        postUsername.setText(post.getProfileUsername()+ " - ");

        TextView postDateAndTime = (TextView) findViewById(R.id.user_time);
        postDateAndTime.setText(post.getProfileTimeAndDate());

        TextView postTitle = (TextView) findViewById(R.id.thread_title);
        postTitle.setText(post.getTitle());

        TextView postContent = (TextView) findViewById(R.id.thread_content);
        postContent.setText(post.getMessage());

        ImageView postReportButton = (ImageView) findViewById(R.id.report_button);
        postReportButton.setOnClickListener(customListener);

        threadRepliesRecyclerView = (RecyclerView) findViewById(R.id.replies_recycler_view);
        threadRepliesRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager tLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        threadRepliesRecyclerView.setLayoutManager(tLayoutManager);
        threadRepliesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    if (!USER_ENTERING_COMMENT) {
                        replyFab.show();
                    }
                } else {
                    replyFab.hide();
                }
            }
        });

        try {
            ArrayList<ParseObject> replies = getReplies(post, false);
            if (replies.size() > 0) {
                populateRepliesRecyclerView(replies, false, threadRepliesRecyclerView, false);
            }
        } catch (ParseException e) {
            Log.e("failed", "failed" + e.getLocalizedMessage());
        }
    }

    private void populateRepliesRecyclerView(ArrayList<ParseObject> replies, boolean isComment, RecyclerView threadRepliesRecyclerView, boolean isReply) throws ParseException {
        threadReplies = new ArrayList<>();
        for (ParseObject j : replies) {
            String username = j.getParseObject(COMMUNITY_REPLIES_REPLYING_USER).fetchIfNeeded().getString("username");
            String message = j.getString(COMMUNITY_REPLIES_REPLY_CONTENT);
            String profileImageUrl = j.getParseObject(COMMUNITY_REPLIES_REPLYING_USER).fetchIfNeeded().getString(USER_PROFILE_PICTURE);
            String profileTimeAndDate = utils.getDateAndTime(j.getCreatedAt());
            String userObjectID = j.getParseObject(COMMUNITY_REPLIES_REPLYING_USER).getObjectId();
            if (!isComment) {
                int repliesCount = 0;
                JSONArray repliesArray = j.getJSONArray(COMMUNITY_POSTS_REPLIES);
                if (repliesArray != null) {
                    if (repliesArray.length() > 0) {
                        repliesCount = repliesArray.length();
                    }
                }
                threadReplies.add(new CommunityPost(j.getObjectId(), message, profileImageUrl, username, profileTimeAndDate, userObjectID, utils.convertJSONtoArrayList(repliesArray), repliesCount));
            } else {
                threadReplies.add(new CommunityPost(j.getObjectId(), message, profileImageUrl, username, profileTimeAndDate, userObjectID, new ArrayList<String>(), 0));
            }
        }

        if (!isComment) {
            Collections.reverse(threadReplies);
        }
        adapter = new RecyclerViewCommunityReplyAdapter(context, threadReplies, isComment);
        threadRepliesRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewCommunityReplyAdapter.onReplyItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, CommunityPost post, boolean isLongClicked, CardView repliesContainer, TextView commentsTV, boolean isReply) {
                switch (view.getId()) {
                    case R.id.view_more_comments:
                        if (commentsTV.getText().equals(context.getResources().getString(R.string.viewmoreComments))) {
                            LinearLayout commentsContainer = getCommentsContainer(post, false);
                            repliesContainer.addView(commentsContainer);
                            COMMENT_CONTAINER_VISIBLE = true;
                            commentsTV.setText(context.getResources().getString(R.string.viewlesscomments));
                        } else {
                            repliesContainer.removeAllViews();
                            COMMENT_CONTAINER_VISIBLE = false;
                            commentsTV.setText(context.getResources().getString(R.string.viewmoreComments));
                        }
                        break;
                    case R.id.report_button:
                        showReportDialog(post.getUserObjectID(), post.getMessage());
                        break;
                    case R.id.reply_button:
                        if (!COMMENT_CONTAINER_VISIBLE) {
                            if (commentsTV.getVisibility() == VISIBLE) {
                                commentsTV.setText(context.getString(R.string.viewlesscomments));
                            } else {
                                commentsTV.setVisibility(VISIBLE);
                                commentsTV.setText(context.getString(R.string.viewlesscomments));
                            }
                            COMMENT_CONTAINER_VISIBLE = true;
                            LinearLayout commentsContainer = getCommentsContainer(post, true);
                            repliesContainer.addView(commentsContainer);
                        }
                        break;
                    default:
                        if (isLongClicked) {

                        }
                        break;
                }
            }
        });
        adapter.notifyDataSetChanged();
        if (isReply) {
            if (adapter.getItemCount() > 0) {
                threadRepliesRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

    private void initToolbar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(post.getTitle());
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.report_button:
                    showReportDialog(post.getUserObjectID(), post.getMessage());
                    break;
                case R.id.reply_fab:
                    showReplyDialog();
                    break;
            }
        }
    };

    private void showReportDialog(final String objectID, final String content) {
        final Dialog dialog = new Dialog(context, R.style.customAlertDialog);
        dialog.setContentView(R.layout.dialog_report);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final RadioGroup reportGroup = (RadioGroup) dialog.findViewById(R.id.radioReport);
        et = dialog.findViewById(R.id.other_et);
        reportGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int selectedId) {
                switch (selectedId) {
                    case R.id.radioProfanity:
                        if (et.getVisibility() == View.VISIBLE) {
                            et.setVisibility(View.INVISIBLE);
                        }
                        reason = "profanity";
                        break;
                    case R.id.radioSpam:
                        if (et.getVisibility() == View.VISIBLE) {
                            et.setVisibility(View.INVISIBLE);
                        }
                        reason = "spam/advertising";
                        break;
                    case R.id.radioOther:
                        et.setVisibility(VISIBLE);
                        break;
                }
            }
        });

        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        final TextView reportButton = dialog.findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reportGroup.getCheckedRadioButtonId() == R.id.radioOther) {
                    assert et != null;
                    if (et.getText().toString().length() == 0) {
                        et.setError(context.getResources().getString(R.string.other_message));
                    } else {
                        reason = et.getText().toString();
                    }
                }

                if (reason.equals(NO_PICTURE)) {
                    utils.makeText(getString(R.string.select_an_option), LENGTH_LONG);
                } else {
                    ParseObject reportPost = new ParseObject(REPORTED_POSTS_CLASS_NAME);
                    ParseObject userObject = ParseObject.createWithoutData("_User", objectID);
                    reportPost.put(REPORTED_POSTS_USER, userObject);
                    reportPost.put(REPORTED_POSTS_MODERATED, false);
                    reportPost.put(REPORTED_POSTS_REASON, reason);
                    reportPost.put(REPORTED_POSTS_CONTENT, content);
                    reportPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                utils.makeText(context.getResources().getString(R.string.reportsubmittedsuccess), LENGTH_LONG);
                                SashidoHelper.earnAchievement(KEEPING_THE_PEACE_COL, getString(R.string.keeping_the_peace), context, parentLayout);
                                dialog.cancel();
                            } else {
                                utils.makeText(context.getResources().getString(R.string.reportsubmitfailed), LENGTH_LONG);
                                Log.e(CommunityRepliesActivity.class.getSimpleName(), "Failed " + e.getLocalizedMessage());
                            }
                        }
                    });
                }
                }
        });
        dialog.show();
    }

    private void showReplyDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_post);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final TextView threadTitleTitle = dialog.findViewById(R.id.thread_title_title);
        threadTitleTitle.setText(post.getMessage());
        final EditText threadTitle = dialog.findViewById(R.id.post_title_input);
        threadTitle.setVisibility(GONE);
        final TextView threadContentTitle = dialog.findViewById(R.id.thread_content_title);
        threadContentTitle.setVisibility(GONE);
        final EditText threadContent = dialog.findViewById(R.id.post_content_input);
        threadContent.setHint(context.getResources().getString(R.string.message));
        final TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        TextView postButton = dialog.findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               validatePost(threadContent.getText().toString(), threadContent, false, threadRepliesRecyclerView, post, COMMUNITY_REPLIES_CLASS_NAME, COMMUNITY_POSTS_CLASS_NAME);
            }
        });
        dialog.show();
    }

    private void validatePost(String message, EditText content, boolean isComment, RecyclerView threadRepliesRecyclerView, CommunityPost post, String currentClassName, String previousClassName) {
        if (message.length() == 0) {
            content.setError(getResources().getString(R.string.please_enter_message));
        } else {
            boolean isBadWordMessage = ProfanityFilter.filterText(message);
            if (isBadWordMessage){
                utils.makeText(context.getResources().getString(R.string.profanity_message), LENGTH_LONG);
            } else {
                savePost(message, isComment, threadRepliesRecyclerView, post, currentClassName, previousClassName);
            }
        }
    }

    private void savePost(final String replyMessage, final boolean isComment, final RecyclerView threadRepliesRecyclerView, final CommunityPost post, String currentClassName, final String previousClassName) {
        final ParseObject threadReply = new ParseObject(currentClassName);
        ParseObject postObject = ParseObject.createWithoutData(previousClassName, post.getObjectID());
        threadReply.put(COMMUNITY_REPLIES_REPLY_CONTENT, replyMessage);
        threadReply.put(COMMUNITY_REPLIES_REPLYING_USER, ParseUser.getCurrentUser());
        if (isComment) {
            threadReply.put("PostReplies", postObject);
        } else {
            threadReply.put(COMMUNITY_REPLIES_POST, postObject);
        }
        threadReply.put(COMMUNITY_POSTS_REPLIES, new JSONArray());
        threadReply.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(previousClassName);
                    query.whereEqualTo("objectId", post.getObjectID());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject j : objects) {
                                    ArrayList<String> replies = new ArrayList<>();
                                    JSONArray jsonReplies = j.getJSONArray(COMMUNITY_POSTS_REPLIES);
                                    if (jsonReplies.length() > 0) {
                                        for (int i = 0; i < jsonReplies.length(); i++) {
                                            replies.add(jsonReplies.optString(i));
                                        }
                                        replies.add(threadReply.getObjectId());
                                        jsonReplies = new JSONArray(replies);
                                    } else {
                                        jsonReplies.put(threadReply.getObjectId());
                                    }
                                    j.put(COMMUNITY_POSTS_REPLIES, jsonReplies);
                                    j.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                                if (adapter != null) {
                                                    threadReplies.add(new CommunityPost(threadReply.getObjectId(), replyMessage, ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE), ParseUser.getCurrentUser().getUsername(), utils.getDateAndTime(threadReply.getCreatedAt()), ParseUser.getCurrentUser().getObjectId(), new ArrayList<String>(), 0));
                                                    adapter.updateList(threadReplies);
                                                } else {
                                                    ArrayList<CommunityPost> threadReplies = new ArrayList<>();
                                                    threadReplies.add(new CommunityPost(threadReply.getObjectId(), replyMessage, ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE), ParseUser.getCurrentUser().getUsername(), utils.getDateAndTime(threadReply.getCreatedAt()), ParseUser.getCurrentUser().getObjectId(), new ArrayList<String>(), 0));
                                                    RecyclerViewCommunityReplyAdapter adapter = new RecyclerViewCommunityReplyAdapter(context, threadReplies, false);
                                                    threadRepliesRecyclerView.setAdapter(adapter);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                if (isComment) {
                                                    COMMENTS_ET.setText("");
                                                }
                                                utils.makeText(getString(R.string.reply_posted_successfully), LENGTH_SHORT);
                                                SashidoHelper.earnAchievement(SOCIAL_BUZZ_COL, getString(R.string.social_buzz), context, parentLayout);
                                            } else {
                                                Log.e(CommunityRepliesActivity.class.getSimpleName(), "failed" + e.getLocalizedMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                Log.e(CommunityRepliesActivity.class.getSimpleName(), "failed" + e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    Log.e(CommunityRepliesActivity.class.getSimpleName(), "failed" + e.getLocalizedMessage());
                }
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public ArrayList<ParseObject> getReplies(CommunityPost post, boolean isComment) throws ParseException {
        String className = "";
        if (isComment) {
            className = COMMUNITY_REPLY_REPLIES_CLASS_NAME;
        } else {
            className = COMMUNITY_REPLIES_CLASS_NAME;
        }
        ArrayList<ParseObject> repliesObjects = new ArrayList<>();
        for (int i = 0; i < post.getRepliesArray().size(); i++) {
            repliesObjects.add(ParseObject.createWithoutData(className, post.getRepliesArray().get(i)));
        }
        ParseObject.fetchAll(repliesObjects);
        return repliesObjects;
    }

    public RecyclerView getNewRecyclerView() {
        RecyclerView commentsRecyclerView = new RecyclerView(CommunityRepliesActivity.this);
        commentsRecyclerView.setHasFixedSize(true);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.setMargins(utils.convertDpToPixels(15), 0, 0, 0);
        commentsRecyclerView.setLayoutParams(marginLayoutParams);
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        commentsRecyclerView.setLayoutManager(cLayoutManager);
        commentsRecyclerView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        return commentsRecyclerView;
    }

    public LinearLayout getCommentsContainer(final CommunityPost post, boolean isReply) {
        LinearLayout commentsContainer = new LinearLayout(context);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentsContainer.setLayoutParams(lp);
        commentsContainer.setOrientation(LinearLayout.VERTICAL);
        final RecyclerView commentsRecyclerView = getNewRecyclerView();
        ArrayList<ParseObject> comments = null;
        try {
            comments = getReplies(post, true);
            populateRepliesRecyclerView(comments, true, commentsRecyclerView, isReply);
        } catch (ParseException e) {
            Log.e(CommunityRepliesActivity.class.getSimpleName(), "Failed " + e.getLocalizedMessage());
        }
        commentsContainer.addView(commentsRecyclerView);

        LinearLayout addCommentContainer = new LinearLayout(context);
        ViewGroup.MarginLayoutParams lp1 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, utils.convertDpToPixels(80));
        addCommentContainer.setWeightSum(1);
        addCommentContainer.setLayoutParams(lp1);
        addCommentContainer.setOrientation(LinearLayout.HORIZONTAL);

        ImageView userProfilePicture = new ImageView(context);
        LinearLayout.LayoutParams ivlp = new LinearLayout.LayoutParams(utils.convertDpToPixels(35), utils.convertDpToPixels(35));
        ivlp.gravity = Gravity.CENTER_VERTICAL;
        userProfilePicture.setLayoutParams(ivlp);
        Picasso.with(context).load(ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE)).transform(new CircleTransform()).into(userProfilePicture);
        addCommentContainer.addView(userProfilePicture);

        final EditText commentsET = new EditText(context);
        COMMENTS_ET = commentsET;
        LinearLayout.LayoutParams etlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.8);
        etlp.gravity = Gravity.CENTER_VERTICAL;
        etlp.setMargins(utils.convertDpToPixels(5), 0, 0, 0);
        commentsET.setLayoutParams(etlp);
        commentsET.setHint(context.getResources().getString(R.string.message_hint));
        commentsET.setHintTextColor(ContextCompat.getColor(context, R.color.colorPrimaryHalf));
        commentsET.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        commentsET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.e("onFocusChange", "onFocusChange");
                if (hasFocus) {
                    replyFab.hide();
                    USER_ENTERING_COMMENT = true;
                } else {
                    replyFab.show();
                    USER_ENTERING_COMMENT = false;
                }
            }
        });
        addCommentContainer.addView(commentsET);
        commentsContainer.addView(addCommentContainer);

        TextView postButton = new CustomTrebucheTextView(context);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.2);
        blp.setMargins(utils.convertDpToPixels(5), 0, utils.convertDpToPixels(5), 0);
        blp.gravity = Gravity.CENTER_VERTICAL;
        postButton.setLayoutParams(blp);
        postButton.setGravity(Gravity.CENTER);
        postButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        postButton.setBackgroundResource(R.drawable.rounded_corner_tv);
        postButton.setText(context.getResources().getString(R.string.post));
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePost(commentsET.getText().toString(), commentsET, true, commentsRecyclerView, post, COMMUNITY_REPLY_REPLIES_CLASS_NAME, COMMUNITY_REPLIES_CLASS_NAME);
            }
        });
        addCommentContainer.addView(postButton);


        return commentsContainer;
    }

    private class AsyncProfanity extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ProfanityFilter.loadConfigs();
            return null;
        }
    }
}
