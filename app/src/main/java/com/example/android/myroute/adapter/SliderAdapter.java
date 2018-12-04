package com.example.android.myroute.adapter;
//*****************************************************************************************************
//*****************************************************************************************************

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.android.myroute.R;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

//*****************************************************************************************************
//*****************************************************************************************************
public class SliderAdapter extends PagerAdapter {
    //Arrays
    public int[] slide_images = {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c
    };
    //*****************************************************************************************************
    //*****************************************************************************************************
    public String[] slide_headings = {
            "Going Somewhere?",
            "Track your Location",
            "Save yourself the Stress"

    };
    //*****************************************************************************************************
    //*****************************************************************************************************

    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context) {
        this.context = context;
    }

    //*****************************************************************************************************
    //*****************************************************************************************************
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    //*****************************************************************************************************
    //*****************************************************************************************************
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    //*****************************************************************************************************
    //*****************************************************************************************************
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView slideimageview = (ImageView) view.findViewById(R.id.slide_image);
        slideimageview.setImageResource(slide_images[position]);
        container.addView(view);
        return view;
    }

    //*****************************************************************************************************
    //*****************************************************************************************************
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}