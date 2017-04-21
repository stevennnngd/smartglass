package com.steven.Smartglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.steven.Smartglass.FacePP.Faceplusplus;
import com.steven.Smartglass.Upload.Upload;
import com.steven.Smartglass.XunFei.Xunfei_TTS;
import com.steven.Smartglass.XunFei.Xunfei_Tingxie;
import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.content.ContentValues.TAG;


public class ResultActivity extends Activity {

    private Button voice;
    private Button takepic;
    private TextView tv;
    private TextView turingtv;
    private static Handler handler;
    private Context context = this;
    private newCamera newCamera = null;
    private SurfaceHolder holder;
    public static final int TuringMSGwhat = 0;
    public static final int TingxieMSGwhat = 1;
    public static final int FaceppMSGwhat = 2;
    public static final int UploadMSGwhat = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        takepic = (Button) findViewById(R.id.takepic);
        takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCamera = (com.steven.Smartglass.newCamera) findViewById(R.id.newCamera);
                newCamera.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        newCamera.takePicture();
                    }
                }, 2000);
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        newCamera.setVisibility(View.INVISIBLE);
                        File tempFile = new File("/sdcard/temp.jpeg");
                        String path = tempFile.getAbsolutePath();
                        ImageView imageView = (ImageView) findViewById(R.id.pic);
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        imageView.setImageBitmap(bitmap);
                    }
                }, 3000);
            }
        });


        tv = (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        turingtv = (TextView) findViewById(R.id.turing);
        turingtv.setMovementMethod(ScrollingMovementMethod.getInstance());
        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58f0e555");
        final SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
        final SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(context, null);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                System.out.println("-----------msg.what:" + msg.what);
                String TTSmsg = msg.obj.toString();
                switch (msg.what) {
                    case TingxieMSGwhat:
                        tv.setText(TTSmsg);
                        Turing(context, TTSmsg);
                        break;
                    case TuringMSGwhat:
                        turingtv.setText(TTSmsg);
                        VoiceContorl(TTSmsg);
                        break;
                    case FaceppMSGwhat:
                        tv.setText(TTSmsg);
                        new Xunfei_TTS(context, mTts, TTSmsg);
                        break;
                    case UploadMSGwhat:
                        tv.setText(TTSmsg);
                        new Xunfei_TTS(context, mTts, TTSmsg);
                        break;

                }
            }
        };


        voice = (Button) findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Xunfei_Tingxie Tingxiethread = new Xunfei_Tingxie(context, handler);
                Tingxiethread.start();
            }
        });


    }


    public void facepp(String url) {
        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
        if (!file.exists()) {
            System.out.println("rcPic pic not exist:" + file.getAbsolutePath());
            return;
        } else {
            System.out.println("rcPic pic dir is:" + file.getAbsolutePath());
        }
        Faceplusplus faceplusplus = new Faceplusplus(file, url, handler);
        faceplusplus.start();
    }

    public void upload() {
        File file = new File(Environment.getExternalStorageDirectory(), "temp.jpeg");
        if (!file.exists()) {
            System.out.println("pic not exist:" + file.getAbsolutePath());
            return;
        } else
            System.out.println("pic dir is:" + file.getAbsolutePath());
        Upload uploadthread = new Upload(file, handler);
        uploadthread.start();
    }


    public void Turing(Context context, String text) {

        TuringManager mTuringManager;
        String TURING_APIKEY = "cbf002b72f5f47d991a13bfd87f27172";
        String TURING_SECRET = "6a01b96f4d898ab5";
        mTuringManager = new TuringManager(context, TURING_APIKEY, TURING_SECRET);
        mTuringManager.setHttpRequestListener(myHttpConnectionListener);
        mTuringManager.requestTuring(text);

    }

    //网络请求回调
    HttpRequestListener myHttpConnectionListener = new HttpRequestListener() {

        @Override
        public void onSuccess(String result) {
            if (result != null) {
                try {
                    Log.d(TAG, "result" + result);
                    JSONObject result_obj = new JSONObject(result);
                    if (result_obj.has("text")) {
                        Log.d(TAG, result_obj.get("text").toString());
                        handler.obtainMessage(TuringMSGwhat, result_obj.get("text").toString()).sendToTarget();
                    }

                } catch (JSONException e) {
                    Log.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onFail(int code, String error) {
            Log.d(TAG, "onFail code:" + code + "|error:" + error);
            //handler.obtainMessage(1, "网络慢脑袋不灵了").sendToTarget();
        }
    };

    public void Voicestop() {
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(context, null);
        mTts.stopSpeaking();
        mTts.destroy();
        mIat.stopListening();
        mIat.destroy();
    }


    public void VoiceContorl(String TTSmsg) {

        if (TTSmsg.equals("图像识别")) {
            xIntent();
            facepp("https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject");
        } else if (TTSmsg.equals("人脸识别")) {
            xIntent();
            facepp("https://api-cn.faceplusplus.com/facepp/v3/detect");
        } else if (TTSmsg.equals("文字识别")) {
            xIntent();
            facepp("https://api-cn.faceplusplus.com/imagepp/beta/recognizetext");
        } else if (TTSmsg.equals("上传")) {
            xIntent();
            upload();
        } else if (TTSmsg.equals("人体识别")) {
            xIntent();
            facepp("https://api-cn.faceplusplus.com/humanbodypp/beta/detect");
        } else if (TTSmsg.equals("语音停止")) {
            Voicestop();
        } else {
            SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
            new Xunfei_TTS(context, mTts, TTSmsg);
        }

    }

    public void xIntent() {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
        Toast.makeText(context, "正在识别，请稍等...", Toast.LENGTH_SHORT).show();
    }

}







