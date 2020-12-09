package com.example.smartify;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class earphoneAdapter extends ArrayAdapter<ApplicationInfo> {
    private List<ApplicationInfo> appsList1 = null;
    private Context context1;
    private PackageManager packageManager1;
    private ArrayList<Boolean> checkList1 = new ArrayList<Boolean>();
    private int counter=0,lastAppInt=-1;


    public earphoneAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context1 = context;
        this.appsList1 = appsList;
        packageManager1 = context.getPackageManager();

        for (int i = 0; i < appsList.size(); i++) {
            checkList1.add(false);
        }
    }

    @Override
    public int getCount() {
        return ((null != appsList1) ? appsList1.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList1) ? appsList1.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context1
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row1, null);
        }

        ApplicationInfo data = appsList1.get(position);
        if (null != data) {
            if (data.packageName!=ExampleService.lastAppString)
            { TextView appName = (TextView) view.findViewById(R.id.app_name1);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage1);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon1);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_app1);
            checkBox.setTag(Integer.valueOf(position)); // set the tag so we can identify the correct row in the listener
            checkBox.setChecked(checkList1.get(position)); // set the status as we stored it
            checkBox.setOnCheckedChangeListener(mListener); // set the listener
            appName.setText(data.loadLabel(packageManager1));
            packageName.setText(data.packageName);
            iconview.setImageDrawable(data.loadIcon(packageManager1));}
            else if(data.packageName==ExampleService.lastAppString) {
                Log.i("package name","matched");
                TextView appName = (TextView) view.findViewById(R.id.app_name1);
                TextView packageName = (TextView) view.findViewById(R.id.app_paackage1);
                ImageView iconview = (ImageView) view.findViewById(R.id.app_icon1);

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_app1);
                checkBox.setTag(Integer.valueOf(position)); // set the tag so we can identify the correct row in the listener
                checkList1.set(position,true);
                checkBox.setChecked(checkList1.get(position)); // set the status as we stored it
                checkBox.setOnCheckedChangeListener(mListener); // set the listener
                appName.setText(data.loadLabel(packageManager1));
                packageName.setText(data.packageName);
                iconview.setImageDrawable(data.loadIcon(packageManager1));
                notifyDataSetChanged();
            }
        }
        return view;
    }

    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           //if(counter==0){
               if (isChecked&&counter==0&&lastAppInt==-1) {
                   checkList1.set((Integer) buttonView.getTag(), isChecked);// get the tag so we know the row and store the status
                   lastAppInt=(Integer) buttonView.getTag();
                   Log.i("last app", "onCheckedChanged:true "+Integer.toString(lastAppInt));
                   counter++;
                   lastAppInt=(Integer) buttonView.getTag();
                   ApplicationInfo currentapp= appsList1.get(lastAppInt);
                   ExampleService.lastAppString = currentapp.packageName;

               }else if (isChecked&&counter>0&&lastAppInt!=(Integer) buttonView.getTag()){
                   checkList1.set((Integer) buttonView.getTag(), isChecked);
                   Log.i("is checked",  Integer.toString((Integer) buttonView.getTag()));
                   checkList1.set(lastAppInt, false);
                   lastAppInt=(Integer) buttonView.getTag();
                   ApplicationInfo currentapp= appsList1.get(lastAppInt);
                   ExampleService.lastAppString = currentapp.packageName;
                   Log.i("last app", "onCheckedChanged:false "+Integer.toString(lastAppInt));
                   notifyDataSetChanged();
               }
               else if (!isChecked&&counter>0&&lastAppInt==(Integer) buttonView.getTag()){
                   counter--;
                   lastAppInt=-1;
                   ExampleService.lastAppString=null;
                   checkList1.set((Integer) buttonView.getTag(), isChecked);
               }
            notifyDataSetChanged();

            /*if(isChecked){
                counter++;
            }
            else if(!isChecked){
                counter--;
            }}*/
          /* else if(isChecked) {
               Toast.makeText(context1, "Select only one", Toast.LENGTH_SHORT).show();

           }*/
        }
    };
}
