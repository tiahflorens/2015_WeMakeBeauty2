package com.jnu.dns.tiah.wemakebeauty.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.BeauTalkItem;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;

import java.util.ArrayList;

/**
 * Created by peter on 2015-05-08.
 */
public class BeauTalkAdapter extends BaseAdapter {
    private ArrayList<BeauTalkItem> list;
    private Context context;

    public BeauTalkAdapter(ArrayList<BeauTalkItem> list, Context context) {
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
        convertView = inflater.inflate(R.layout.item_beautalk, parent, false);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.item_beautalk_tv_title);
        ImageView photo = (ImageView) convertView.findViewById(R.id.item_beautalk_img_photo);

        BeauTalkItem beautalk = list.get(position);

        tvTitle.setText(beautalk.getTitle()+"  @"+beautalk.getNickname());
        photo.setImageBitmap(BitmapFactory.decodeByteArray(beautalk.getPic(),0,beautalk.getPic().length));

        return convertView;
    }
}
