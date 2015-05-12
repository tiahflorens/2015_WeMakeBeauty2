package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

public class ReviewWrite extends ActionBarActivity {

    private EditText etBrand, etProduct, etTitle, etMemo, etPrice;
    private ImageView photo;
    private RatingBar rating;
    private Context context;
    private int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review_layout);
        context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        temp();
        setCategory(0);

    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void init() {

        ImageButton btn = (ImageButton) findViewById(R.id.write_review_imgbtn_done);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        RadioButton rb1 = (RadioButton) findViewById(R.id.write_review_rb_option1);
        rb1.setText("SKIN TYPE");
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategory(Tags.SKINTYPE);
            }
        });

        RadioButton rb2 = (RadioButton) findViewById(R.id.write_review_rb_option2);
        rb2.setText("SKIN TONE");
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategory(Tags.SKINTONE);
            }
        });

        etBrand = (EditText) findViewById(R.id.write_review_et_brand);
        etProduct = (EditText) findViewById(R.id.write_review_et_product);
        etTitle = (EditText) findViewById(R.id.write_review_et_title);
        etMemo = (EditText) findViewById(R.id.write_review_et_memo);
        etPrice = (EditText) findViewById(R.id.write_review_et_price);
        photo = (ImageView) findViewById(R.id.write_review_img_photo);
        rating = (RatingBar) findViewById(R.id.write_review_rb_rating);


    }

    public void temp() {
        etBrand.setText("brand  " + ((int) (Math.random() * 10) % 3));
        etProduct.setText("product " + ((int) (Math.random() * 10) % 3));
        etTitle.setText("title " + ((int) (Math.random() * 10) % 3));
        etMemo.setText("memo" + ((int) (Math.random() * 10) % 3));
        etPrice.setText("1500");

    }


    public void toast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void request() {
        if (etBrand.getText().length() == 0 ||
                etProduct.getText().length() == 0 ||
                etTitle.getText().length() == 0 ||
                etMemo.getText().length() == 0 ||
                etPrice.getText().length() == 0) {
            toast("빈칸을 채워주세요");
            return;
        }
        if (getCategory() == 0) {
            toast("종류를 선택해주세요!");
            return;
        }
        Preferences prefs = new Preferences(context);

        ReviewItem rev = new ReviewItem(
                etBrand.getText().toString(),
                etProduct.getText().toString(),
                etMemo.getText().toString(),
                null, //photo
                rating.getRating(),
                etTitle.getText().toString(),
                prefs.getInt(Tags.USER_ID),
                Integer.parseInt(etPrice.getText().toString()),
                prefs.getString(Tags.USER_NICKNAME),
                getCategory());


        send(new Gson().toJson(rev));
    }

    private void send(String args) {

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
            protected String doInBackground(String... params) {

                return new NetworkHandler().sendRequest(Tags.REVIEW, Tags.REVIEW_WRITE, params[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();

                if (s == null)
                    Toast.makeText(context, Tags.MSG_TRY, Toast.LENGTH_SHORT).show();
                else {

                    handleResponse(s);
                }
            }

        }.execute(args);

    }


    public void handleResponse(String raw) {
        ReviewItem rev = new Gson().fromJson(raw, ReviewItem.class);

        toast("작성이 완료되었습니다");

        if (rev.isDone())
            this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_writing, menu);
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
