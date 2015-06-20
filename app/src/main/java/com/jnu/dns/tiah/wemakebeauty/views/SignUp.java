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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jnu.dns.tiah.wemakebeauty.R;
import com.jnu.dns.tiah.wemakebeauty.items.UserItem;
import com.jnu.dns.tiah.wemakebeauty.others.NetworkHandler;
import com.jnu.dns.tiah.wemakebeauty.others.Tags;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Pattern;

public class SignUp extends ActionBarActivity {


    private EditText etEmail, etPasswd, etNick, etBirth;
    private Context context;
    private ImageView imgView;
    private ImageButton ibtnDone, ibtnCancel;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private byte[] bytePhoto;
    private Uri mImageCaptureUri;


    TextView tvEmail, tvPasswd, tvBirth, tvNick;
    String EMAIL, PASSWD, NICK, BIRTHDAY, INTRO, DONE, CANCEL, TITLE_EDIT, TITLE_UP, ERROR_FILL, ERROR_VALID, ERROR_SHORT, ERROR_PIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        initCompoents();
        //setComponentsText();
        temp();
        addActions();
    }


    public void setComponentsText() {
        EMAIL = "Email";
        PASSWD = "Password";
        NICK = "Nickname";
        BIRTHDAY = "Birth";
        INTRO = "Introduce";
        DONE = "Done";
        CANCEL = "Cancel";
        TITLE_EDIT = "Edit Profile";
        TITLE_UP = "Sign Up";


        tvEmail.setText(EMAIL);
        tvPasswd.setText(PASSWD);
        tvBirth.setText(BIRTHDAY);
        tvNick.setText(NICK);


    }

    public void temp() {
        ERROR_FILL = "Fill the form please";
        ERROR_PIC = "Touch the icon on the top then select picture for profile";
        ERROR_SHORT = "Password is less than 6 charaters";
        ERROR_VALID = "Invalid email";

    }


    public void initCompoents() {
        context = this;
        imgView = (ImageView) findViewById(R.id.signin_imgv_icon);

        etEmail = (EditText) findViewById(R.id.signup_et_email);
        etPasswd = (EditText) findViewById(R.id.signup_et_passwd);
        etNick = (EditText) findViewById(R.id.signup_et_nickname);
        etBirth = (EditText) findViewById(R.id.signup_et_birth);
        tvEmail = (TextView) findViewById(R.id.textView);
        tvPasswd = (TextView) findViewById(R.id.textView2);
        tvNick = (TextView) findViewById(R.id.textView51);
        tvBirth = (TextView) findViewById(R.id.textView3);
        ibtnDone = (ImageButton) findViewById(R.id.signup_imgbtn_done);
        ibtnCancel = (ImageButton) findViewById(R.id.signup_imgbtn_cancel);


    }

    public void addActions() {
        ibtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqeustSignUp();
            }
        });
        ibtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });


        ibtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqeustSignUp();
            }
        });
        ibtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });


    }


    public void log(String msg) {
        Log.d("tiah", "SignUp " + msg);
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
                    Bitmap photo = extras.getParcelable("data");

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bytePhoto = stream.toByteArray();
                    imgView.setImageBitmap(photo);
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


    public void cancel() {
        this.finish();
    }


    public void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public UserItem user;

    public void reqeustSignUp() {

        //서버로 가입 요청 혹은 수정 요청을 전송합니다.

        //필수 정보가 채워젔는지 확인
        if (etEmail.getText().length() == 0

                || etNick.getText().length() == 0) {

            toast(ERROR_FILL);
            return;
        }

        if (!matched(etEmail.getText().toString())) {//올바른 이메일인지 확인
            toast(ERROR_VALID);
            return;
        }
        //6자 이상

        if (etPasswd.getText().length() < 6) { //비밀번호 길이 확인
            toast(ERROR_SHORT);
            return;
        }

        user = new UserItem(etEmail.getText().toString(), etPasswd.getText().toString(),
                etNick.getText().toString(), etBirth.getText().toString(),
                bytePhoto, -1, -1);


        log("request sign up " + user.getEmail() + " , " + user.getBirth() + " ");
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
                return network.sendRequest(Tags.USER, Tags.SIGN_UP, strings[0]);
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
                    handleResponse(user.isDone(), user.getError());
                }
            }
        }.execute(new Gson().toJson(user));


    }


    public void handleResponse(boolean b, String msg) {

        if (b) {//서버측에서 작업을 성공했다면
            finish();//작업을 완료하고 액티비티 종료
        } else { //요청이 받아들여지지 않았을 때
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean matched(String inputTxt) { //valid email check
        String regex2 = "^([0-9a-zA-Z_\\.-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$";

        return Pattern.matches(regex2, inputTxt);
    }


}
