package com.example.android.bookinventory;

import android.app.Application;
import android.content.res.Resources;

/**
 * Created by Eli on 03-Aug-18.
 * https://stackoverflow.com/a/51279729/9132003
 */

public class InventoryApp extends Application {
    private static Resources res;

    @Override
    public void onCreate() {
        super.onCreate();
        res = getResources();
    }

    public static Resources getRes() {
        return res;
    }

}
