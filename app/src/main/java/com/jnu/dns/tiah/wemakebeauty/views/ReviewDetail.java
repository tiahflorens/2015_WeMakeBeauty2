package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

public class ReviewDetail extends ActionBarActivity {

    private Context context;
    private int rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        rid = getIntent().getIntExtra("rid", -1);

        if (rid < 0)
            exit();
        else
            reqeustDetail();

    }

    public void exit() {
        Toast.makeText(context, " sorry", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public int getRid() {
        return rid;
    }

    public void reqeustDetail() {

        ReviewItem rev = new ReviewItem(getRid());
        String[] params = {Tags.REVIEW_READ_SINGLE, new Gson().toJson(rev)};
        sendReqeust(params);
    }


    public void sendReqeust(String[] p) {
        new AsyncTask<String, Void, String>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage(Tags.MSG_WAIT);
                pd.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                NetworkHandler network = new NetworkHandler();
                return network.sendRequest(Tags.REVIEW, strings[0], strings[1]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();


                if (s == null) {
                    Toast.makeText(context, Tags.MSG_TRY, Toast.LENGTH_SHORT).show();


                } else
                    handleResponse(s);

            }
        }.execute(p);
    }

    public void handleResponse(String raw) {
        ReviewItem rev = new Gson().fromJson(raw, ReviewItem.class);

        TextView tvBrand, tvProduct, tvTitle, tvDetail, tvPrice, tvWriter;
        ImageView img;
        RatingBar rating;

        tvBrand = (TextView) findViewById(R.id.detail_review_tv_brand);
        tvProduct = (TextView) findViewById(R.id.detail_review_tv_product);
        tvTitle = (TextView) findViewById(R.id.detail_review_tv_title);
        tvDetail = (TextView) findViewById(R.id.detail_review_tv_detail);
        tvPrice = (TextView) findViewById(R.id.detail_review_tv_price);
        tvWriter = (TextView) findViewById(R.id.detail_review_tv_nick);
        img = (ImageView) findViewById(R.id.detail_review_img_photo);
        rating = (RatingBar) findViewById(R.id.detail_review_ratingbar);

        tvBrand.setText(rev.getBrandName());
        tvProduct.setText(rev.getProductName());
        tvTitle.setText(rev.getTitle());
        tvDetail.setText(rev.getMemo());
        tvPrice.setText(rev.getPrice() + "원");
        tvWriter.setText(rev.getNickName());
        img.setImageBitmap(BitmapFactory.decodeByteArray(rev.getPic(), 0, rev.getPic().length));
        rating.setRating(rev.getRating());

        rating.setClickable(false);


        RadioButton radioButton1, radioButton2;
        radioButton1 = (RadioButton) findViewById(R.id.detail_review_rb_option1);
        radioButton2 = (RadioButton) findViewById(R.id.detail_review_rb_option2);

        if (rev.getCategory() > 15) { //TODO
            radioButton1.setSelected(true);
            radioButton2.setSelected(false);

        } else {
            radioButton1.setSelected(false);
            radioButton2.setSelected(true);
        }
        radioButton1.setClickable(false);
        radioButton2.setClickable(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
