package com.jnu.dns.tiah.wemakebeauty.items;

import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.util.ArrayList;

/**
 * Created by PeterYoon on 3/25/15.
 */
public class BeauTalkItem {

    private int id, idx;
    private byte[] pic;
    private String memo, title, nickname;
    private ArrayList<Integer> list;
    private ArrayList<BeauTalkItem> set;
    private ArrayList<CommentItem> comments;


    public BeauTalkItem(int id) {
        //  read --------->>
        this.id = id;
    }

    public BeauTalkItem(int idx, ArrayList<BeauTalkItem> set) {
        // <<------------response
        this.idx = idx;
        this.set = set;
    }

    public BeauTalkItem(int id, byte[] pic, String memo, String title, String nickname, ArrayList<Integer> list) {

        // << ----------- response item
        this.id = id;
        this.pic = pic;
        this.memo = memo;
        this.title = title;
        this.nickname = nickname;
        this.list = list;
    }

    public BeauTalkItem(int id, byte[] pic, String memo, String title, String nickname, ArrayList<Integer> list, ArrayList<CommentItem> comments) {
        // << ----------- response item

        this.id = id;
        this.pic = pic;
        this.memo = memo;
        this.title = title;
        this.nickname = nickname;
        this.list = list;
        this.comments = comments;
    }

    public BeauTalkItem(byte[] pic, String memo, String title, String nickname) {
        // write ----------->>
        this.pic = pic;
        this.memo = memo;
        this.title = title;
        this.nickname = nickname;
    }


    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public byte[] getPic() {
        return pic;
    }

    public String getMemo() {
        return memo;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNickname() {
        return nickname;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public ArrayList<BeauTalkItem> getSet() {
        return set;
    }
}
