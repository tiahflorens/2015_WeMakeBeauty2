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
 * Created by peter on 2015-04-26.
 */
public class ReviewAdapter extends BaseAdapter {
    ArrayList<ReviewItem> list;
    Context context;

    public ReviewAdapter(ArrayList<ReviewItem> list, Context context) {
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

              LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_review, parent, false);

            TextView tvIndex, tvProduct, tvBrand;

            tvIndex = (TextView) convertView.findViewById(R.id.item_review_tv_idx);
            tvProduct = (TextView) convertView.findViewById(R.id.item_review_tv_product);
            tvBrand = (TextView) convertView.findViewById(R.id.item_review_tv_brand);
            ImageView img = (ImageView) convertView.findViewById(R.id.item_review_img_photo);

            ReviewItem rev = list.get(position);

            tvIndex.setText((position + 1)+"");
            tvBrand.setText(rev.getBrandName());
            tvProduct.setText(rev.getProductName());
            if (rev.getPic() != null)
                img.setImageBitmap(BitmapFactory.decodeByteArray(rev.getPic(), 0, rev.getPic().length));
            else
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));


        return convertView;
    }
}
