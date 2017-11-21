package icn.proludic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import icn.proludic.adapters.RecyclerViewCommunityPostAdapter;
import icn.proludic.misc.ProfanityFilter;
import icn.proludic.misc.SashidoHelper;
import icn.proludic.misc.Utils;
import icn.proludic.models.CommunityPost;

import static icn.proludic.misc.Constants.COMMUNITY_POSTS_CLASS_NAME;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_COMMUNITY;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_CONTENT;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_OP;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_REPLIES;
import static icn.proludic.misc.Constants.COMMUNITY_POSTS_TITLE;
import static icn.proludic.misc.Constants.COMMUNITY_REPLIES_CLASS_NAME;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.LENGTH_LONG;
import static icn.proludic.misc.Constants.LENGTH_SHORT;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.POST_KEY;
import static icn.proludic.misc.Constants.SOCIAL_BUZZ_COL;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;

/**
 * Author:  Bradley Wilson
 * Date: 21/06/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class CommunityActivity extends AppCompatActivity {

    private Context context = this;
    private Utils utils;
    private Dialog dialog;
    private RecyclerView postRecyclerView;
    private RecyclerViewCommunityPostAdapter adapter;
    private CoordinatorLayout parentLayout;
    private ArrayList<CommunityPost> communityPosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        utils = new Utils(context);
        new AsyncProfanity().execute();
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);
        initFab();
        initRecyclerView();
        initToolbar();
    }

    private void initToolbar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(context.getResources().getString(R.string.forum));
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(CommunityActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    private void initRecyclerView() {
        postRecyclerView = (RecyclerView) findViewById(R.id.forum_recycler_view);
        postRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        postRecyclerView.setLayoutManager(pLayoutManager);
        if (utils.isNetworkAvailable(context)) {
            populateCommunityPosts(ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, ParseUser.getCurrentUser().getString(HOME_PARK_KEY)));
        } else {
            showConnectionSnackbar();
        }
    }

    private void showConnectionSnackbar() {
        final Snackbar snackBar = Snackbar.make(parentLayout, context.getResources().getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(context.getResources().getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isNetworkAvailable(context)) {
                    populateCommunityPosts(ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, ParseUser.getCurrentUser().getString(HOME_PARK_KEY)));
                    snackBar.dismiss();
                } else {
                    showConnectionSnackbar();
                }
            }
        });

        snackBar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.show();
    }

    private void populateCommunityPosts(ParseObject homePark) {
        communityPosts = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(COMMUNITY_POSTS_CLASS_NAME);
        query.whereEqualTo(COMMUNITY_POSTS_COMMUNITY, homePark);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        if (postRecyclerView.getVisibility() != View.VISIBLE) {
                            postRecyclerView.setVisibility(View.VISIBLE);
                        }
                        try {
                            for (ParseObject j : objects) {
                                String title = j.getString(COMMUNITY_POSTS_TITLE);
                                Log.e("debug", "Title");
                                String message = j.getString(COMMUNITY_POSTS_CONTENT);
                                Log.e("debug", "Content");
                                String userObjectID, opProfileUrl, opUsername;
                                // error was being caused here by the OP account being removed
                                try {
                                    userObjectID = j.getParseObject(COMMUNITY_POSTS_OP).fetchIfNeeded().getObjectId();
                                    Log.e("debug", "OP ID");
                                    opProfileUrl = j.getParseObject(COMMUNITY_POSTS_OP).fetchIfNeeded().getString(USER_PROFILE_PICTURE);
                                    Log.e("debug", "OP DP");
                                    opUsername = j.getParseObject(COMMUNITY_POSTS_OP).getString("username");
                                    Log.e("debug", "OP Name");
                                } catch (ParseException e1) {
                                    continue;
                                }
                                String dateAndTime = utils.getDateAndTime(j.getCreatedAt());
                                Log.e("debug", "DateTime");
                                int repliesTotal = 0;
                                JSONArray repliesArray = j.getJSONArray(COMMUNITY_POSTS_REPLIES);
                                Log.e("debug", "Replies Array");
                                if (repliesArray.length() > 0) {
                                    repliesTotal = repliesArray.length();
                                }
                                communityPosts.add(new CommunityPost(j.getObjectId(), title, message, opProfileUrl, opUsername, dateAndTime, repliesTotal, utils.convertJSONtoArrayList(repliesArray), userObjectID));
                                Log.e("debug", Integer.toString(communityPosts.size()));
                            }
                            Log.e("debug", Integer.toString(communityPosts.size()));
                            adapter = new RecyclerViewCommunityPostAdapter(context, communityPosts);
                            adapter.setOnItemClickListener(new RecyclerViewCommunityPostAdapter.onThreadItemClickListener() {
                                @Override
                                public void onItemClickListener(View view, final int position, final CommunityPost post, boolean isLongClicked) {
                                    if (isLongClicked) {
                                        AlertDialog.Builder builder;
                                        builder = new AlertDialog.Builder(context, R.style.customAlertDialog);
                                        builder.setTitle(context.getResources().getString(R.string.deletethread));
                                        if (post.getRepliesTotal() > 0) {
                                            builder.setMessage(context.getResources().getString(R.string.areyousuredeletethreadandcomments));
                                        } else {
                                            builder.setMessage(context.getResources().getString(R.string.areyousuredeletethread));
                                        }
                                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery(COMMUNITY_POSTS_CLASS_NAME);
                                                        query.whereEqualTo("objectId", post.getObjectID());
                                                        query.whereEqualTo(COMMUNITY_POSTS_OP, ParseUser.getCurrentUser());
                                                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                            @Override
                                                            public void done(final ParseObject object, ParseException e) {
                                                                object.deleteInBackground(new DeleteCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            communityPosts.remove(position);
                                                                            adapter.removeItem(communityPosts, position);
                                                                            if (post.getRepliesTotal() > 0) {
                                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery(COMMUNITY_REPLIES_CLASS_NAME);
                                                                                query.whereContainedIn("objectId", post.getRepliesArray());
                                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                                    @Override
                                                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                                                        if (e == null) {
                                                                                             for (ParseObject j : objects) {
                                                                                                 j.deleteInBackground(new DeleteCallback() {
                                                                                                     @Override
                                                                                                     public void done(ParseException e) {
                                                                                                         if (e == null) {
                                                                                                             Log.e("Success", "Successfully Deleted Replies");
                                                                                                         } else {
                                                                                                             Log.e("failed", "failed" + e.getLocalizedMessage());
                                                                                                         }
                                                                                                     }
                                                                                                 });
                                                                                             }
                                                                                        } else {
                                                                                            Log.e("failed", "failed" + e.getLocalizedMessage());
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        } else {
                                                                            Log.e("failed", "failed" + e.getLocalizedMessage());
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert);
                                                AlertDialog dialog = builder.create();
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.show();
                                    } else {
                                        Intent intent = new Intent(CommunityActivity.this, CommunityRepliesActivity.class);
                                        intent.putExtra(POST_KEY, post);
                                        startActivity(intent);
                                    }
                                }
                            });
                            postRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Log.e("debug", "AdapterSet");
                        } catch (Exception e2) {
                            Log.e("debug", e2.getLocalizedMessage());
                            e2.printStackTrace();
                        }
                    } else {
                        postRecyclerView.setVisibility(View.GONE);
                        Log.e("debug", "visibility set to false");
                    }
                } else {
                    Log.e("failed", "failed" + e.getLocalizedMessage());
                }
            }
        });
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(customListener);
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.add_fab:
                    showAddThreadDialog();
                    break;
            }
        }
    };

    private void showAddThreadDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_post);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText threadTitle = dialog.findViewById(R.id.post_title_input);
        final EditText threadContent = dialog.findViewById(R.id.post_content_input);
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
                if (threadTitle.getText().toString().length() == 0) {
                    threadTitle.setError(getResources().getString(R.string.please_enter_title));
                } else if (threadContent.getText().toString().length() == 0) {
                    threadContent.setError(getResources().getString(R.string.please_enter_message));
                } else if (threadTitle.getText().toString().length() == 0 && threadContent.getText().toString().length() == 0) {
                    threadContent.setError(getResources().getString(R.string.no_title_or_message));
                } else {
                    boolean isBadWordMessage = ProfanityFilter.filterText(threadContent.getText().toString());
                    boolean isBadWordTitle = ProfanityFilter.filterText(threadTitle.getText().toString());
                    if (isBadWordMessage && isBadWordTitle) {
                        utils.makeText(context.getResources().getString(R.string.profanity_message_and_title), LENGTH_LONG);
                    } else if (isBadWordTitle){
                        utils.makeText(context.getResources().getString(R.string.profanity_title), LENGTH_LONG);
                    } else if (isBadWordMessage){
                        utils.makeText(context.getResources().getString(R.string.profanity_message), LENGTH_LONG);
                    } else {
                        savePost(threadTitle.getText().toString(), threadContent.getText().toString());
                    }
                }
            }
        });
        dialog.show();
    }

    private void savePost(final String title, final String message) {
        final ParseObject newPost = new ParseObject(COMMUNITY_POSTS_CLASS_NAME);
        newPost.put(COMMUNITY_POSTS_OP, ParseUser.getCurrentUser());
        newPost.put(COMMUNITY_POSTS_COMMUNITY, ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, ParseUser.getCurrentUser().getString(HOME_PARK_KEY)));
        newPost.put(COMMUNITY_POSTS_CONTENT, message);
        newPost.put(COMMUNITY_POSTS_TITLE, title);
        newPost.put(COMMUNITY_POSTS_REPLIES, new JSONArray());
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    utils.makeText(context.getResources().getString(R.string.forumpostsuccess), LENGTH_SHORT);
                    SashidoHelper.earnAchievement(SOCIAL_BUZZ_COL, getString(R.string.social_buzz), context, parentLayout);
                    postRecyclerView.setVisibility(View.VISIBLE);
                    if (adapter != null) {
                        communityPosts.add(0, new CommunityPost(newPost.getObjectId(), title, message, ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE), ParseUser.getCurrentUser().getUsername(), utils.getDateAndTime(new Date()), 0, new ArrayList<String>(), ParseUser.getCurrentUser().getObjectId()));
                        adapter.insertItem(communityPosts, 0);
                    } else {
                        ArrayList<CommunityPost> posts = new ArrayList<>();
                        posts.add(new CommunityPost(newPost.getObjectId(), title, message, ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE), ParseUser.getCurrentUser().getUsername(), utils.getDateAndTime(new Date()), 0, new ArrayList<String>(), ParseUser.getCurrentUser().getObjectId()));
                        adapter = new RecyclerViewCommunityPostAdapter(context, posts);
                        postRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    dialog.cancel();
                } else {
                    utils.makeText(context.getResources().getString(R.string.forumpostfailed), LENGTH_SHORT);
                }
            }
        });
    }

    private class AsyncProfanity extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            ProfanityFilter.loadConfigs();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CommunityActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
