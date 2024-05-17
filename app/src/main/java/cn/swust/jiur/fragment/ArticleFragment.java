package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.databinding.FragmentArticleBinding;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.utils.SharedPreferenceUtil;
import cn.swust.jiur.viewmodel.ArticleViewModel;

public class ArticleFragment extends BaseFragment<FragmentArticleBinding> {
    public static final String ARTICLE = "article";
    private ArticleViewModel viewModel;
    private FragmentArticleBinding binding;
    private MainActivity mainActivity;
    private String baseUrl;

    @Override
    public void initData() {
        binding = getBinding();
        binding.materialTextView.setMovementMethod(new ScrollingMovementMethod());
        baseUrl = getActivity().getResources().getString(R.string.my_host);
        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        mainActivity = (MainActivity)getActivity();
        mainActivity.getMenu().findItem(R.id.item_toolbar_refresh).setVisible(true);
        mainActivity.setItemRefreshClickListener(() -> FetchDataUtils.fetchData(baseUrl  + "/article",viewModel.getArticleJson()));
        viewModel.getArticleJson().observe(this, jsonObject -> {
            try {
                if(jsonObject.optJSONObject("data") != null){
                    jsonObject = jsonObject.getJSONObject("data");
                }
                String title = jsonObject.optString("title");
                String author = jsonObject.optString("author");
                //binding.tvArticleTitle.setText(title);

                //binding.tvArticleAuthor.setText();
                ((MainActivity)getActivity()).setToolBarBuilder(title,author);
                //((MainActivity)getActivity()).setToolBarTitle(title,author);
                binding.materialTextView.setText(jsonObject.optString("content"));
                SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING,
                        HomeFragment.M_CACHES,ARTICLE,jsonObject.toString());

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });
        String article = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                HomeFragment.M_CACHES, ARTICLE);
        if (!"".equals(article)){
            try {
                JSONObject jsonObject = new JSONObject(article);
                viewModel.getArticleJson().postValue(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }else {
            FetchDataUtils.fetchData(baseUrl  + "/article",viewModel.getArticleJson());
        }

    }

    @Override
    public void onDestroy() {
        mainActivity.getMenu().findItem(R.id.item_toolbar_refresh).setVisible(false);
        viewModel.release();
        super.onDestroy();
    }

    @Override
    public FragmentArticleBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentArticleBinding.inflate(inflater, container, false);
    }
}
