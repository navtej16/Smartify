package com.example.smartify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

    import com.example.smartify.R;

    public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context){
        this.context=context;
    }
    //Arrays
    public int[] slide_images={
            R.drawable.bg_green,
            R.drawable.bg_location,
            R.drawable.bg_auto_rotate,
            R.drawable.bg_earphone
    };
    public String[] slide_headings={
            "Flip",
            "Location",
            "Auto-Rotate",
            "Earphone"
    };
    public String[] slide_descs={
            "Flip is a feature that can make your life slightly easy. Like many times phone vibrates when you are in the meeting or in between something important. So it helps you to put your phone in do not disturb mode as you flip the phone.",
            "The second feature is the location. As many times it happens when you go to the office, school, etc etc you turn on the wifi and turn off data. So it helps you to reduce that . just feed the location at which you want, set its radius and it will be automatic turn on and off.",
            "The third feature is the auto-rotate. It helps you turn on and off the auto-rotate of the screen automatically. Just select apps you want to do that function and it will rotate.",
            "The fourth feature is earphone-plugin. Like sometimes it is annoying to start the app after earphone is plugged. so this feature helps you to automatically start the app that you selected after earphone is plugged."
    };
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImageView=(ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading=(TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription=(TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
