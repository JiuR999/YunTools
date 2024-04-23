package cn.swust.jiur.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jsoup.Jsoup;

import java.io.IOException;

public class WebViewModel extends ViewModel {
    MutableLiveData<String> html;

    public WebViewModel() {
        this.html = new MutableLiveData<>();
    }

    public MutableLiveData<String> getHtml() {
        return html;
    }

    public void fetchData() {
        // 这里模拟网络请求，你需要替换为真实的网络请求逻辑
        // 例如使用 Retrofit 或 Volley 进行网络请求
        // 在请求成功后，更新LiveData的值，触发UI更新
        // 这里只是模拟数据更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 模拟网络请求
                try {
                    String s = Jsoup.connect("http://htwinkle.cn/article").get().html();
                    // 假设网络请求返回的数据
                    html.postValue(s); // 更新LiveData的值
                }  catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
