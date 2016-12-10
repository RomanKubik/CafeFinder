package com.example.kubik.cafefinder.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kubik.cafefinder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kubik on 12/6/16.
 */

public class ImagePagerAdapter extends PagerAdapter {

    private List<Bitmap> mImageList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ImagePagerAdapter(List<Bitmap> imageList, Context context) {
        this.mImageList = imageList;
        this.mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateImageList(List<Bitmap> imageList) {
        mImageList = imageList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.viewpager_image_item, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.img_cafe_info);
        imageView.setImageBitmap(mImageList.get(position));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
