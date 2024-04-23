package cn.swust.jiur.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.QuickAdapterHelper;
import com.chad.library.adapter4.loadState.LoadState;
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.adapter.PictureAdapter;
import cn.swust.jiur.databinding.FragmentProfileBinding;
import cn.swust.jiur.entity.Profile;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.utils.ImageUtils;
import cn.swust.jiur.viewmodel.JsonDataViewModle;

public class ProfileFragment extends BaseFragment<FragmentProfileBinding> {
    public static final int SPAN_COUNT = 2;
    private final String TAG = "ProfileFragment";
    private String baseAvatarUrl, basePicassoUrl, baseUrl;
    private JsonDataViewModle viewModle;
    private List<Profile> profiles;
    private PictureAdapter adapter;
    private QuickAdapterHelper helper;
    private final int LIMIT = 26;
    private int skip = 0;
    private int page = 0;
    private final String PARAM_LIMIT = "limit";
    private final String PARAM_SKIP = "skip";
    private final String PARAM_ORDER = "order";
    private final String PARAM_CID = "cid";
    private boolean isExit = false;
    private String type;
    private Map<String, String> params;
    private ImageUtils imageUtils;
    //limit=26&amp;skip=26&order=new&cid=55f7d54e69401b2286e9e498
    @Override
    public void initData() {
        profiles = new ArrayList<>();
        adapter = new PictureAdapter();
        imageUtils = new ImageUtils(getContext());
        params = new HashMap<>();
        baseAvatarUrl = getActivity().getResources().getString(R.string.service_avatar);
        String profileId = PictureCategoryFragmentArgs.fromBundle(requireArguments())
                .getProfileId();
        String profileCatName = PictureCategoryFragmentArgs.fromBundle(requireArguments())
                .getProfileCatName();
        basePicassoUrl = "https://service.picasso.adesk.com/v1/vertical/category/"
                + profileId + "/vertical?";
        //类型
        type = PictureCategoryFragmentArgs.fromBundle(requireArguments()).getCategoryType();
        if(type.equals("头像库")){
            baseUrl = baseAvatarUrl;
        }else {
            baseUrl = basePicassoUrl;
        }
        
        params.put(PARAM_LIMIT, String.valueOf(LIMIT));
        params.put(PARAM_SKIP, String.valueOf(skip));
        params.put(PARAM_ORDER, "new");
        params.put(PARAM_CID, profileId);
        Log.d(TAG, "图片id:" + profileId);
        ((MainActivity) getActivity()).setToolBarTitle(profileCatName);
        initViewModel();
        initRecycleView();
    }

    private void initViewModel() {
        viewModle = new ViewModelProvider(getActivity()).get(JsonDataViewModle.class);
        viewModle.getCatagoryDatas().observe(getActivity(), jsonObject -> {
            try {
                List<Profile> nProfiles = new ArrayList<>();
                if(jsonObject != null){
                    JSONObject objects = jsonObject.getJSONObject("res");
                    JSONArray pictures = null;
                    if(type.equals("头像库")){
                        pictures = objects.getJSONArray("avatar");
                    } else {
                        pictures = objects.getJSONArray("vertical");
                    }
                    for (int i = 0; i < pictures.length(); i++) {
                        Profile profile = new Profile();
                        if(type.equals("头像库")){
                            profile.setUrl(pictures.getJSONObject(i).optString("thumb"));
                        } else {
                            profile.setUrl(pictures.getJSONObject(i).optString("img"));
                            profile.setType(PictureAdapter.PICASSO);
                        }
                        //JSONArray tag = pictures.getJSONObject(i).getJSONArray("tag");
                        //List<String> tags = JSON.parseArray(tag.toString(), String.class);
                        //Log.d(TAG, tags.toString());
                        nProfiles.add(profile);
                    }
                }
                if (page == 1) {
                    adapter.submitList(nProfiles);

                } else {
                    adapter.addAll( nProfiles);

                }
                helper.setTrailingLoadState(new LoadState.NotLoading(false));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initRecycleView() {
        adapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn);
        adapter.setAnimationFirstOnly(false);
        adapter.setEmptyViewEnable(true);

        adapter.setOnItemLongClickListener((baseQuickAdapter, view, i) -> {
            Profile profile = adapter.getItems().get(i);
            new MaterialAlertDialogBuilder(getContext())
                    .setItems(new String[]{"保存"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imageUtils.downloadImage(profile.getUrl());
                        }
                    }).show();
            return true;
        });

        helper = new QuickAdapterHelper.Builder(adapter)
                .setTrailingLoadStateAdapter(new TrailingLoadStateAdapter.OnTrailingListener() {
                    @Override
                    public void onLoad() {
                        page++;
                        skip = (page - 1) * LIMIT;
                        params.put(PARAM_SKIP, String.valueOf(skip));
                        request(baseUrl, params);
                    }

                    @Override
                    public void onFailRetry() {
                        request(baseUrl, params);
                    }

//                    @Override
//                    public boolean isAllowLoading() {
//                        return page == 0 ;
//                    }
                })
                .build();
        //设置是否还有东西
        helper.setTrailingLoadState(new LoadState.NotLoading(false));

        getBinding().recyclerviewProfile.setAdapter(helper.getAdapter());

    }

    private void request(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl);
        if(params != null){
            for (String key : params.keySet()) {
                url.append("&")
                        .append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        Log.d("头像",url.toString());
//        Map<String,String> headers = new HashMap<>();
//        headers.put("Content-Typ","application/json");
//        headers.put("Transfer-Encoding","chunked");
//        headers.put("User-Agent","okhttp/4.12.0");
//        headers.put("Content-Encoding","gzip");
//        headers.put("Etag","W/\"832f50f96ee930c2c56c497051d39839b54338c4\"");

        FetchDataUtils.fetchData(url.toString(),
                viewModle.getCatagoryDatas());

    }

    @Override
    public FragmentProfileBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModle.release();
        isExit = true;
    }
}
