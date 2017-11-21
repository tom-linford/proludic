package icn.proludic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import icn.proludic.R;
import icn.proludic.models.ExercisesModel;

import static icn.proludic.misc.Constants.NO_VIDEO;

/**
 * Author:  Bradley Wilson
 * Date: 17/05/2017
 * Package: icn.proludic.adapters
 * Project Name: proludic
 */

public class RecyclerViewImageOnlyAdapter extends RecyclerView.Adapter<RecyclerViewImageOnlyAdapter.RecyclerViewImageOnlyViewHolder> {

    private Context context;
    private List<ExercisesModel> imageUrls;
    private onThumbnailItemClickListener mItemClickListener;

    public RecyclerViewImageOnlyAdapter(Context context, List<ExercisesModel> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }
    @Override
    public RecyclerViewImageOnlyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.item_row_image_only, parent, false);
        return new RecyclerViewImageOnlyViewHolder(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewImageOnlyViewHolder holder, int position) {
        Picasso.with(context).load(imageUrls.get(position).getImageURL()).into(holder.thumbnail);
        if (imageUrls.get(position).getVideoURL().equals(NO_VIDEO)) {
            holder.play_button.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != imageUrls ? imageUrls.size() : 0);

    }

    public void setOnItemClickListener(onThumbnailItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onThumbnailItemClickListener {
        void onItemClickListener(View view, int position, ExercisesModel model);
    }

    public class RecyclerViewImageOnlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail, play_button;

        public RecyclerViewImageOnlyViewHolder(View itemView) {
            super(itemView);
            this.thumbnail = (ImageView) itemView.findViewById(R.id.image);
            this.play_button = (ImageView) itemView.findViewById(R.id.play_button);
            this.play_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), imageUrls.get(getAdapterPosition()));
            }
        }
    }
}
