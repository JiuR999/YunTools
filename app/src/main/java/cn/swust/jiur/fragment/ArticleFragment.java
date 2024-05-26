package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.databinding.FragmentArticleBinding;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.impl.ItemRefreshClickListener;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.utils.SharedPreferenceUtil;
import cn.swust.jiur.viewmodel.ArticleViewModel;

public class ArticleFragment extends BaseFragment<FragmentArticleBinding> {
    public static final String ARTICLE = "article";
    private ArticleViewModel viewModel;
    private FragmentArticleBinding binding;
    private MainActivity mainActivity;
    private String baseUrl;
    private Handler handler;
    @Override
    public void initData() {
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    String[] article = (String[]) msg.obj;
                    Spanned spanned = Html.fromHtml(article[2]);

                    ((MainActivity)getActivity()).setToolBarBuilder(article[0],article[1]);
                    //((MainActivity)getActivity()).setToolBarTitle(title,author);
                    SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING,
                            HomeFragment.M_CACHES,"article_title",article[0]);
                    SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING,
                            HomeFragment.M_CACHES,"article_author",article[1]);
                    SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING,
                            HomeFragment.M_CACHES,ARTICLE,article[2]);
                    binding.materialTextView.setText(spanned);
                }
            }
        };
        binding = getBinding();
        binding.materialTextView.setMovementMethod(new ScrollingMovementMethod());
        baseUrl = "http://htwinkle.cn/article";
        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        mainActivity = (MainActivity)getActivity();
        mainActivity.getMenu().findItem(R.id.item_toolbar_refresh).setVisible(true);
        mainActivity.setItemRefreshClickListener(new ItemRefreshClickListener() {
            @Override
            public void refresh() {
                fetchArticle();
            }
        });
        //mainActivity.setItemRefreshClickListener(() -> FetchDataUtils.fetchData(baseUrl  + "/article",viewModel.getArticleJson()));
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
            Spanned spanned = Html.fromHtml(article);
            String title = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                    HomeFragment.M_CACHES,"article_title");
            String author = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                    HomeFragment.M_CACHES,"article_author");
            ((MainActivity)getActivity()).setToolBarBuilder(title,author);
            binding.materialTextView.setText(spanned);
            /*try {
                JSONObject jsonObject = new JSONObject(article);
                viewModel.getArticleJson().postValue(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }*/
        }else {
            fetchArticle();
            //FetchDataUtils.fetchData(baseUrl  + "/article",viewModel.getArticleJson());
        }

    }

    private void fetchArticle() {
        new Thread(()->{
            try {
                Document document = Jsoup.connect("http://htwinkle.cn/article").get();
                String title = document.getElementsByTag("h2").text();
                String author = document.getElementsByTag("h3").text();
                Log.d("文章","作者"+author+"/标题:"+title);
                Elements p = document.getElementsByTag("p");
                StringBuilder sb = new StringBuilder();
                for (Element element : p) {
                    sb.append("<p>").append(element.text()).append("</p>");
                }
                String [] articles = {title,author,sb.toString()};
                handler.sendMessage(MessageFactory.newMessage(1,articles));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
