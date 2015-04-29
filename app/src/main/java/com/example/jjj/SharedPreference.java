package com.example.jjj;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by project on 2015-04-28.
 */
public class SharedPreference {
    private String TAG = SharedPreference.class.getSimpleName();
    static private SharedPreference mInstance = null;
    private Context mCtx = null;
    static final private String IS_RUNNING = "isRun";
    static final private String STATE_PREFERENCE = "statePreference";

    static public SharedPreference getInstance(Context context){
        if(mInstance == null) {
            mInstance = new SharedPreference();
            mInstance.setContext(context);
        }
        return mInstance;
    }

    private void setContext(Context context){
        mCtx = context;
    }

    public boolean getRunningState(){
        SharedPreferences preferences = mCtx.getSharedPreferences(STATE_PREFERENCE,Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_RUNNING,false);
    }

    public void setRunningState(boolean state){
        SharedPreferences preferences = mCtx.getSharedPreferences(STATE_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_RUNNING,state);
        editor.commit();
    }




}
