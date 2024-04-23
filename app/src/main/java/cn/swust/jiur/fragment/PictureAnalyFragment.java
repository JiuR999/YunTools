package cn.swust.jiur.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swust.jiur.adapter.PictureAdapter;
import cn.swust.jiur.databinding.FragmentPictureAnalyBinding;
import cn.swust.jiur.entity.Profile;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.utils.ImageUtils;
import cn.swust.jiur.viewmodel.JsonDataViewModle;

public class PictureAnalyFragment extends BaseFragment<FragmentPictureAnalyBinding> {
    FragmentPictureAnalyBinding binding;
    private JsonDataViewModle viewModle;
    PictureAdapter adapter;
    private List<Profile> profiles;
    private Dialog dialog;

    @Override
    public void initData() {
        binding = getBinding();
        profiles = new ArrayList<>();
        adapter = new PictureAdapter();
        dialog = DialogFactory.loadDialog(getContext());
        dialog.setCancelable(false);
        ImageUtils imageUtils = new ImageUtils(getContext());
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener<Profile>() {
            @Override
            public boolean onLongClick(@NonNull BaseQuickAdapter<Profile, ?> baseQuickAdapter, @NonNull View view, int i) {
                Profile profile = profiles.get(i);
                new MaterialAlertDialogBuilder(getContext())
                        .setItems(new String[]{"保存"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageUtils.downloadImage(profile.getUrl());
                            }
                        }).show();
                return true;
            }
        });
        viewModle = new ViewModelProvider(this).get(JsonDataViewModle.class);
        viewModle.getCatagoryDatas().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                dialog.dismiss();
                if (jsonObject != null) {

                    JSONArray images = jsonObject.optJSONArray("images");
                    if (images != null) {
                        List<String> list = JSON.parseArray(images.toString(), String.class);
                        for (int i = 0; i < list.size(); i++) {
                            Profile profile = new Profile();
                            profile.setUrl(list.get(i));
                            profile.setType("1");
                            //Log.d()
                            profiles.add(profile);
                        }
                        adapter.submitList(profiles);
                    }
                } else {
                    Toast.makeText(getContext(), "解析失败!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.input.inputTextfield.setHint("请输入图集链接");
        String baseUrl = "https://api.pearktrue.cn/api/tuji/api.php?url=";
        binding.input.btnInputSubmit.setOnClickListener(v -> {
            String url = binding.input.editInputText.getText().toString();
            if (!"".equals(url)) {
                Pattern pattern = Pattern.compile("http://[\\w|.|/]*");
                Matcher matcher = pattern.matcher(url);
                while (matcher.find()) {
                    url = matcher.group();
                }
                dialog.show();
                FetchDataUtils.fetchData(baseUrl + url, viewModle.getCatagoryDatas());
            } else {
                Toast.makeText(getContext(), "请先输入链接!", Toast.LENGTH_SHORT).show();
            }

        });

        binding.recycleAnalyResult.setAdapter(adapter);
    }

    @Override
    public FragmentPictureAnalyBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentPictureAnalyBinding.inflate(inflater, container, false);
    }
}
