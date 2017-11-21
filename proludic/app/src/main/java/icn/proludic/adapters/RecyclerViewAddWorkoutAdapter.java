package icn.proludic.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.models.SimpleExerciseModel;

/**
 * Author: Tom Linford
 * Date: 19/10/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewAddWorkoutAdapter extends RecyclerView.Adapter<RecyclerViewAddWorkoutAdapter.RecyclerViewAddWorkoutViewHolder> {

    private Context context;
    private ArrayList<SimpleExerciseModel> exercisesList;
    private onExerciseClickListener onClickListener;

    public RecyclerViewAddWorkoutAdapter(Context context, ArrayList<SimpleExerciseModel> exercisesList) {
        this.context = context;
        this.exercisesList = exercisesList;
    }

    @Override
    public RecyclerViewAddWorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) inflater.inflate(R.layout.item_row_simple_exercises, parent, false);
        return new RecyclerViewAddWorkoutViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAddWorkoutViewHolder holder, int position) {
        SimpleExerciseModel exercise = exercisesList.get(position);
        holder.name.setText(exercise.getName());
    }

    public interface onExerciseClickListener {
        void onItemClickListener(View view, int position, String id, boolean isSelected);
    }

    public void setOnItemClickListener(RecyclerViewAddWorkoutAdapter.onExerciseClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return (exercisesList != null ? exercisesList.size() : 0);
    }

    class RecyclerViewAddWorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView addRemove;
        RelativeLayout container;
        private boolean isSelected;

        RecyclerViewAddWorkoutViewHolder(View itemView) {
            super(itemView);
            this.container = itemView.findViewById(R.id.container);
            this.name = itemView.findViewById(R.id.name);
            this.addRemove = itemView.findViewById(R.id.add_remove);
            this.container.setOnClickListener(this);
            this.isSelected = false;
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null) {
                onClickListener.onItemClickListener(view, getAdapterPosition(), exercisesList.get(getAdapterPosition()).getId(), this.isSelected);
            }

            if (this.isSelected) {
                this.container.setBackgroundColor(ContextCompat.getColor(context, R.color.unselectedGrey));
                this.addRemove.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
                this.isSelected = false;
            } else {
                this.container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryHalf));
                this.addRemove.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_remove));
                this.isSelected = true;
            }
        }
    }
}
