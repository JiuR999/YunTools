package cn.swust.jiur.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class ArticleViewModel extends ViewModel {
    private MutableLiveData<JSONObject> articleJson;

    public MutableLiveData<JSONObject> getArticleJson() {
        if(articleJson == null){
            articleJson = new MutableLiveData<>();
        }
        return articleJson;
    }

    public void release() {
        articleJson = null;
    }
}
