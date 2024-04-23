package cn.swust.jiur.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class JsonDataViewModle extends ViewModel {
    private MutableLiveData<JSONObject> catagoryDatas;

    public MutableLiveData<JSONObject> getCatagoryDatas() {
        if(catagoryDatas == null){
            catagoryDatas = new MutableLiveData<>();
        }
        return catagoryDatas;
    }
    public void release(){
        catagoryDatas = null;
    }
}
