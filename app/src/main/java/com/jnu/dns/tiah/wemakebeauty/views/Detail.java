package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

public class Detail extends ActionBarActivity {

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

    public void requestLike() {
        Preferences prefs = new Preferences(context);
        ReviewItem rev = new ReviewItem(getRid(), prefs.getInt(Tags.USER_ID));
        String[] params = {Tags.REVIEW_LIKE, new Gson().toJson(rev)};
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

        TextView tvBrand, tvProduct, tvTitle, tvMemo, tvPrice, tvWriter;
        ImageView img;
        RatingBar rating;
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
