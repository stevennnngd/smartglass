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

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
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
    private Button facepic;
    private ImageView imageView;
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
    public static final int Shibie = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        imageView = (ImageView) findViewById(R.id.pic);

        facepic = (Button) findViewById(R.id.facepic);
        facepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCamera = (com.steven.Smartglass.newCamera) findViewById(R.id.newCamera);
                newCamera.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
                turingtv.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        newCamera.takePicture();
                    }
                }, 1000);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        newCamera.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(ResultActivity.this, Facesetting.class);
                        startActivity(intent);
                        ResultActivity.this.finish();
                        facepic.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        turingtv.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
        });

        takepic = (Button) findViewById(R.id.takepic); //xml中设置了不可见
        takepic.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           newCamera = (com.steven.Smartglass.newCamera) findViewById(R.id.newCamera);
                                           newCamera.setVisibility(View.VISIBLE);
                                           tv.setVisibility(View.INVISIBLE);
                                           turingtv.setVisibility(View.INVISIBLE);
                                           new Handler().postDelayed(new Runnable() {
                                               public void run() {
                                                   newCamera.takePicture();
                                               }
                                           }, 1000);
                                           new Handler().postDelayed(new Runnable() {
                                               public void run() {
                                                   newCamera.setVisibility(View.INVISIBLE);
                                                   File tempFile = new File("/sdcard/temp.jpeg");
                                                   String path = tempFile.getAbsolutePath();
                                                   Bitmap bitmap = BitmapFactory.decodeFile(path);
                                                   imageView.setImageBitmap(bitmap);
                                                   facepic.setVisibility(View.VISIBLE);
                                                   tv.setVisibility(View.VISIBLE);
                                                   turingtv.setVisibility(View.VISIBLE);
                                               }
                                           }, 2000);
                                       }
                                   }

        );


        tv = (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        turingtv = (TextView) findViewById(R.id.turing);
        turingtv.setMovementMethod(ScrollingMovementMethod.getInstance());
        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58f0e555");
        final SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
        final VoiceWakeuper mIvw = VoiceWakeuper.createWakeuper(context, null);

        handler = new

                Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        System.out.println("-----------msg.what:" + msg.what);
                        String TTSmsg = msg.obj.toString();
                        System.out.println("-----------TTSmsg:" + TTSmsg);
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
                                new Xunfei_TTS(context, mTts, TTSmsg, handler, mWakeuperListener);
                                break;
                            case UploadMSGwhat:
                                tv.setText(TTSmsg);
                                new Xunfei_TTS(context, mTts, TTSmsg, handler, mWakeuperListener);
                                break;
                            case Shibie:
                                voice.performClick();
                                break;
                        }
                    }
                };


        voice = (Button) findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         new Xunfei_TTS(context, mTts, "我在,请说", handler, mWakeuperListener);
                                         new Handler().postDelayed(new Runnable() {
                                             public void run() {
                                                 Xunfei_Tingxie Tingxiethread = new Xunfei_Tingxie(context, handler, mWakeuperListener);
                                                 Tingxiethread.start();
                                             }
                                         }, 2200);
                                     }
                                 }

        );


        //语音唤醒测试


        //1.加载唤醒词资源，resPath为唤醒资源路径
        StringBuffer param = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/58f0e555.jet");
        param.append(ResourceUtil.IVW_RES_PATH + "=" + resPath);
        param.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
        SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START, param.toString());
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + "芝麻开门");
        //设置当前业务类型为唤醒
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        //设置唤醒一直保持，直到调用stopListening，传入0则完成一次唤醒后，会话立即结束（默认0）
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "0");
        //4.开始唤醒
        mIvw.startListening(mWakeuperListener);
    }


    //听写监听器
    public WakeuperListener mWakeuperListener = new WakeuperListener() {
        public void onResult(WakeuperResult result) {
            String text = result.getResultString();
            System.out.println("----------------语音唤醒:" + text);
            Toast.makeText(context, "语音唤醒开启", Toast.LENGTH_SHORT).show();
            voice.performClick();
        }

        public void onError(SpeechError error) {
        }

        public void onBeginOfSpeech() {
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                //当使用唤醒+识别功能时获取识别结果
                //arg1:是否最后一个结果，1:是，0:否。
                RecognizerResult reslut = ((RecognizerResult) obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                System.out.println("========================唤醒+识别reslut：" + reslut);
            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };


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
        }
    };

    public void Voicestop() {
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
        mTts.stopSpeaking();
        mTts.destroy();
        VoiceWakeuper mIvw = VoiceWakeuper.createWakeuper(context, null);
        mIvw.startListening(mWakeuperListener);
    }


    public void VoiceContorl(String TTSmsg) {

        if (TTSmsg.equals("图像识别")) {
            xIntent("https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject");
        } else if (TTSmsg.equals("人脸识别")) {
            xIntent("https://api-cn.faceplusplus.com/facepp/v3/detect");
        } else if (TTSmsg.equals("文字识别")) {
            xIntent("https://api-cn.faceplusplus.com/imagepp/beta/recognizetext");
        } else if (TTSmsg.equals("人体识别")) {
            xIntent("https://api-cn.faceplusplus.com/humanbodypp/beta/detect");
        } else if (TTSmsg.equals("人脸对比")) {
            xIntent("https://api-cn.faceplusplus.com/facepp/v3/search");
        } else if (TTSmsg.equals("上传")) {
            takepic.performClick();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    upload();
                    Toast.makeText(context, "正在上传，请稍等...", Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        } else if (TTSmsg.equals("语音停止")) {
            Voicestop();
        } else {
            SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(context, null);
            new Xunfei_TTS(context, mTts, TTSmsg, handler, mWakeuperListener);
        }

    }

    public void xIntent(final String url) {
        takepic.performClick();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                facepp(url);
                Toast.makeText(context, "正在识别，请稍等...", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

}







