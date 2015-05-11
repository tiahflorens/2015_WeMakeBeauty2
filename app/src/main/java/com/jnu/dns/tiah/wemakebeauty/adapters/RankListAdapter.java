package com.jnu.dns.tiah.wemakebeauty.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;

import java.util.ArrayList;

/**
 * Created by PeterYoon on 3/24/15.
 */

public class RankListAdapter extends BaseAdapter {

    private ArrayList<ReviewItem> list;
    private Context context;

    public RankListAdapter(ArrayList<ReviewItem> list, Context context) {
        this.list = list;
        this.context = context;


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        ImageView idx, pre;
        //if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_ranking, parent, false);

        tv = (TextView) convertView.findViewById(R.id.item_ranking_tv_product);
        idx = (ImageView) convertView.findViewById(R.id.item_ranking_img_index);
        pre = (ImageView) convertView.findViewById(R.id.item_ranking_img_preview);


        switch (position){ // index image icon.
            case 0:
                idx.setImageDrawable(context.getResources().getDrawable(R.drawable.no1));
                break;
            case 1:
                idx.setImageDrawable(context.getResources().getDrawable(R.drawable.no2));
                break;
            case 2:
                idx.setImageDrawable(context.getResources().getDrawable(R.drawable.no3));
                break;
        }

        if(position==4){ //0){
            byte[] pic = list.get(position).getPic();
            if (pic != null)
                pre.setImageBitmap(BitmapFactory.decodeByteArray(pic, 0, pic.length));
        }
        tv.setText(list.get(position).getBrandName() + " " + list.get(position).getProductName());

        return convertView;
    }
}
