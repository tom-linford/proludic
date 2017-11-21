package icn.proludic.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.misc.CircleTransform;
import icn.proludic.models.CommunityPost;

import static icn.proludic.misc.Constants.NO_PICTURE;

/**
 * Author:  Bradley Wilson
 * Date: 27/06/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewCommunityReplyAdapter extends RecyclerView.Adapter<RecyclerViewCommunityReplyAdapter.RecyclerViewCommunityReplyViewHolder>{
    private Context context;
    private ArrayList<CommunityPost> threadReplies;
    private onReplyItemClickListener mItemClickListener;
    private boolean isComment;

    public RecyclerViewCommunityReplyAdapter(Context context, ArrayList<CommunityPost> threadReplies, boolean isComment) {
        this.context = context;
        this.threadReplies = threadReplies;
        this.isComment = isComment;
    }

    @Override
    public RecyclerViewCommunityReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_thread_reply, parent, false);
        return new RecyclerViewCommunityReplyViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewCommunityReplyViewHolder holder, int position) {
        CommunityPost post = threadReplies.get(position);
        if (post.getProfileImageURL().equals(NO_PICTURE)) {
            Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(holder.profilePicture);
        } else {
            Picasso.with(context).load(post.getProfileImageURL()).transform(new CircleTransform()).into(holder.profilePicture);
        }
        holder.username.setText(post.getProfileUsername());
        holder.dateAndTime.setText(post.getProfileTimeAndDate());
        holder.threadContent.setText(post.getMessage());

        if (post.getRepliesTotal() > 0) {
            holder.viewMoreComments.setVisibility(View.VISIBLE);
        }

        if (isComment) {
            holder.replyButton.setVisibility(View.INVISIBLE);
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }


    }

    @Override
    public int getItemCount() {
        return (null != threadReplies ? threadReplies.size() : 0);
    }

    public void updateList(ArrayList<CommunityPost> threadReplies) {
        this.threadReplies = threadReplies;
        notifyDataSetChanged();
    }
    public void insertItem(ArrayList<CommunityPost> threadReplies, int position) {
        this.threadReplies = threadReplies;
        notifyItemInserted(position);
    }

    public void setOnItemClickListener(onReplyItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onReplyItemClickListener {
        void onItemClickListener(View view, int position, CommunityPost post, boolean isLongClicked, CardView repliesContainer, TextView commentsTV, boolean isReply);
    }

    class RecyclerViewCommunityReplyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private ImageView profilePicture, reportButton, replyButton;
        private TextView threadContent, username, dateAndTime, viewMoreComments;
        private CardView repliesContainer;

        RecyclerViewCommunityReplyViewHolder(View itemView) {
            super(itemView);
            this.profilePicture = itemView.findViewById(R.id.user_profile_picture);
            this.username =  itemView.findViewById(R.id.user_name);
            this.dateAndTime =  itemView.findViewById(R.id.user_time);
            this.threadContent = itemView.findViewById(R.id.thread_content);
            this.replyButton = itemView.findViewById(R.id.reply_button);
            this.reportButton = itemView.findViewById(R.id.report_button);
            this.viewMoreComments = itemView.findViewById(R.id.view_more_comments);
            this.repliesContainer = itemView.findViewById(R.id.replies_container);
            replyButton.setOnClickListener(this);
            reportButton.setOnClickListener(this);
            viewMoreComments.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if (threadReplies.get(getAdapterPosition()).getUserObjectID().equals(ParseUser.getCurrentUser().getObjectId())) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(view, getAdapterPosition(), threadReplies.get(getAdapterPosition()), true, repliesContainer, viewMoreComments, false);
                }
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.reply_button:
                    mItemClickListener.onItemClickListener(view, getAdapterPosition(), threadReplies.get(getAdapterPosition()), false, repliesContainer, viewMoreComments, true);
                    break;
                default:
                    mItemClickListener.onItemClickListener(view, getAdapterPosition(), threadReplies.get(getAdapterPosition()), false, repliesContainer, viewMoreComments, false);
                    break;
            }
        }
    }
}
