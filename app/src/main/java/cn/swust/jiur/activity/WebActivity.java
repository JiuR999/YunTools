package cn.swust.jiur.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.IOException;

import cn.swust.jiur.BaseActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.databinding.ActivityWebBinding;
import cn.swust.jiur.utils.WebViewUtil;

public class WebActivity extends BaseActivity {
    private ActivityWebBinding webBinding;
    public  WebView webViewTool;
    private TextView textView;
    private ValueCallback<Uri[]> checkedFile;
    private ActivityResultLauncher<String[]> explorer;
    private WebViewModel webViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webBinding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(webBinding.getRoot());
        webViewModel = new ViewModelProvider(this).get(WebViewModel.class);

        webViewModel.getHtml().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
        String url = getIntent().getStringExtra("url");
        String tip = getIntent().getStringExtra("tip");
        if(url!=null){
            textView = findViewById(R.id.tv_web_tip);
            textView.setText(tip);
            initWebView(url);
           registerResult();
        }
                //webViewModel.fetchData();
    }
    private void registerResult() {
        explorer = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Log.d("Open file `{}`", result.toString());
                        if (checkedFile != null) {
                            if (result == null) {
                                checkedFile.onReceiveValue(null); //result没选择时为null
                            } else {
                                Log.d("Uri",result.toString());
                                checkedFile.onReceiveValue(new Uri[]{result});
                            }
                            checkedFile = null;
                        }
                    }
                });
    }

    private void initWebView(String url) {
        webViewTool = findViewById(R.id.start_code_web);
        WebSettings webSettings = webViewTool.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if(checkedFile != null){
                    checkedFile.onReceiveValue(null);
                }
                checkedFile = filePathCallback;
                String[] accept = fileChooserParams.getAcceptTypes();
                explorer.launch(accept);
                return true;
            }

        };
        webViewTool.setWebChromeClient(webChromeClient);
        webViewTool.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webViewTool.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webViewTool.removeAllViews();
        WebViewUtil.clearCache(this);
    }
}