package icn.proludic.adapters;

import android.content.Context;
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
 * Date: 26/06/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewCommunityPostAdapter extends RecyclerView.Adapter<RecyclerViewCommunityPostAdapter.RecyclerViewCommunityViewHolder>{

    private Context context;
    private ArrayList<CommunityPost> communityPosts;
    private onThreadItemClickListener mItemClickListener;

    public RecyclerViewCommunityPostAdapter(Context context, ArrayList<CommunityPost> communityPosts) {
        this.context = context;
        this.communityPosts = communityPosts;
    }

    @Override
    public RecyclerViewCommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_thread, parent, false);
        return new RecyclerViewCommunityViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewCommunityViewHolder holder, int position) {
        CommunityPost post = communityPosts.get(position);
        if (post.getProfileImageURL().equals(NO_PICTURE)) {
            Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(holder.profilePicture);
        } else {
            Picasso.with(context).load(post.getProfileImageURL()).transform(new CircleTransform()).into(holder.profilePicture);
        }
        holder.threadTitle.setText(post.getTitle());
        holder.username.setText(post.getProfileUsername() + " - ");
        holder.dateAndTime.setText(post.getProfileTimeAndDate());
        holder.repliesTotal.setText(String.valueOf(post.getRepliesTotal()));
    }

    public void setOnItemClickListener(onThreadItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onThreadItemClickListener {
        void onItemClickListener(View view, int position, CommunityPost post, boolean isLongClicked);
    }

    public void insertItem(ArrayList<CommunityPost> posts, int position) {
        this.communityPosts = posts;
        notifyItemInserted(position);
    }

    public void removeItem(ArrayList<CommunityPost> posts, int position) {
        this.communityPosts = posts;
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return (null != communityPosts ? communityPosts.size() : 0);
    }

    class RecyclerViewCommunityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ImageView profilePicture, commentsImages;
        private TextView threadTitle, username, dateAndTime, repliesTotal;

        RecyclerViewCommunityViewHolder(View itemView) {
            super(itemView);
            this.profilePicture = itemView.findViewById(R.id.user_profile_picture);
            this.threadTitle =  itemView.findViewById(R.id.thread_title);
            this.username =  itemView.findViewById(R.id.user_name);
            this.dateAndTime =  itemView.findViewById(R.id.user_time);
            this.repliesTotal = itemView.findViewById(R.id.posts_total);
            commentsImages = itemView.findViewById(R.id.comments_image);
            commentsImages.setOnClickListener(this);
            repliesTotal.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(view, getAdapterPosition(), communityPosts.get(getAdapterPosition()), false);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (communityPosts.get(getAdapterPosition()).getUserObjectID().equals(ParseUser.getCurrentUser().getObjectId())) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(view, getAdapterPosition(), communityPosts.get(getAdapterPosition()), true);
                }
            }
            return true;
        }
    }


}
