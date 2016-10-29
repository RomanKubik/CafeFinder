package com.example.kubik.cafefinder.adapters;

import android.content.Context;
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

    private List<BaseCafeInfo> mCafeList;
    private Context mContext;

    public MainCafeListAdapter(List<BaseCafeInfo> cafeList) {
        this.mCafeList = cafeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.cafe_card_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BaseCafeInfo cafe = mCafeList.get(position);
        Picasso.with(mContext).load(ApiUrlBuilder
                .getPhotoUrl(cafe.getPhotos().get(0).getPhotoReference()))
                .fit().centerCrop()
                .into(holder.imgCafeCard);

        holder.tvCafeCardName.setText(cafe.getName());
        holder.tvCafeCardRate.setText(String.valueOf(cafe.getRating()));
        holder.tvCafeCardAddress.setText(cafe.getVicinity());


    }

    @Override
    public int getItemCount() {
        return mCafeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
        }
    }
}
