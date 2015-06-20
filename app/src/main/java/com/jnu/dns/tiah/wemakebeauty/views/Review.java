package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.adapters.ReviewAdapter;
import com.jnu.dns.tiah.wemakebeauty.items.ReviewItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.util.ArrayList;

public class Review extends ActionBarActivity implements SearchView.OnQueryTextListener {


    private ArrayList<ReviewItem> list;
    private ReviewAdapter adapter;
    private Context context;
    private int currentIndex;
    private Preferences prefs;
    private boolean mLockListView, searchFlag;
    private ListView lv;
    private Gson gson;
    private String tag;
    private int pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //requestNormalReviews();
        //testAdd();

        pid = getIntent().getIntExtra("pid", -1);
        log("pid : " + pid);

        init();
        if (pid > 0)
            requestForRakingProducts();

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log("onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(outState);
        log("onSaveInstanceState");
    }

    public void setTag(String tag) {
        log("set Tag : " + tag);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void init() {
        setLock(true);
        setSearchFlag(false);
        currentIndex = -1;
        gson = new Gson();
        context = this;
        prefs = new Preferences(context);

        list = new ArrayList<>();
        //backup = new ArrayList<>();

        adapter = new ReviewAdapter(list, context);
        lv = (ListView) findViewById(R.id.listView);
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
                        log("onScroll reached bottom  : " + mLockListView);
                        log("visible item count : " + visibleItemCount);


                        if(pid<0)
                            if (list.size() > visibleItemCount)
                                if (searchFlag)
                                    requestSearchReviews();
                                else
                                    requestNormalReviews();
                        }
                        //testAdd();

            }
        });
    }

    public void requestForRakingProducts() {
        log("request Search for Ranking Items");
        ReviewItem rev= new ReviewItem(prefs.getInt(Tags.USER_ID) , pid);
        runAsync(Tags.REVIEW , Tags.REVIEW_READ_RANKING_REVIEWS , gson.toJson(rev));


    }

    public void setSearchFlag(boolean b) {
        log("search flag : " + b);
        if (searchFlag != b)
            list.clear(); //TODO

        searchFlag = b;
    }

    public void setLock(boolean b) {
        log("set lock  : " + b);
        mLockListView = b;
    }


    public void moveToDetailPage(int rid) {
        Intent intent = new Intent(Review.this, ReviewDetail.class);
        intent.putExtra("rid", rid);
        startActivity(intent);
    }


    public void requestSearchReviews() {
        log("request Search Reviews ");

        setLock(true);
        ReviewItem rev = new ReviewItem(prefs.getInt(Tags.USER_ID), getCurrentIndex(), getTag());

        runAsync(Tags.REVIEW, Tags.REVIEW_READ_SEARCH, gson.toJson(rev));

    }

    public void requestNormalReviews() {
        log("request Normal Reviews");
        setLock(true);
        ReviewItem rev = new ReviewItem(prefs.getInt(Tags.USER_ID), getCurrentIndex());
        runAsync(Tags.REVIEW, Tags.REVIEW_READ_SET, gson.toJson(rev));
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void runAsync(String category, String division, String data) {
        final String args[] = {category, division, data};
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
                return network.sendRequest(strings[0], strings[1], strings[2]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();

                setLock(false); // this could be a problem

                if (s == null) {
                    Toast.makeText(context, Tags.MSG_TRY, Toast.LENGTH_SHORT).show();


                } else
                    handleResponse(s);

            }
        }.execute(args);

    }

    public void handleResponse(String raw) {

        ReviewItem rev = gson.fromJson(raw, ReviewItem.class);
        if (rev.getSet() == null) {
            return;
        }
        if (rev.getSet().size() != 0) {
            refreshList(rev.getSet());
            if(pid<0)
            setCurrentIndex(rev.getIdx());
        }
    }

    public void refreshList(ArrayList<ReviewItem> data) {

        list.addAll(data);
        adapter.notifyDataSetChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_review, menu);


        MenuItem serachItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(serachItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);

        }
        Log.d("tiah", " is null?? " + (searchView == null));


        return true;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.write:

                startActivity(new Intent(Review.this, ReviewWrite.class));
                Toast.makeText(context, "write actionbar!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.search:
                EditText editText = new EditText(context);
                getSupportActionBar().setCustomView(editText);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        log("onQeuryTextSubmit : " + query);
        setSearchFlag(true);
        setTag(query);
        setCurrentIndex(0);
        requestSearchReviews();
        return false;
    }

    public void setCurrentIndex(int idx) {
        this.currentIndex = idx;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.isEmpty()) {
            log("onQeuryTextChange  isEmpty!");
            setSearchFlag(false);

        }

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
        if(pid<0) {
            list.clear();
            currentIndex = -1;
            requestNormalReviews();

        }

    }

    public void log(String msg) {
        Log.d("tiah", "Review.class :  " + msg);
    }
}
