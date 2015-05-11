package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.UserItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.io.IOException;

public class SignIn extends ActionBarActivity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROJECTID = "604471277635";
    private EditText etEmail, etPasswd;
    private Context context;
    private GoogleCloudMessaging gcm;
    private String regid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);
        initComponents();

        if (checkPlayServices()) {//dev id를 위한것입니다.
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            Log.d("tiah", "SignIn view regId : " + regid);

            if (regid.isEmpty()) { //dev id가 없다면 등록을 진행함
                registerInBackground();
            }
        } else {
            Log.i("tiah", "No valid Google Play Services APK found.");
        }
    }




    public void initComponents() {
        context = this;

        etEmail = (EditText) findViewById(R.id.signin_et_email);
        etPasswd = (EditText) findViewById(R.id.signin_et_passwd);


        //TextView tvEmail ,tvPasswd;
        //tvEmail = (TextView) findViewById(R.id.textView7);
        // tvPasswd = (TextView) findViewById(R.id.textView6);


        ImageButton ibtnSignIn, ibtnSignUp, ibtnLostandFind;

        ibtnLostandFind = (ImageButton) findViewById(R.id.signin_imgbtn_findpasswd);
        ibtnSignIn = (ImageButton) findViewById(R.id.signin_imgbtn_signin);
        ibtnSignUp = (ImageButton) findViewById(R.id.signin_imgbtn_signup);


        ibtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
        ibtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        ibtnLostandFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPasswd();
            }
        });
    }

    public void findPasswd() {
        if (etEmail.getText().length() == 0) {
            Toast.makeText(this, "input your email first!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserItem user = new UserItem(etEmail.getText().toString());
        sendRequset(new Gson().toJson(user), Tags.USER, Tags.LOSTANDFIND);
    }

    public void login() {
        String email = etEmail.getText().toString();

        if (email.length() == 0) { //이메일 검사
            Toast.makeText(this, Tags.ERROR_EMPTY_EMAIL, Toast.LENGTH_SHORT).show();
            return;
        }

        if (regid == null) { // device id 검사
            Toast.makeText(this, Tags.ERROR_TRY_AGAIN, Toast.LENGTH_SHORT).show();
            return;
        }
        if (regid.length() == 0) {
            Toast.makeText(this, Tags.ERROR_TRY_AGAIN, Toast.LENGTH_SHORT).show();
            return;
        }


        UserItem user = new UserItem(email, etPasswd.getText().toString(), regid);


        sendRequset(new Gson().toJson(user), Tags.USER, Tags.SIGN_IN);

    }

    public void sendRequset(String data, String category, String divistion) {
        String arr[] = {data, category, divistion};
        new AsyncTask<String, Void, String>() {
            //서버로 로그인 요청을 보냅니다.
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

                return network.sendRequest(strings[1], strings[2], strings[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();


                if (s == null) {//응답이 null
                    Toast.makeText(context, Tags.ERROR_TRY_AGAIN, Toast.LENGTH_SHORT).show();
                    Log.d("tiah", "signinView onPostExcute response s is null");
                } else if (s.contains("<html>"))
                    Log.d("tiah", " response contains <html> tag");
                else if (s.equalsIgnoreCase("null"))
                    Log.d("tiah", " response contains null as string");

                else {
                    UserItem user = new Gson().fromJson(s, UserItem.class);
                    handleResponse(user);
                    //showMainView(user);
                }
            }
        }.execute(arr);
    }

    public void handleResponse(UserItem user) {
        if (!user.isDone()) {
            Toast.makeText(context, user.getError(), Toast.LENGTH_SHORT).show();

        } else {
            Preferences prefs = new Preferences(context);

            prefs.save(Tags.AUTO_LOGIN, true);
            prefs.save(Tags.USER_EMAIL, user.getEmail());
            prefs.save(Tags.USER_PASSWD, user.getPasswd());
            prefs.save(Tags.USER_NICKNAME, user.getNickname());
            prefs.save(Tags.USER_BIRTH, user.getBirth());
            prefs.save(user.getPics());
            prefs.save(Tags.USER_ID, user.getUid());
            prefs.save(Tags.SHAPE, user.getShape());
            prefs.save(Tags.TYPE, user.getType());

            if (user.getShape() > 0)
                startActivity(new Intent(SignIn.this, Main.class));
            else
                startActivity(new Intent(SignIn.this, Examine.class));
            finish();
        }


    }




    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("tiah", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("tiah", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("tiah", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences() {//param context
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.

        return getSharedPreferences(SignUp.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(PROJECTID);

                    storeRegistrationId(context, regid);
                } catch (IOException ignored) {

                }
                return null;
            }
        }.execute();

    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);
        Log.i("tiah", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
