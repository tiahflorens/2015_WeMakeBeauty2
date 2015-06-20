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

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ReviewWrite extends ActionBarActivity {

    private EditText etBrand, etProduct, etTitle, etMemo, etPrice;
    private ImageView photo;
    private RatingBar rating;
    private Context context;
    private int category;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private byte[] bytePhoto;
    private Uri mImageCaptureUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review_layout);
        context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        //temp();
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

        RadioButton rb1 = (RadioButton) findViewById(R.id.write_review_rb_option1);//SKIN TONE
        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategory(Tags.CATEGORY_TYPE);
            }
        });

        RadioButton rb2 = (RadioButton) findViewById(R.id.write_review_rb_option2); //SKIN TYPE
        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategory(Tags.CATEGORY_TONE);
            }
        });

        etBrand = (EditText) findViewById(R.id.write_review_et_brand);
        etProduct = (EditText) findViewById(R.id.write_review_et_product);
        etTitle = (EditText) findViewById(R.id.write_review_et_title);
        etMemo = (EditText) findViewById(R.id.write_review_et_memo);
        etPrice = (EditText) findViewById(R.id.write_review_et_price);
        photo = (ImageView) findViewById(R.id.write_review_img_photo);
        rating = (RatingBar) findViewById(R.id.write_review_rb_rating);
        photo = (ImageView) findViewById(R.id.write_review_img_photo);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doTakeAlbumAction();
            }
        });


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
                bytePhoto, //photo
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


    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bytePhoto = stream.toByteArray();


                    photo.setImageBitmap(bitmap);
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
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }
}
