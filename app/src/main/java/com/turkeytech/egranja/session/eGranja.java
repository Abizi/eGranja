package com.turkeytech.egranja.session;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import static com.turkeytech.egranja.session.Constants.PRODUCTS_NAME;

public class eGranja extends Application {

    public static boolean HomeViewType;
    public static String SortOrder = PRODUCTS_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}
