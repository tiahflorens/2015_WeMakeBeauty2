package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.adapters.BeauTalkAdapter;
import com.jnu.dns.tiah.wemakebeauty.items.BeauTalkItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.util.ArrayList;


public class BeauTalk extends ActionBarActivity {

    private int idx;
    private Context context;
    private ArrayList<BeauTalkItem> list;
    private BeauTalkAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beautalk_layout);
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setIdx(0);
        init();
        refreshList();
    }

    private void init() {
        list = new ArrayList<>();
        lv = (ListView) findViewById(R.id.beauty_lv_list);
        adapter = new BeauTalkAdapter(list, context);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                moveToDetailPage(list.get(position).getId());
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (!list.isEmpty())
                    if (lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1
                            && lv.getChildAt(lv.getChildCount() - 1).getBottom() <= lv.getHeight()) {


                        if (list.size() > visibleItemCount)
                            refreshList();

                    }


            }
        });
    }

    public void moveToDetailPage(int bid) {
        Log.d("tiah", " beautalk . move to detail page");
        Intent intent = new Intent(BeauTalk.this, BeauTalkDetail.class);
        intent.putExtra("bid", bid);
        startActivity(intent);
    }

    private void refreshList() {
        BeauTalkItem bea = new BeauTalkItem(getIdx());
        String[] params = {Tags.BEAUTY_READALL, new Gson().toJson(bea)};
        sendRequest(params);

    }

    private void sendRequest(String[] p) {
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
                return network.sendRequest(Tags.BEAUTY, strings[0], strings[1]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();
                handleResponse(s);


            }
        }.execute(p);
    }

    public void handleResponse(String raw) {
        if (raw == null)
            return;
        BeauTalkItem item = new Gson().fromJson(raw, BeauTalkItem.class);

        setIdx(item.getId());
        list.addAll(item.getSet());
        adapter.notifyDataSetChanged();

    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.write) {
            write();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void write() {

        startActivity(new Intent(BeauTalk.this, BeautalkWrite.class));


    }
}
