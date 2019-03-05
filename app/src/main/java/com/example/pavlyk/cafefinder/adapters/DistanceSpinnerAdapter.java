package com.example.pavlyk.cafefinder.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pavlyk.cafefinder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for spinner on toolbar.
 * Created by Kubik on 11/6/16.
 */

public class DistanceSpinnerAdapter extends ArrayAdapter<String> {

    private List<String> mData = new ArrayList<>();
    private LayoutInflater inflater;

    public DistanceSpinnerAdapter(Context context, List<String> objects) {
        super(context, R.layout.spinner_item_main_activity, objects);

        mData = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_item_main_activity, parent, false);
        TextView tvCategory = (TextView) view.findViewById(R.id.tv_radius);
        tvCategory.setText(mData.get(position));
        return view;
    }
}
