package com.jnu.dns.tiah.wemakebeauty.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.UserItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Preferences;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;
import com.jnu.dns.tiah.wemakebeauty.others.Work;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class Examine extends ActionBarActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    private ImageView imgView;
    private ImageButton ibtnDone;
    private TextView tvResult;
    private Preferences prefs;
    private Gson gson;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("examine onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examine_layout);
        initComponent();
        addActions();
    }

    private void initComponent() {
        context = this;
        prefs = new Preferences(context);
        gson = new Gson();
        imgView = (ImageView) findViewById(R.id.examine_imgv_photo);
        ibtnDone = (ImageButton) findViewById(R.id.examine_ibtn_done);
        tvResult = (TextView) findViewById(R.id.examine_tv_result);
        setVisibleDone(false);


    }

    private void addActions() {

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();
            }
        });

        ibtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMain();
            }
        });
    }

    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void doTakePhotoAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }


    public void request(byte[] pic) {
        int uid = prefs.getInt(Tags.USER_ID);
        UserItem user = new UserItem(uid, pic);

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

                return new NetworkHandler().sendRequest(Tags.USER, Tags.USER_EXAMINE, params[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pd.dismiss();

                if (s == null)
                    handleResponse(false, Tags.MSG_TRY);
                else if (s.contains("<html>"))
                    handleResponse(false, Tags.MSG_TRY);
                else if (s.equalsIgnoreCase("null"))
                    Log.d("tiah", " response contains null as string");
                else {
                    UserItem user = new Gson().fromJson(s, UserItem.class);
                    handleResponse(user);
                }
            }

        }.execute(gson.toJson(user));
    }

    public void handleResponse(boolean b, String msg) {
        if (!b)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void handleResponse(UserItem user) {

        if (user.isDone()) {//서버측에서 작업을 성공했다면
            setVisibleDone(true);
            int[] arr = new int[2];

            Work work = new Work();
            work.decoding(arr, user.getType());

            //shape , tone , type
            tvResult.setText(
                    "shape : " + work.getShapeInText(user.getShape())+ user.getShape()
                    + "\ntone : " + work.getToneInText(arr[0])+ arr[0]
                    + "\ntype : " + work.getTypeInText(arr[1]) + arr[1] );


            prefs.save(Tags.SHAPE, user.getShape());
            prefs.save(Tags.SKINTONE , arr[0]);
            prefs.save(Tags.SKINTYPE , arr[1]);

            log("shape and type saved");

        }

    }




    public void setVisibleDone(boolean b) {
        if (b)
            ibtnDone.setVisibility(View.VISIBLE);

        else
            ibtnDone.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("result code : " + requestCode);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                log("crop from camera");

                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    imgView.setImageBitmap(photo);
                    request(stream.toByteArray());

                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
                log("pick from album");
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                log("pick from camera");

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                /*intent.putExtra("outputX", 240);
                intent.putExtra("outputY", 240);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                */
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);


                //startActivityForResult(intent, CROP_FROM_CAMERA);

                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    image_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    imgView.setImageBitmap(image_bitmap);
                    request(stream.toByteArray());

                    //배치해놓은 ImageView에 set
                } catch (Exception e) {

                }

                break;
            }
        }
    }


    private void moveToMain() {

        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    private void log(String msg) {
        Log.d("tiah", " examine : " + msg);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_examine, menu);
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
