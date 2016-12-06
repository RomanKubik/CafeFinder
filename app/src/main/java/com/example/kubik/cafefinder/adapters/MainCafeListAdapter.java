package com.example.kubik.cafefinder.adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.helpers.ApiUrlBuilder;
import com.example.kubik.cafefinder.models.BaseCafeInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Adapter for displaying main cafe list.
 */

public class MainCafeListAdapter extends RecyclerView.Adapter<MainCafeListAdapter.ViewHolder> {

    private static final float DEFAULT_MINIMUM_CAFE_RATING = 1;

    private List<BaseCafeInfo> mCafeList;
    private Context mContext;
    private Location mLocation;

    private OnItemClickListener mItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    public MainCafeListAdapter(List<BaseCafeInfo> cafeList, Context context, Location location) {
        this.mCafeList = cafeList;
        this.mContext = context;
        this.mLocation = location;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.cafe_card_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BaseCafeInfo cafe = mCafeList.get(position);
        //Use location to get distance from current location to selected cafe
        Location location = new Location("");
        location.setLatitude(cafe.getGeometry().getLocation().getLat());
        location.setLongitude(cafe.getGeometry().getLocation().getLng());
        //Get main cafe poster
        if (cafe.getPhotos().size() != 0) {
            String url = ApiUrlBuilder.getPhotoUrl(cafe.getPhotos().get(0).getPhotoReference());
            Picasso.with(mContext)
                    .load(url)
                    .fit().centerCrop()
                    .into(holder.imgCafeCard);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.logo_cafe)
                    .into(holder.imgCafeCard);
        }
        holder.tvCafeCardName.setText(cafe.getName());
        if (cafe.getRating() >= DEFAULT_MINIMUM_CAFE_RATING) {
            holder.tvCafeCardRate.setText(String.valueOf(cafe.getRating()));
        } else {
            holder.tvCafeCardRate.setText(String.valueOf(DEFAULT_MINIMUM_CAFE_RATING));
        }

        holder.tvCafeCardAddress.setText(cafe.getVicinity());
        //Get distance
        String distance = String.valueOf((int) location.distanceTo(mLocation)) + mContext.getString(R.string.meters);
        holder.tvCafeCardDestination.setText(distance);

    }

    @Override
    public int getItemCount() {
        return mCafeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_cafe_card)
        ImageView imgCafeCard;
        @BindView(R.id.tv_cafe_card_name)
        TextView tvCafeCardName;
        @BindView(R.id.tv_cafe_card_rate)
        TextView tvCafeCardRate;
        @BindView(R.id.tv_cafe_card_address)
        TextView tvCafeCardAddress;
        @BindView(R.id.tv_cafe_card_destination)
        TextView tvCafeCardDestination;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
