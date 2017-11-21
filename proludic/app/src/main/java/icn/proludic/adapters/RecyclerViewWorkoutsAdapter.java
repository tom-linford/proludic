package icn.proludic.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.misc.CircleTransform;
import icn.proludic.models.ExercisesModel;
import icn.proludic.models.WorkoutsModel;

import static icn.proludic.misc.Constants.MOST_USED;

/**
 * Author:  Bradley Wilson
 * Date: 25/04/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewWorkoutsAdapter extends RecyclerView.Adapter<RecyclerViewWorkoutsAdapter.RecyclerViewWorkoutViewHolder> {

    private Context context;
    private ArrayList<WorkoutsModel> workoutsList;
    private onWorkoutItemClickListener mItemClickListener;
    private String selectionType;

    public RecyclerViewWorkoutsAdapter(Context context, ArrayList<WorkoutsModel> workoutsList, String selectionType) {
        this.context = context;
        this.workoutsList = workoutsList;
        this.selectionType = selectionType;
    }

    @Override
    public RecyclerViewWorkoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_exercises, viewGroup, false);
        return new RecyclerViewWorkoutViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewWorkoutViewHolder holder, int position) {
        WorkoutsModel model = workoutsList.get(position);
        holder.exerciseTitle.setText(model.getName());
        if (selectionType.equals(MOST_USED)) {
            holder.exercisesUses.setVisibility(View.VISIBLE);
            holder.favouritesIcon.setVisibility(View.GONE);
            holder.exercisesUses.setText(String.valueOf(model.getUses()));
        } else {
            holder.favouritesIcon.setVisibility(View.VISIBLE);
            holder.exercisesUses.setVisibility(View.GONE);
        }

        if (model.getBranded()) {
            Picasso.with(context).load(model.getBrandImage()).transform(new CircleTransform()).into(holder.brandImage);
        }

        if (model.getAvailability()) {
            holder.containerView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHalfPrimary));
        } else {
            holder.containerView.setBackgroundColor(ContextCompat.getColor(context, R.color.unselectedGrey));
        }

        if (model.getFavourited()) {
            holder.favouritesIcon.setColorFilter(
                    new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
        } else {
            holder.favouritesIcon.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return (null != workoutsList ? workoutsList.size() : 0);
    }

    public void setOnItemClickListener(RecyclerViewWorkoutsAdapter.onWorkoutItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void updateList(ArrayList<WorkoutsModel> temp) {
        workoutsList = temp;
        notifyDataSetChanged();
    }

    public interface onWorkoutItemClickListener {
        void onItemClickListener(View view, int position, String name, String objectId, boolean isFavourited);
    }

    public class RecyclerViewWorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView exerciseTitle, exercisesUses;
        private ImageView favouritesIcon, brandImage;
        private LinearLayout containerView;

        public RecyclerViewWorkoutViewHolder(View itemView) {
            super(itemView);
            this.containerView = (LinearLayout) itemView.findViewById(R.id.e_container);
            this.exerciseTitle = (TextView) itemView.findViewById(R.id.name);
            this.brandImage = itemView.findViewById(R.id.exercise_image);
            this.favouritesIcon = (ImageView) itemView.findViewById(R.id.favourite_icon);
            this.exercisesUses = (TextView) itemView.findViewById(R.id.uses);
            this.containerView.setOnClickListener(this);
            this.exerciseTitle.setOnClickListener(this);
            this.favouritesIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (workoutsList.get(getAdapterPosition()).getAvailability()) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(v, getAdapterPosition(), workoutsList.get(getAdapterPosition()).getName(), workoutsList.get(getAdapterPosition()).getObjectId(), workoutsList.get(getAdapterPosition()).getFavourited());
                }
            }
        }
    }
}
