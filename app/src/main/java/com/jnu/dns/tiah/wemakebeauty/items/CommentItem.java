package com.jnu.dns.tiah.wemakebeauty.items;

import java.util.ArrayList;

/**
 * Created by peter on 2015-05-07.
 */
public class CommentItem {
    private String nick,comment;
    private int bid ,uid;
    private byte[] selfie;
    private ArrayList<CommentItem> set;

    public CommentItem(String comment, int bid, int uid) {
        // write request ---------->>
        this.comment = comment;
        this.bid = bid;
        this.uid = uid;
    }

    public CommentItem(String nick, String comment, byte[] selfie) {
        // <<----------- response item
        this.nick = nick;
        this.comment = comment;
        this.selfie = selfie;
    }

    public int getUid() {
        return uid;
    }

    public byte[] getSelfie() {
        return selfie;
    }

    public ArrayList<CommentItem> getSet() {
        return set;
    }

    public String getNick() {
        return nick;
    }

    public String getComment() {
        return comment;
    }

    public int getBid() {
        return bid;
    }
}
