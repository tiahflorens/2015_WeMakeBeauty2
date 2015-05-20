package com.jnu.dns.tiah.wemakebeauty.others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jnu.dns.tiah.wemakebeauty.items.EventItem;

/**
 * Created by peter on 2015-05-11.
 */
public class DBAdapter {
    private SQLiteDatabase db;
    private DBOpenHelper dbHelper;

    public DBAdapter(Context _context) {
        dbHelper = new DBOpenHelper(_context, "myDB", null, 1);
    }

    public void close() {
        //액티비티 종료할 때 디비를 꼭 호출해야합니다.
        db.close();
    }

    public void open() throws SQLiteException {
        //디비를 사용하기 전에 꼭 호출해야합니다.
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void insert(EventItem event) {
        Log.d("tiah" , "db.insert " + event.getBrand());
        ContentValues c = new ContentValues();
        c.put("brand", event.getBrand());
        c.put("due", event.getDue());
        c.put("content", event.getMemo());


        db.insert("event", null, c);

    }

    public void insert(String brand, String due , String content){

        ContentValues c = new ContentValues();
        c.put("brand", brand);
        c.put("due", due);
        c.put("content", content);
        db.insert("event", null, c);
    }
    public void delete(){
        db.delete("event",null,null);
    }

    public Cursor get() {
        Log.d("tiah" ,"db.get");
        //return db.rawQuery("select * from event where due>" + System.currentTimeMillis(), null);
        return db.rawQuery("select * from event" , null);

    }


    class DBOpenHelper extends SQLiteOpenHelper {
        //이 클래스는 저도 그냥 복붙해서 사용합니다.

        public DBOpenHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }


        public void onCreate(SQLiteDatabase _db) {
            //여기서 테이블을 생성합니다.
            String s1 = "create table event ( brand text , due long , content text);";

            _db.execSQL(s1);
        }

        public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
                              int _newVersion) {
            Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
                    + " to " + _newVersion
                    + ", which will destroy all old data");

            _db.execSQL("DROP Table IF EXITSTS contact");
            _db.execSQL("DROP Table IF EXITSTS chat");
            onCreate(_db);
        }
    }
}
