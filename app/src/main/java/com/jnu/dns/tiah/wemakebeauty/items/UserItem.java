package com.jnu.dns.tiah.wemakebeauty.items;

import java.util.Calendar;

/**
 * Created by PeterYoon on 3/27/15.
 */
public class UserItem {
    private String email, passwd, devId, nickname;
    private byte[] pics;
    private long birth;
    private int uid, type, shape;
    private String error;
    private boolean done;

    public UserItem(String error, boolean done) {
        //response sign up
        this.error = error;
        this.done = done;
    }

    public UserItem(String email, String passwd, String nickname, String birth, byte[] pics, int shape, int type) {
        this.email = email;
        this.passwd = passwd;
        this.nickname = nickname;
        this.birth = calc(birth);
        this.pics = pics;
        this.shape = shape;
        this.type = type;
    }


    public UserItem(String email, String nickname, byte[] pics, int uid, int type, int shape, String error, boolean done) {
        //response sign in
        this.email = email;
        this.nickname = nickname;
        this.pics = pics;
        this.uid = uid;
        this.type = type;
        this.shape = shape;
        this.error = error;
        this.done = done;
    }

    public UserItem(int uid, String email, String passwd, String nickname, long birth, byte[] pics, int type, int shape) {
        this.email = email;
        this.passwd = passwd;
        this.nickname = nickname;
        this.pics = pics;
        this.birth = birth;
        this.uid = uid;
        this.type = type;
        this.shape = shape;
        this.done = true;


    }

    public UserItem(String email, String passwd, String devId) {
        this.email = email;
        this.passwd = passwd;
        this.devId = devId;
    }

    public UserItem(int uid, String nickname, byte[] pics) {
        this.uid = uid;
        this.nickname = nickname;
        this.pics = pics;
    }


    public UserItem( int uid ,byte[] pics) {
        //examine ------>
        this.pics = pics;
        this.uid = uid;
    }

    public UserItem(int type, int shape, String error, boolean done) {
        //  <----- examine
        this.type = type;
        this.shape = shape;
        this.error = error;
        this.done = done;
    }

    public UserItem(String email) {
        // lost&find ----->
        this.email = email;
    }

    public long calc(String raw) {
        Calendar c = Calendar.getInstance();
        String y = raw.substring(0, 4);
        String m = raw.substring(4, 6);
        String d = raw.substring(6, 8);
        c.set(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d));

        return c.getTimeInMillis();
    }


    public String getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getDevId() {
        return devId;
    }

    public String getNickname() {
        return nickname;
    }

    public byte[] getPics() {
        return pics;
    }

    public long getBirth() {
        return birth;
    }

    public int getUid() {
        return uid;
    }

    public int getType() {
        return type;
    }

    public int getShape() {
        return shape;
    }

    public String getError() {
        return error;
    }

    public boolean isDone() {
        return done;
    }
}
