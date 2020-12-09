package com.example.smartify;
import android.graphics.drawable.Drawable;

public class AppList {

    private String name;
    Drawable icon;
   // Drawable tick;


    public AppList(String name, Drawable icon, Drawable tick) {
        this.name = name;
        this.icon = icon;
        //this.tick = tick;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    //public Drawable getTick(){return tick; }
}