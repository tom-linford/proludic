package icn.proludic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import icn.proludic.R;

/**
 * Author:  Bradley Wilson
 * Date: 16/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewStatsAdapter extends RecyclerView.Adapter<RecyclerViewStatsAdapter.RecyclerViewStatsViewHolder> {

    private String[] statsList;
    private onStatsClickListener mItemClickListener;

    public RecyclerViewStatsAdapter(String[] statsList) {
        this.statsList = statsList;
    }

    @Override
    public RecyclerViewStatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_stats, parent, false);
        return new RecyclerViewStatsViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewStatsViewHolder holder, int position) {
        holder.stats.setText(statsList[position]);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void setOnItemClickListener(RecyclerViewStatsAdapter.onStatsClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onStatsClickListener {
        void onItemClickListener(View view, int position);
    }

    class RecyclerViewStatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView stats;
        View itemView;
        RecyclerViewStatsViewHolder(View itemView) {
            super(itemView);
            this.stats = (TextView) itemView.findViewById(R.id.stats);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
            stats.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }
}
