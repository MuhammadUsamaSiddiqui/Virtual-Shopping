package com.ubit.scanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHandler {

    private static SharedPreferenceHandler mInstance;
    private static Context mContext;

    private SharedPreferenceHandler(Context context) {
        mContext = context;
    }

    public static synchronized SharedPreferenceHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferenceHandler(context);
        }
        return mInstance;
    }


    public void setOrderID(int orderId){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shopping", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("orderId", orderId);
        editor.apply();
    }

    public int getOrderID(){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("shopping", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("orderId",100);
    }

}
