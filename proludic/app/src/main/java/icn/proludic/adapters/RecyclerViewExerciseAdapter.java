package icn.proludic.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import icn.proludic.models.ExercisesModel;

import static icn.proludic.misc.Constants.BROWSE_ALL;
import static icn.proludic.misc.Constants.MOST_USED;

/**
 * Author:  Bradley Wilson
 * Date: 25/04/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewExerciseAdapter extends RecyclerView.Adapter<RecyclerViewExerciseAdapter.RecyclerViewExercisesViewHolder> {

    private Context context;
    private ArrayList<ExercisesModel> exercisesList;
    private onExerciseItemClickListener mItemClickListener;
    private String selectionType;

    public RecyclerViewExerciseAdapter(Context context, ArrayList<ExercisesModel> exercisesList, String selectionType) {
        this.context = context;
        this.exercisesList = exercisesList;
        this.selectionType = selectionType;
    }

    @Override
    public RecyclerViewExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_exercises, viewGroup, false);
        return new RecyclerViewExercisesViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewExercisesViewHolder holder, int position) {
        ExercisesModel model = exercisesList.get(position);

        if (model.getName().equals("Arm Curl")) {
            Log.e("mostused", Boolean.toString(model.getAvailability()));
        }
        switch (selectionType) {
            case MOST_USED:
                holder.exercisesUses.setVisibility(View.VISIBLE);
                holder.favouritesIcon.setVisibility(View.GONE);
                holder.exercisesUses.setText(String.valueOf(model.getUses()));
                break;
            case BROWSE_ALL:
                holder.favouritesIcon.setVisibility(View.VISIBLE);
                holder.exercisesUses.setVisibility(View.GONE);
                break;
            default:
                holder.favouritesIcon.setVisibility(View.VISIBLE);
                holder.exercisesUses.setVisibility(View.GONE);
                break;
        }
        Picasso.with(context).load(model.getImageURL()).transform(new CircleTransform()).into(holder.exerciseImage);
        holder.exerciseTitle.setText(model.getName());
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

    public void setOnItemClickListener(RecyclerViewExerciseAdapter.onExerciseItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void updateList(ArrayList<ExercisesModel> temp) {
        exercisesList = temp;
        notifyDataSetChanged();
    }

    public interface onExerciseItemClickListener {
        void onItemClickListener(View view, int position, String name, String objectId, boolean isFavourited);
    }

    @Override
    public int getItemCount() {
        return (null != exercisesList ? exercisesList.size() : 0);
    }

    class RecyclerViewExercisesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView exerciseTitle, exercisesUses;
        ImageView favouritesIcon, exerciseImage;
        LinearLayout containerView;

        RecyclerViewExercisesViewHolder(View itemView) {
            super(itemView);
            this.containerView = (LinearLayout) itemView.findViewById(R.id.e_container);
            this.exerciseTitle = (TextView) itemView.findViewById(R.id.name);
            this.favouritesIcon = (ImageView) itemView.findViewById(R.id.favourite_icon);
            this.exercisesUses = (TextView) itemView.findViewById(R.id.uses);
            this.exerciseImage = (ImageView) itemView.findViewById(R.id.exercise_image);
            this.containerView.setOnClickListener(this);
            this.exerciseTitle.setOnClickListener(this);
            this.favouritesIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (exercisesList.get(getAdapterPosition()).getAvailability()) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(v, getAdapterPosition(), exercisesList.get(getAdapterPosition()).getName(), exercisesList.get(getAdapterPosition()).getObjectId(), exercisesList.get(getAdapterPosition()).getFavourited());
                }
            }
        }
    }
}


