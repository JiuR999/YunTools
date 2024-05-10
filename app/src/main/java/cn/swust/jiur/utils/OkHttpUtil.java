package cn.swust.jiur.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import cn.swust.jiur.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author JiuR
 */
public class OkHttpUtil {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String IMAGE = "image/*";
    private volatile static OkHttpClient okHttpClient;
    public static OkHttpClient getOkHttpClientInstance(){
        if(okHttpClient == null){
            synchronized (OkHttpUtil.class){
                if(okHttpClient == null){
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }
    /**
     *
     * @param url 接口地址
     */
    public static JSONObject getData(String url){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = getOkHttpClientInstance().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            assert response.body() != null;
            String data = response.body().string();

            JSONObject jsonObject = new JSONObject(data);
            Log.d("OKHTTP",jsonObject.toString());
            return jsonObject;
        } catch (IOException | JSONException e) {
            return null;
        }
    }
    /**
     *
     * @param baseUrl 接口地址
     */
    public static JSONObject getDataByParams(String baseUrl, Map<String,Object> params){
        if(params != null){

        }
        Request request = new Request.Builder()
                .url(baseUrl)
                .get()
                .build();
        try {
            Response response = getOkHttpClientInstance().newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            assert response.body() != null;
            String data = response.body().string();

            JSONObject jsonObject = new JSONObject(data);
            Log.d("OKHTTP",jsonObject.toString());
            return jsonObject;
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    /**
     *
     * @param url 接口地址
     */
    public static JSONObject getData(String url,Map<String,String> headers){
        Request.Builder request = new Request.Builder()
                .url(url)
                .get();
        for (String key : headers.keySet()) {
            request.addHeader(key,headers.get(key));
        }
        Request request1 = request.build();
        try {
            Response response = getOkHttpClientInstance().newCall(request1).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            assert response.body() != null;
            String data = response.body().string();

            JSONObject jsonObject = new JSONObject(data);
            Log.d("OKHTTP",jsonObject.toString());
            return jsonObject;
        } catch (IOException | JSONException e) {
            return null;
        }
    }
    public static JSONObject post(String url, RequestBody body) throws IOException, JSONException {

//        MultipartBody.Builder builder1 = new MultipartBody.Builder();
//        for (String key : params.keySet()) {
//            if(key.equals(MULTIPART_FORM_DATA) || key.equals(IMAGE)){
//                builder1.addFormDataPart(key,)
//            }
//            //builder1.add(key, (String) params.get(key));
//        }
        //FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/json;charset=utf-8")
                .addHeader("Transfer-Encoding","chunked")
                .addHeader("Server","cdn")
                .addHeader("Access-Control-Allow-Origin","*")
                .addHeader("Access-Control-Max-Age","86400")
                .addHeader("Vary","Accept-Encoding")
                .addHeader("Strict-Transport-Security","max-age=31536000")
                .post(body)
                .build();
        Call call = getOkHttpClientInstance().newCall(request);
        Response execute = call.execute();
        if(execute.isSuccessful()){
            return new JSONObject(execute.body().string());
        }
        return null;
    }

    public static void uploadFile(Context context, String url){
        File file1 = new File(context.getString(R.string.exter_path)+"OPTION.xls");
        File file2 = new File(context.getString(R.string.exter_path)+"option.xlsx");
        MultipartBody multipartBody = new MultipartBody.Builder()
                .addFormDataPart("image1",file1.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"),file1))
                .addFormDataPart("image2",file2.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"),file2))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Call call = getOkHttpClientInstance().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream in = response.body().byteStream();
                FileUtil.writeFile(in,context.getString(R.string.exter_path)+"different.xlsx");
                in.close();
            }
        });

    }
    public void uploadImg(File file){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("image",file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file));
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("https://tucdn.wpon.cn/api/upload")
                .post(requestBody)
                .build();
    }

}
