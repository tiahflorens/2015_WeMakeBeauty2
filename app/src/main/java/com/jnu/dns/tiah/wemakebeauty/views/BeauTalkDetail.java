package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.adapters.CommentAdapter;
import com.jnu.dns.tiah.wemakebeauty.items.BeauTalkItem;
import com.jnu.dns.tiah.wemakebeauty.items.CommentItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.util.ArrayList;

public class BeauTalkDetail extends ActionBarActivity {

    private ArrayList<CommentItem> list;
    private CommentAdapter adapter;
    private Context context;
    private int bid, uid;
    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beau_talk_detail_body_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();

        init();
    }

    public void initComponent() {
        context = this;
        etComment = (EditText) findViewById(R.id.beauty_detail_et_comment);
        list = new ArrayList<>();
        ListView lv = (ListView) findViewById(R.id.beauty_detail_lv_comments);
        View header = getLayoutInflater().inflate(R.layout.beau_talk_detail_header_layout, null, false);
        header.setClickable(false);
        adapter = new CommentAdapter(list, context);
        lv.addHeaderView(header);
        lv.setAdapter(adapter);

        Preferences prefs = new Preferences(context);
        uid = prefs.getInt(Tags.USER_ID);


        bid = getIntent().getIntExtra("bid", -1);
    }

    public void doCute() {
        if (isCute) {
            imageToast(R.drawable.unlike_btn);
            requestCute(false);
            isCute = false;
        } else {
            requestCute(true);
            imageToast(R.drawable.like_btn);
            isCute = true;
        }

    }

    public void imageToast(int d) {
        Toast toast =
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(d);
        toast.setView(imageView);
        toast.setGravity(Gravity.CENTER, 50, 50);
        toast.show();
    }

    public void requestCute(boolean b) { //TODO check its working
        BeauTalkItem item = new BeauTalkItem(uid, bid, b);
        String[] params = {Tags.BEAUTY_CUTE, new Gson().toJson(item)};
        sendRequest(params);
    }

    public void init() {
        if (bid < 0) {
            this.finish();
        }

        BeauTalkItem item = new BeauTalkItem(bid);
        String[] params = {Tags.BEAUTY_READ, new Gson().toJson(item)};
        sendRequest(params);

    }

    public void sendRequest(String[] args) {


        new AsyncTask<String, Void, String>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage(Tags.MSG_WAIT);
                //pd.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                NetworkHandler network = new NetworkHandler();
                return network.sendRequest(Tags.BEAUTY, strings[0], strings[1]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();
                handleResponse(s);
            }

        }.execute(args);
    }


    public void updateUI(BeauTalkItem item) {
        TextView title = (TextView) findViewById(R.id.beauty_detail_tv_title);
        TextView nick = (TextView) findViewById(R.id.beauty_detail_tv_nick);
        TextView memo = (TextView) findViewById(R.id.beauty_detail_tv_memo);
        ImageView photo = (ImageView) findViewById(R.id.beauty_detail_img_photo);

        title.setText(item.getTitle());
        nick.setText(item.getNickname());
        memo.setText(item.getMemo());
        photo.setImageBitmap(BitmapFactory.decodeByteArray(item.getPic(), 0, item.getPic().length));
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkDouble(System.currentTimeMillis());
            }
        });


        isCute = item.getList().contains(uid);

        refreshComments(item.getComments());

        findViewById(R.id.beauty_detail_btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeComment();
            }
        });

    }

    public void handleResponse(String raw) {
        if (raw == null)
            return;
        BeauTalkItem item = new Gson().fromJson(raw, BeauTalkItem.class);


        Log.d("tiah", " item is null return");
        if (item == null)
            return;

        if (item.isComment())
            refreshComments(item.getComments());
        else
            updateUI(item);


    }


    public void refreshComments(ArrayList<CommentItem> _list) {
        if (_list.isEmpty())
            return;

        list.clear();
        list.addAll(_list);
        adapter.notifyDataSetChanged();
    }

    private boolean isCute;
    private long stamp;

    public void checkDouble(long stamp2) {
        Log.d("tiah", "Beautalk detail :  checkoudble");
        if (stamp == 0)
            stamp = stamp2;
        else {
            if ((stamp2 - stamp) < 500) {
                doCute();
            }
            stamp = stamp2;

        }
    }


    public void writeComment() {
        String text = etComment.getText().toString();
        if (text.isEmpty())
            return;
        CommentItem item = new CommentItem(text, bid, uid);
        String params[] = {Tags.BEAUTY_COMMENT, new Gson().toJson(item)};
        sendRequest(params);
        etComment.setText("");

    }


}
