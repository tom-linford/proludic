package icn.proludic.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.models.HomeParkModel;

/**
 * Author:  Bradley Wilson
 * Date: 08/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewHomeParkAdapter extends RecyclerView.Adapter<RecyclerViewHomeParkAdapter.RecyclerViewHomeParkViewHolder> {

    private Context context;
    private ArrayList<HomeParkModel> homeParksList;
    private onHomeParkItemClickListener mItemClickListener;

    public RecyclerViewHomeParkAdapter(Context context, ArrayList<HomeParkModel> homeParksList) {
        Log.e("homeParksSize", String.valueOf(homeParksList.size()));
        this.context = context;
        this.homeParksList = homeParksList;
    }

    @Override
    public RecyclerViewHomeParkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_select_home_park, parent, false);
        return new RecyclerViewHomeParkViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHomeParkViewHolder holder, int position) {
        HomeParkModel model = homeParksList.get(position);
        Log.e("imageURL", model.getImageURL());
//        Picasso.with(context).load(model.getImageURL()).into(holder.homeParkImage);
        holder.homeParkName.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return (null != homeParksList ? homeParksList.size() : 0);
    }

    public void setOnItemClickListener(RecyclerViewHomeParkAdapter.onHomeParkItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onHomeParkItemClickListener {
        void onItemClickListener(View view, int position, String objectId, String name);
    }

    class RecyclerViewHomeParkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView homeParkName;
        private ImageView homeParkImage;

        RecyclerViewHomeParkViewHolder(View itemView) {
            super(itemView);
            this.homeParkName = (TextView) itemView.findViewById(R.id.select_home_name);
            this.homeParkImage = (ImageView) itemView.findViewById(R.id.select_home_park_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), homeParksList.get(getAdapterPosition()).getObjectID(), homeParksList.get(getAdapterPosition()).getName());
            }
        }
    }
}
