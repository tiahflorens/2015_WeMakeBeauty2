package com.jnu.dns.tiah.wemakebeauty.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

/**
 * Created by PeterYoon on 3/25/15.
 */
public class Preferences {
    private SharedPreferences prefs;
    private SharedPreferences.Editor e;

    public Preferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        e = prefs.edit();

    }

    public void save(String tag , String val){
        e.putString(tag,val);
        e.apply();
    }
    public void save(String tag,boolean val){
        e.putBoolean(tag,val);
        e.apply();
    }
    public void save(byte[] data){
        e.putString(Tags.USER_PIC, Base64.encodeToString(data,Base64.DEFAULT));
        e.apply();
    }
    public void save(String tag , int i){
        e.putInt(tag, i);
        e.apply();
    }
    public void save(String tag, long l){
        e.putLong(tag, l);
        e.apply();
    }

    public String getString(String tag){
        return prefs.getString(tag,null);
    }
    public boolean getBoolean( ){return prefs.getBoolean(Tags.AUTO_LOGIN,false);}
    public int getInt(String tag){return prefs.getInt(tag,-1);}
    public long getLong(String tag){return prefs.getLong(tag,-1);}





    public void clear() {
        //모든 저장된 기록을 삭제합니다.
        SharedPreferences.Editor e = prefs.edit();
        e.clear();
        e.apply();
    }
}
