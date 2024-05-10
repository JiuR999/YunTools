package cn.swust.jiur;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.swust.jiur.constant.MessageConstant;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.fragment.AnalyVedioFragment;
import cn.swust.jiur.utils.ImageUtils;
import cn.swust.jiur.utils.OkHttpUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    private static final String TAG = NewAppWidget.class.getSimpleName();
    public static final String APP_WIDGET_ID = "appWidgetId";
    private FetchDataTask fetchDataTask;
    int cnt = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals("webview_button_clicked")) {
            //Intent webViewIntent = new Intent(context, AnalyVedioFragment.class);
            // 设置WebView的URL
            //webViewIntent.putExtra("url", "http://www.example.com");
            // 在Activity中打开WebView
            //webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(webViewIntent);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int appWidgetId = intent.getIntExtra(APP_WIDGET_ID,0);
                updateAppWidget(context,appWidgetManager,appWidgetId);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.appwidget_text,v);
        this.fetchDataTask = new FetchDataTask(views, appWidgetManager, appWidgetId);
        views.setViewVisibility(R.id.progress_bar, View.VISIBLE);
        // 添加按钮点击事件
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction("webview_button_clicked");
        intent.putExtra(APP_WIDGET_ID,appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        // 发起网络请求
        this.fetchDataTask.execute();
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {
        private final RemoteViews views;

        private final AppWidgetManager appWidgetManager;
        private final int appWidgetId;

        FetchDataTask(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
            this.views = views;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
        }

        @Override
        protected String doInBackground(Void... params) {
            // 在这里进行网络请求,并返回结果
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://cocfz.com/")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Document document = Jsoup.parse(response.body().string());
                    Elements img = document.getElementsByTag("img");
                    String src = img.get(1).attr("src");
                    return src;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return "Hello!";
        }

        @Override
        protected void onPostExecute(String result) {
            // 更新 TextView 的文本
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            views.setTextViewText(R.id.appwidget_text,format
                    .format(new Date(System.currentTimeMillis())));
            try {
                String substring = result.substring(result.indexOf(",")+1);
                views.setImageViewBitmap(R.id.appwidget_img_start,ImageUtils.base64ToImg(substring));
                views.setViewVisibility(R.id.progress_bar, View.GONE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}