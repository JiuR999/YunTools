package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter4.BaseQuickAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.adapter.PictureAdapter;
import cn.swust.jiur.databinding.FragmentProfileCatagoryBinding;
import cn.swust.jiur.entity.Profile;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.viewmodel.JsonDataViewModle;

public class PictureCategoryFragment extends BaseFragment<FragmentProfileCatagoryBinding> {
    public static final int SPAN_COUNT = 2;
    public static final String AVATAR_CATEGORY = "http://service.avatar.adesk.com/v1/avatar/category";
    public static final String PICASSO_CATEGORY = "https://service.picasso.adesk.com/v1/lightwp/category";
    public static final String CATEGORY = "category";
    private JsonDataViewModle viewModle;
    private List<Profile> profiles;
    private PictureAdapter categoryAdapter;
    private NavController navController;
    private String type;
    @Override
    public void initData() {
        navController = Navigation.findNavController(getBinding().getRoot());
        viewModle = new ViewModelProvider(this).get(JsonDataViewModle.class);
        type = PictureCategoryFragmentArgs.fromBundle(getArguments()).getCategoryType();
        ((MainActivity) getActivity()).setToolBarTitle(type);
        initAdapter();
        viewModle.getCatagoryDatas().observe(this, jsonObject -> {
            try {
                JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray(CATEGORY);

                if (type.equals("头像库")) {
                    profiles = com.alibaba.fastjson.JSONObject.parseArray(jsonArray.toString(),
                            Profile.class);
                } else {
                    if (profiles == null) {
                        profiles = new ArrayList<>();
                    }
                    profiles.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String categoryName = jsonObject1.optString("name");
                            String id = jsonObject1.optString("id");
                            String cover = jsonObject1.optString("cover");
                            Profile profile = new Profile(id, categoryName, cover, null);
                            profiles.add(profile);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                categoryAdapter.submitList(profiles);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        if (type.equals("头像库")) {
            FetchDataUtils.fetchData(AVATAR_CATEGORY, viewModle.getCatagoryDatas());
        } else {
            FetchDataUtils.fetchData(PICASSO_CATEGORY, viewModle.getCatagoryDatas());
        }
        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        initRecycleView();
    }

    private void initRecycleView() {
        categoryAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        getBinding().recyclerView.setLayoutManager(gridLayoutManager);
        getBinding().recyclerView.setAdapter(categoryAdapter);
    }

    private void initAdapter() {
        categoryAdapter = new PictureAdapter();
        categoryAdapter.submitList(profiles);
        categoryAdapter.addOnItemChildClickListener(R.id.img_profile_cover, new BaseQuickAdapter.OnItemChildClickListener<Profile>() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<Profile, ?> baseQuickAdapter, @NonNull View view, int i) {
                Profile profile = profiles.get(i);
                String id = profile.getId();
                Bundle args = new PictureCategoryFragmentArgs.Builder()
                        .setProfileId(id)
                        .setProfileCatName(profile.getCategoryName())
                        .setCategoryType(type)
                        .build().toBundle();
                navController.navigate(R.id.navigation_profile,args);
                Log.d("头像_ID", id);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModle.release();
    }

    @Override
    public FragmentProfileCatagoryBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentProfileCatagoryBinding.inflate(inflater, container, false);
    }
}
