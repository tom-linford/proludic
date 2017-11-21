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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import icn.proludic.R;
import icn.proludic.models.ExerciseListModel;

import static android.view.View.GONE;

/**
 * Author:  Bradley Wilson
 * Date: 18/05/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class RecyclerViewExerciseListAdapter extends RecyclerView.Adapter<RecyclerViewExerciseListAdapter.RecyclerViewExerciseListViewHolder> {

    private Context context;
    private ArrayList<ExerciseListModel> exercisesList;
    private boolean isSummary;

    public RecyclerViewExerciseListAdapter(Context context, ArrayList<ExerciseListModel> exerciseList, boolean isSummary) {
        this.context = context;
        this.exercisesList = exerciseList;
        this.isSummary = isSummary;
    }

    @Override
    public RecyclerViewExerciseListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_exercise_list, viewGroup, false);
        return new RecyclerViewExerciseListViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewExerciseListViewHolder holder, int position) {
        ExerciseListModel model = exercisesList.get(position);
        if (!isSummary) {
            Picasso.with(context).load(model.getImageURL()).into(holder.exerciseImage);
        } else {
            holder.exerciseImage.setVisibility(GONE);

            String time = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(model.getFinalTime()),
                    TimeUnit.MILLISECONDS.toSeconds(model.getFinalTime()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(model.getFinalTime()))
            );
            holder.exerciseTime.setText(time);
        }
        holder.exerciseDesc.setText(model.getTotalReps() + " x " + model.getExerciseName());
    }

    @Override
    public int getItemCount() {
        return (null != exercisesList ? exercisesList.size() : 0);
    }

    public class RecyclerViewExerciseListViewHolder extends RecyclerView.ViewHolder {

        private ImageView exerciseImage;
        private TextView exerciseDesc;
        public LinearLayout container;
        public TextView exerciseTime;

        public RecyclerViewExerciseListViewHolder(View itemView) {
            super(itemView);
            this.exerciseImage = (ImageView) itemView.findViewById(R.id.exercise_image);
            this.exerciseDesc = (TextView) itemView.findViewById(R.id.exercise_text);
            this.container = (LinearLayout) itemView.findViewById(R.id.el_container);
            this.exerciseTime = (TextView) itemView.findViewById(R.id.exercise_time);
        }
    }
}
