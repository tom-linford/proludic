package icn.proludic.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.models.AchievementModel;

/**
 * Author:  Bradley Wilson
 * Date: 16/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewAchievementsAdapter extends RecyclerView.Adapter<RecyclerViewAchievementsAdapter.RecyclerViewAchievementsViewHolder> {

    private Context context;
    private ArrayList<AchievementModel> achievementsList;
    private onAchievementItemClickListener mItemClickListener;

    public RecyclerViewAchievementsAdapter(Context context, ArrayList<AchievementModel> achievementsList) {
        this.context = context;
        this.achievementsList = achievementsList;
    }

    @Override
    public RecyclerViewAchievementsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_achievements, parent, false);
        return new RecyclerViewAchievementsViewHolder(mainGroup);
    }

    public void setOnItemClickListener(onAchievementItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onAchievementItemClickListener {
        void onItemClickListener(View view, int position, String name, String description, String image, int weight);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAchievementsViewHolder holder, int position) {
        AchievementModel model = achievementsList.get(position);
        if (!model.isLocked()) {
            holder.lockedImage.setVisibility(View.VISIBLE);
            holder.achievementImage.setBackgroundColor(ContextCompat.getColor(context, R.color.shadedGrey));
            holder.achievementImage.clearColorFilter();
        } else {
            holder.lockedImage.setVisibility(View.GONE);
            holder.achievementImage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryHalf));

            if (!model.isStatus()) {
                holder.achievementImage.setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
            } else {
                holder.achievementImage.clearColorFilter();
            }
        }
        Picasso.with(context).load(model.getImage()).into(holder.achievementImage);
        holder.achievementHearts.setText(String.valueOf(model.getHearts()));
    }

    @Override
    public int getItemCount() {
        return (null != achievementsList ? achievementsList.size() : 0);
    }

    class RecyclerViewAchievementsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView achievementHearts;
        ImageView achievementImage, lockedImage;

        RecyclerViewAchievementsViewHolder(View itemView) {
            super(itemView);
            this.achievementHearts = (TextView) itemView.findViewById(R.id.achievement_hearts);
            this.achievementImage = (ImageView) itemView.findViewById(R.id.achievements_image);
            this.lockedImage = (ImageView) itemView.findViewById(R.id.locked);
            this.achievementImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), achievementsList.get(getAdapterPosition()).getName(), achievementsList.get(getAdapterPosition()).getDescription(), achievementsList.get(getAdapterPosition()).getImage(), achievementsList.get(getAdapterPosition()).getWeight());
            }
        }
    }
}
