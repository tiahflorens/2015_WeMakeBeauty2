package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.adapters.RankListAdapter;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.util.ArrayList;


public class Ranking extends ActionBarActivity {

    private Context context;
    private ArrayList<ReviewItem> skinList, typeList;
    private RankListAdapter adapterSkin, adapterType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
        requestForRankItem();

    }


    public void initComponent() {
        context = this;

        skinList = new ArrayList<>();
        typeList = new ArrayList<>();

        adapterSkin = new RankListAdapter(skinList, context);
        adapterType = new RankListAdapter(typeList, context);

        ListView lvSkin = (ListView) findViewById(R.id.rank_lv_skin);
        ListView lvType = (ListView) findViewById(R.id.rank_lv_type);

        lvSkin.setAdapter(adapterSkin);
        lvType.setAdapter(adapterType);

        lvSkin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveToReviewPage(skinList.get(position).getId());
            }
        });

        lvType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveToReviewPage(typeList.get(position).getId());
            }
        });
    }


    public void moveToReviewPage(int pid) {
        Intent intent = new Intent(Ranking.this, Review.class);
        intent.putExtra("pid", pid);
        startActivity(intent);
    }

    public void requestForRankItem() {
        Preferences prefs = new Preferences(context);

        ReviewItem rev = new ReviewItem(prefs.getInt(Tags.USER_ID));

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

                return new NetworkHandler().sendRequest(Tags.REVIEW, Tags.REVIEW_READ_RANK, params[0]);
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

        }.execute(new Gson().toJson(rev));

    }


    public void handleResponse(String raw) {
        if (raw == null)
            return;
        ReviewItem rev = new Gson().fromJson(raw, ReviewItem.class);

        skinList.addAll(rev.getSkinList());
        typeList.addAll(rev.getTypeList());
        adapterSkin.notifyDataSetChanged();
        adapterType.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rank_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
