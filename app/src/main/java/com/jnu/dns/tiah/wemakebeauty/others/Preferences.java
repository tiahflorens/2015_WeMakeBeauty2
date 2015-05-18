package com.jnu.dns.tiah.wemakebeauty.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.jnu.dns.tiah.wemakebeauty.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by PeterYoon on 3/25/15.
 */
public class Preferences {
    private SharedPreferences prefs;
    private SharedPreferences.Editor e;
    private Context context;

    public Preferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
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

        if(data==null)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.signup_photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = stream.toByteArray();

        }
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
