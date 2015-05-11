package com.jnu.dns.tiah.wemakebeauty.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.BeauTalkItem;
import com.jnu.dns.tiah.wemakebeauty.items.CommentItem;
import com.jnu.dns.tiah.wemakebeauty.items.UserItem;

import java.util.ArrayList;

/**
 * Created by peter on 2015-05-07.
 */
public class CommentAdapter extends BaseAdapter {
    private ArrayList<CommentItem> list;
    private Context context;

    public CommentAdapter(ArrayList<CommentItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_comment, parent, false);

        TextView tvNick = (TextView) convertView.findViewById(R.id.item_comment_tv_nick);
        TextView tvComment = (TextView) convertView.findViewById(R.id.item_comment_tv_comment);
        ImageView photo = (ImageView) convertView.findViewById(R.id.item_comment_img_selfie);


        CommentItem item = list.get(position);
        tvNick.setText(item.getNick());
        tvComment.setText(item.getComment());
        if (item.getSelfie() == null) {
            photo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        } else
            photo.setImageBitmap(BitmapFactory.decodeByteArray(item.getSelfie(), 0, item.getSelfie().length));


        return convertView;
    }
}
