package cn.swust.jiur.utils;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.util.Map;

public class FetchDataUtils {

    public static void fetchData(String url, MutableLiveData<JSONObject> datas) {
        // 这里模拟网络请求，你需要替换为真实的网络请求逻辑
        // 例如使用 Retrofit 或 Volley 进行网络请求
        // 在请求成功后，更新LiveData的值，触发UI更新
        new Thread(() -> {
            // 模拟网络请求
            JSONObject data = OkHttpUtil.getData(url);
            datas.postValue(data);
        }).start();
    }

    public static void fetchDataWithHeaders(String url, MutableLiveData<JSONObject> datas,
                                 Map<String,String> headers) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 模拟网络请求
                JSONObject data = OkHttpUtil.getData(url,headers);
                datas.postValue(data);
            }
        }).start();
    }
}
