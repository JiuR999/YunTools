package cn.swust.jiur.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MyUploadWebChromeClient extends WebChromeClient{
    public static final String XLS = "application/vnd.ms-excel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private ActivityResultLauncher<String[]> explorer;
    private Activity activity;
    private int selectRequestCode = 200;
//    public MyUploadWebChromeClient(ActivityResultLauncher<String[]> explorer) {
//        this.explorer = explorer;
//    }

    public MyUploadWebChromeClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        String[] accept = fileChooserParams.getAcceptTypes();
        //explorer.launch(accept);
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType(XLSX);
        activity.startActivityForResult(intent, selectRequestCode);
        return true;
    }
}
