package icn.proludic.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

import icn.proludic.R;
import icn.proludic.models.LeaderboardModel;

import static icn.proludic.misc.Constants.ACHIEVEMENTS_NATIONAL;

/**
 * Author:  Bradley Wilson
 * Date: 16/05/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class RecyclerViewLeaderboardAdapter extends RecyclerView.Adapter<RecyclerViewLeaderboardAdapter.RecyclerViewLeaderboardsViewHolder> {

    private Context context;
    private ArrayList<LeaderboardModel> leaderboardList;

    public RecyclerViewLeaderboardAdapter(Context context, ArrayList<LeaderboardModel> leaderboardsList) {
        this.context = context;
        if (getItemCount() > 0) {
            this.leaderboardList.clear();
        }
        this.leaderboardList = leaderboardsList;
    }

    @Override
    public RecyclerViewLeaderboardsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_leaderboards, viewGroup, false);
        return new RecyclerViewLeaderboardsViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewLeaderboardsViewHolder holder, int position) {
        LeaderboardModel model = leaderboardList.get(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.unselectedGrey));
        }
        if (model.getType().equals(ACHIEVEMENTS_NATIONAL)) {
            if (model.getName().equals(ParseUser.getCurrentUser().getString("name"))) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryHalf));
            }
        } else {
            if (model.getUsername().equals(ParseUser.getCurrentUser().getString("username"))) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryHalf));
            }
        }

        if (model.getType().startsWith("Hearts")) {
            holder.total.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_heart), null, null, null);
        } else {
            holder.total.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        int newPosition = position + 1;
        holder.rank.setText("#" + newPosition);
        holder.name.setText(model.getName());
        holder.total.setText(String.valueOf(model.getTotal()));
    }

    @Override
    public int getItemCount() {
        return (null != leaderboardList ? leaderboardList.size() : 0);
    }

    public class RecyclerViewLeaderboardsViewHolder extends RecyclerView.ViewHolder {

        TextView rank, name, total;

        public RecyclerViewLeaderboardsViewHolder(View itemView) {
            super(itemView);
            this.rank = (TextView) itemView.findViewById(R.id.tv_rank);
            this.name = (TextView) itemView.findViewById(R.id.tv_name);
            this.total = (TextView) itemView.findViewById(R.id.tv_total);
        }
    }
}
