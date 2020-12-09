package com.example.smartify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class InstalledAppsAdapter extends RecyclerView.Adapter<InstalledAppsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AppInfo> mDataSet;

    public InstalledAppsAdapter(Context context, ArrayList<AppInfo> list) {
        mContext = context;
        mDataSet = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewLabel;
        public TextView mTextViewPackage;
        public ImageView mImageViewIcon;
        public CheckBox mAppSelect;
        public RelativeLayout mItem;

        public ViewHolder(View v) {
            super(v);
            // Get the widgets reference from custom layout
            mTextViewLabel = (TextView) v.findViewById(R.id.Apk_Name);
            mTextViewPackage = (TextView) v.findViewById(R.id.Apk_Package_Name);
            mImageViewIcon = (ImageView) v.findViewById(R.id.packageImage);
            mAppSelect = (CheckBox) v.findViewById(R.id.appSelect);
            mItem = (RelativeLayout) v.findViewById(R.id.item);
        }

    }

    @Override
    public InstalledAppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // Get the current package name
        final String packageName = mDataSet.get(position).getAppPackage();

        // Get the current app icon
        Drawable icon = mDataSet.get(position).getAppIcon();

        // Get the current app label
        String label = mDataSet.get(position).getAppName();

        // Set the current app label
        holder.mTextViewLabel.setText(label);

        // Set the current app package name
        holder.mTextViewPackage.setText(packageName);

        // Set the current app icon
        holder.mImageViewIcon.setImageDrawable(icon);

        holder.mAppSelect.setChecked(mDataSet.get(position).isSelected());

        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataSet.get(position).setSelected(!mDataSet.get(position).isSelected());
                InstalledAppsAdapter.this.notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        // Count the installed apps
        return mDataSet.size();
    }

}