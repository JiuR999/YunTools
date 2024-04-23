package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.adapter.TodayOfHistoryAdapter;
import cn.swust.jiur.databinding.FragmentTodayOfHistoryBinding;
import cn.swust.jiur.entity.TodayOfHistory;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.viewmodel.JsonDataViewModle;

public class TodayOfHistoryFragment extends BaseFragment<FragmentTodayOfHistoryBinding>{

    private List<TodayOfHistory> histories;
    private TodayOfHistoryAdapter adapter;
    private JsonDataViewModle viewModle;
    @Override
    public void initData() {
        histories = new ArrayList<>();
        viewModle = new ViewModelProvider(this).get(JsonDataViewModle.class);
        viewModle.getCatagoryDatas().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                    if(jsonObject != null){
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        histories = JSON.parseArray(jsonArray.toString(),TodayOfHistory.class);
                        adapter.submitList(histories);
                    }
            }
        });
        //histories = fetchDatas();
        String url = "https://uapi.woobx.cn/app/histoday?";
        String year = null;
        String month = null;
        String day = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = LocalDateTime.now();
            year = String.valueOf(localDateTime.getYear());
            month = String.valueOf(localDateTime.getMonth().getValue());
            day = String.valueOf(localDateTime.getDayOfMonth());
        }
        String nUrl = url+"month="+month+"&day="+day;

        ((MainActivity)getActivity()).setToolBarBuilder("历史上的今天",year+"年"+month+"月"+day+"日");

        FetchDataUtils.fetchData(nUrl, viewModle.getCatagoryDatas());
        adapter = new TodayOfHistoryAdapter();
        //adapter.submitList(histories);
        getBinding().recycleTodayOfHistory.setAdapter(adapter);
    }

    @Override
    public FragmentTodayOfHistoryBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentTodayOfHistoryBinding.inflate(inflater,container,false);
    }
}
