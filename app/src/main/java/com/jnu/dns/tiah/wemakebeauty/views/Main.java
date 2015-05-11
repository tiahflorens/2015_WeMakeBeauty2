package com.jnu.dns.tiah.wemakebeauty.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;


public class Main extends ActionBarActivity {


    private DrawerLayout dlDrawer;
    private ActionBarDrawerToggle dtToggle;
    private boolean isDrawerOpen;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.main_layout);
        initComponents();
        setNavigator();


    }


    private void initComponents() {
        ImageView a, b, c, d;
        a = (ImageView) findViewById(R.id.main_imgBbtn0);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(v.getId());
            }
        });

        b = (ImageView) findViewById(R.id.main_imgBbtn1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(v.getId());
            }
        });

        c = (ImageView) findViewById(R.id.main_imgBbtn2);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(v.getId());
            }
        });


        d = (ImageView) findViewById(R.id.main_imgBbtn3);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(v.getId());
            }
        });

    }

    private void log(String msg) {
        Log.d("tiah", "Main--> " + msg);
    }

    public void open(int id) {

        Intent intent = null;
        switch (id) {

            case R.id.main_imgBbtn0:
                intent = new Intent(Main.this, Ranking.class);
                break;
            case R.id.main_imgBbtn1:
                intent = new Intent(Main.this, BeauTalk.class);
                break;
            case R.id.main_imgBbtn2:
                intent = new Intent(Main.this, Review.class);
                break;
            case R.id.main_imgBbtn3:
                intent = new Intent(Main.this, Map.class);
                break;
        }

        if (intent != null)
            startActivity(intent);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO
        if (item.getItemId() == R.id.refresh) {//액션바 오른쪽에 해당하는 아이콘 아이디입니다.

            return true;
        }

        return dtToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    public void logout() {
        Preferences prefs = new Preferences(context);
        prefs.clear();
        startActivity(new Intent(Main.this, SignIn.class));
        this.finish();
    }

    public void setNavigator() {
        //네비게이터, 메뉴를 초기화하고 등록합니다.
       /* navList = (ListView) findViewById(R.id.home_lv_drawer1);
        navAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, navItems);
        navList.setAdapter(navAdapter);
        navList.setOnItemClickListener(new DrawerItemClickListener());
*/
        Preferences prefs = new Preferences(context);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageButton logout = (ImageButton) findViewById(R.id.drawer_ibtn_signout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();
            }
        });

        ImageView photo = (ImageView) findViewById(R.id.drawer_img_selfie);
        String raw = prefs.getString(Tags.USER_PIC);
        if (raw != null) {
            byte[] src = Base64.decode(raw, Base64.DEFAULT);
            photo.setImageBitmap(BitmapFactory.decodeByteArray(src, 0, src.length));
        } else
            photo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));

        TextView tvNick = (TextView) findViewById(R.id.drawer_tv_nick);
        tvNick.setText(prefs.getString(Tags.USER_NICKNAME));


        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.drawable.menu,
                R.string.dopen, R.string.dclose) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isDrawerOpen = true;
                log("drawer opend!");
            }

        };
        dlDrawer.setDrawerListener(dtToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public void onBackPressed() {
        if (isDrawerOpen) {
            dlDrawer.closeDrawers();
        } else
            super.onBackPressed();

    }


}
