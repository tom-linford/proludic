package icn.proludic.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.models.NearestParkModel;

/**
 * Author:  Bradley Wilson
 * Date: 22/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewNearestParkAdapter extends RecyclerView.Adapter<RecyclerViewNearestParkAdapter.RecyclerViewNearestParkViewHolder>{
    private ArrayList<NearestParkModel> nearestParkList;
    private Context context;
    private onNearestParkItemClickListener mItemClickListener;

    public RecyclerViewNearestParkAdapter(Context context, ArrayList<NearestParkModel> nearestParkList) {
        this.context = context;
        this.nearestParkList = nearestParkList;
    }

    @Override
    public RecyclerViewNearestParkViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_nearest_park, viewGroup, false);
        return new RecyclerViewNearestParkViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewNearestParkViewHolder holder, int position) {
        NearestParkModel model = nearestParkList.get(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.unselectedGrey));
        }
        holder.tvName.setText(model.getName());
        holder.tvDistance.setText(model.getDistance());
    }
    public void setOnItemClickListener(onNearestParkItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onNearestParkItemClickListener {
        void onItemClickListener(View view, int position, NearestParkModel model);
    }


    @Override
    public int getItemCount() {
        return null != nearestParkList ? nearestParkList.size() : 0;
    }

    public class RecyclerViewNearestParkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName, tvDistance;
        private ImageView viewMapButton;

        public RecyclerViewNearestParkViewHolder(View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tv_name);
            this.tvDistance = itemView.findViewById(R.id.tv_distance);
            this.viewMapButton = itemView.findViewById(R.id.view_map_button);
            this.viewMapButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(view, getAdapterPosition(), nearestParkList.get(getAdapterPosition()));
            }
        }
    }
}
