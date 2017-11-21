package icn.proludic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.misc.CircleTransform;
import icn.proludic.models.FriendRequestsModel;

import static icn.proludic.misc.Constants.NO_PICTURE;

/**
 * Author:  Bradley Wilson
 * Date: 22/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewFriendRequestsAdapter extends RecyclerView.Adapter<RecyclerViewFriendRequestsAdapter.RecyclerViewFriendRequestsViewHolder>{

    private Context context;
    private ArrayList<FriendRequestsModel> requestsList;
    private onRequestItemClickListener mItemClickListener;

    public RecyclerViewFriendRequestsAdapter(Context context, ArrayList<FriendRequestsModel> requestsList) {
        this.context = context;
        this.requestsList = requestsList;
    }

    @Override
    public RecyclerViewFriendRequestsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_friend_requests, viewGroup, false);
        return new RecyclerViewFriendRequestsViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewFriendRequestsViewHolder holder, int position) {
        FriendRequestsModel model = requestsList.get(position);

        if (model.getImageUrl().equals(NO_PICTURE)) {
            Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(holder.userProfilePicture);
        } else {
            Picasso.with(context).load(model.getImageUrl()).transform(new CircleTransform()).into(holder.userProfilePicture);
        }
        if (model.isChallenge()) {
            holder.reqType.setImageResource(R.drawable.ic_challengereq);
            String name = model.getName().substring(0, 1).toUpperCase()+model.getName().substring(1);
            if (model.isWeight()) {
                holder.userFullName.setText(name + "\n" + " " + context.getResources().getString(R.string.challengeweight));
            } else {
                holder.userFullName.setText(name + "\n" + context.getResources().getString(R.string.challengehearts));
            }
        } else {
            holder.reqType.setImageResource(R.drawable.ic_friendreq);
            holder.userFullName.setText(model.getName());
        }

        if (model.isPending()) {
            holder.buttonsContainer.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
        } else {
            holder.status.setVisibility(View.VISIBLE);
            holder.buttonsContainer.setVisibility(View.GONE);
            if (model.isAccepted()) {
                holder.status.setText(context.getResources().getString(R.string.accepted));
            } else {
                holder.status.setText(context.getResources().getString(R.string.declined));
            }
        }
    }

    public void setOnItemClickListener(onRequestItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void removeItem(ArrayList<FriendRequestsModel> requestsList, int position) {
        this.requestsList = requestsList;
        notifyItemRemoved(position);
    }

    public interface onRequestItemClickListener {
        void onItemClickListener(View view, int position, String objectID, String requestObjectId, boolean isChallenge);
    }


    @Override
    public int getItemCount() {
        return (null != requestsList ? requestsList.size() : 0);
    }

    public class RecyclerViewFriendRequestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userProfilePicture, tick, cross, reqType;
        private TextView userFullName, status;
        private LinearLayout buttonsContainer;

        public RecyclerViewFriendRequestsViewHolder(View itemView) {
            super(itemView);
            this.userProfilePicture = (ImageView) itemView.findViewById(R.id.user_profile_picture);
            this.userFullName = (TextView) itemView.findViewById(R.id.user_full_name);
            this.tick = (ImageView) itemView.findViewById(R.id.accept_friend_request);
            this.cross = (ImageView) itemView.findViewById(R.id.decline_friend_request);
            this.status = (TextView) itemView.findViewById(R.id.statusTV);
            this.buttonsContainer = (LinearLayout) itemView.findViewById(R.id.buttons_container);
            this.reqType = (ImageView) itemView.findViewById(R.id.request_type);
            this.tick.setOnClickListener(this);
            this.cross.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), requestsList.get(getAdapterPosition()).getObjectId(), requestsList.get(getAdapterPosition()).getRequestObjectId(), requestsList.get(getAdapterPosition()).isChallenge());
            }
        }
    }
}
