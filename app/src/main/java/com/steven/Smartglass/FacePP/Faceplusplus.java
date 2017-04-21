package com.steven.Smartglass.FacePP;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.steven.Smartglass.Baidutranslate.TransApi;
import com.steven.Smartglass.Baidutranslate.TransJsonDeco;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;

import static com.steven.Smartglass.ResultActivity.FaceppMSGwhat;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class Faceplusplus extends Thread {

    private File file;
    private String url;
    private String TrScen;
    private String TrObj;
    private String Trupper;
    private String Trlower;
    private TransApi transApi = new TransApi();
    private Handler handler;
    Gson gson = new Gson();

    public Faceplusplus(File file, String url, Handler handler) {
        this.file = file;
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void run() {

        String finalstr = "当前请求识别人数过多，请重试";
        byte[] buff = getBytesFromFile(file);
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "hylmMeoibMoMKo5H-FrMit571QL2e0yQ");
        map.put("api_secret", "rWXV8-Dkf-4LTChwyhStdRjhiD-iSZNy");
        if (url == "https://api-cn.faceplusplus.com/facepp/v3/detect") {
            map.put("return_attributes", "gender,age,ethnicity,smiling,glass,blur");
        }
        if (url == "https://api-cn.faceplusplus.com/humanbodypp/beta/detect") {
            map.put("return_attributes", "gender,cloth_color");
        }

        byteMap.put("image_file", buff);

        try {
            byte[] bacd = post(url, map, byteMap);
            String str = new String(bacd);
            System.out.println("服务器返回的json：" + str);

            //图像识别处理
            if (url == "https://api-cn.faceplusplus.com/imagepp/beta/detectsceneandobject") {
                String scenesvalue = null;
                String objectsvalue = null;
                PicjsonDeco picjsonDeco = gson.fromJson(str, PicjsonDeco.class);
                if ((picjsonDeco.getScenes().size()) > 0) {
                    scenesvalue = picjsonDeco.getScenes().get(0).getValue();
                    TrScen = transApi.getTransResult(scenesvalue, "en", "zh");
                    TransJsonDeco transJsonDeco = gson.fromJson(TrScen, TransJsonDeco.class);
                    TrScen = transJsonDeco.getTrans_result().get(0).getDst();
                }
                if ((picjsonDeco.getObjects().size()) > 0) {
                    objectsvalue = picjsonDeco.getObjects().get(0).getValue();
                    TrObj = transApi.getTransResult(objectsvalue, "en", "zh");
                    TransJsonDeco transJsonDeco = gson.fromJson(TrObj, TransJsonDeco.class);
                    TrObj = transJsonDeco.getTrans_result().get(0).getDst();
                }
                if (scenesvalue == null && objectsvalue == null) {
                    finalstr = "无法识别，请重新拍摄";
                } else if (scenesvalue != null && objectsvalue != null) {
                    finalstr = "您所看到的场景是：" + TrScen + "\n" + "物体是：" + TrObj;
                } else if (scenesvalue == null && objectsvalue != null) {
                    finalstr = "当前物体是：" + TrObj;
                } else {
                    finalstr = "当前场景是：" + TrScen;
                }
            }


            //人体识别处理
            if (url == "https://api-cn.faceplusplus.com/humanbodypp/beta/detect") {
                String gendervalue = null;
                String uppervalue = null;
                String lowervalue = null;
                BodyjsonDeco bodyjsonDeco = gson.fromJson(str, BodyjsonDeco.class);
                if ((bodyjsonDeco.getHumanbodies().size()) > 0) {
                    gendervalue = bodyjsonDeco.getHumanbodies().get(0).getAttributes().getGender().getValue();
                    if (gendervalue.equals("Male")) {
                        gendervalue = "男性";
                    } else {
                        gendervalue = "女性";
                    }
                    uppervalue = bodyjsonDeco.getHumanbodies().get(0).getAttributes().getUpper_body_cloth_color();
                    lowervalue = bodyjsonDeco.getHumanbodies().get(0).getAttributes().getLower_body_cloth_color();
                    Trupper = transApi.getTransResult(uppervalue, "en", "zh");
                    TransJsonDeco upperJsonDeco = gson.fromJson(Trupper, TransJsonDeco.class);
                    Trupper = upperJsonDeco.getTrans_result().get(0).getDst();

                    Trlower = transApi.getTransResult(lowervalue, "en", "zh");
                    TransJsonDeco lowerJsonDeco = gson.fromJson(Trlower, TransJsonDeco.class);
                    Trlower = lowerJsonDeco.getTrans_result().get(0).getDst();
                }

                if (gendervalue == null && uppervalue == null && lowervalue == null) {
                    finalstr = "无法识别，请重新拍摄";
                } else if (uppervalue != null && lowervalue != null) {
                    finalstr = "性别：" + gendervalue + "\n" + "上身衣服颜色是：" + Trupper + "\n" + "下身衣服颜色是：" + Trlower;
                } else if (uppervalue == null && lowervalue != null) {
                    finalstr = "性别：" + gendervalue + "\n" + "下身衣服颜色是：" + Trlower;
                } else {
                    finalstr = "性别：" + gendervalue + "\n" + "上身衣服颜色是：" + Trupper;
                }

            }

            //文字识别处理
            if (url == "https://api-cn.faceplusplus.com/imagepp/beta/recognizetext") {

                String text = "";
                String type = "";
                TextjsonDeco textjson = gson.fromJson(str, TextjsonDeco.class);

                for (int i = 0; i < textjson.getResult().size(); i++) {
                    type = textjson.getResult().get(i).getType();
                    System.out.println("type:" + type);
                    if (type.equals("textline")) {
                        text = text + textjson.getResult().get(i).getValue() + "\n";
                    }
                }
                finalstr = text;
            }

            //人脸识别处理
            if (url == "https://api-cn.faceplusplus.com/facepp/v3/detect") {
                String gendervalue = null;
                int agevalue;
                double smile;
                String smiledf = null;
                String ethnicityvalue = null;
                String glass = null;
                double blur;
                String blurdf = null;
                //DecimalFormat df = new DecimalFormat(".##");
                try {
                    FacejsonDeco facejsonDeco = gson.fromJson(str, FacejsonDeco.class);
                    if (facejsonDeco.getFaces().size() > 0) {
                        gendervalue = facejsonDeco.getFaces().get(0).getAttributes().getGender().getValue();
                        if (gendervalue.equals("Male")) {
                            gendervalue = "男性";
                        } else
                            gendervalue = "女性";
                        agevalue = facejsonDeco.getFaces().get(0).getAttributes().getAge().getValue();
                        ethnicityvalue = facejsonDeco.getFaces().get(0).getAttributes().getEthnicity().getValue();
                        if (ethnicityvalue.equals("Asian")) {
                            ethnicityvalue = "亚洲人";
                        } else if (ethnicityvalue.equals("White")) {
                            ethnicityvalue = "白人";
                        } else
                            ethnicityvalue = "黑人";
                        smile = facejsonDeco.getFaces().get(0).getAttributes().getSmile().getValue();
                        if (smile < 0) {
                            smiledf = "0" + smile;
                        } else
                            smiledf = "" + smile;
                        glass = facejsonDeco.getFaces().get(0).getAttributes().getGlass().getValue();
                        if (glass.equals("None")) {
                            glass = "无";
                        } else if (glass.equals("Dark")) {
                            glass = "黑框眼镜或墨镜";
                        } else
                            glass = "普通眼镜";
                        blur = facejsonDeco.getFaces().get(0).getAttributes().getBlur().getBlurness().getValue();
                        if (blur < 0) {
                            blurdf = "0" + blur;
                        } else
                            blurdf = "" + blur;
                        //blurdf = df.format(blur);

                        finalstr = "性别：" + gendervalue + "\n" + "年龄：" + agevalue + "\n"
                                + "人种：" + ethnicityvalue + "\n" + "微笑值：" + smiledf + "  (满分100)" + "\n"
                                + "佩戴眼镜：" + glass + "\n" + "人脸模糊度：" + blurdf;
                    } else
                        finalstr = "无法识别，请重新拍摄";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.obtainMessage(FaceppMSGwhat, finalstr).sendToTarget();
        /*Message tempMsg = facehandler.obtainMessage();
        tempMsg.obj = finalstr;
        facehandler.sendMessage(tempMsg);*/
    }

    private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();

    protected static byte[] post(String
                                         url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if (fileMap != null && fileMap.size() > 0) {
            Iterator fileIter = fileMap.entrySet().iterator();
            while (fileIter.hasNext()) {
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try {
            if (code == 200) {
                ins = conne.getInputStream();
            } else {
                ins = conne.getErrorStream();
            }
        } catch (SSLException e) {
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while ((len = ins.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }

    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }

    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
}
