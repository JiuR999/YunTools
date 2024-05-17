package cn.swust.jiur.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import cn.swust.jiur.R;
import cn.swust.jiur.constant.MessageConstant;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.factory.MarkwonFactory;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.fragment.MusicFragment;
import cn.swust.jiur.impl.KeyHandler;
import io.noties.markwon.LinkResolver;
import io.noties.markwon.Markwon;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;

public class UpdateUtil {
    private Context context;
    private String newVersion = "";
    private String key = "";
    private Handler handler;
    private final String vNamePath = "/getVersionName";
    private final String updateContentPath = "/getVersionContent";

    public UpdateUtil(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void isUpdate(Handler mHandler) throws IOException {
        key = (String) SharedPreferenceUtil.readData(context, SharedPreferenceUtil.Type.STRING,
                MusicFragment.KEYFILENAME, "key");
//        KeyCheckHelper.checkKey(context, new KeyHandler() {
//            @Override
//            public void handleKey(Handler handler,String key) {
//                JSONObject jsonObject =  OkHttpUtil.getData(key + vNamePath);
//
//                if (jsonObject != null) {
//                    newVersion = jsonObject.optString("data");
//                    try {
//                        String localVersion = getLocalVersion(context);
//                        Log.d("当前版本", localVersion);
//                        Log.d("最新版本", newVersion);
//                        if (!newVersion.equals(localVersion)) {
//                            //获取更新内容
//                            JSONObject jsonObject1 = OkHttpUtil.getData(key + updateContentPath);
//                            assert jsonObject1 != null;
//                            String updateContent = jsonObject1.optString("data");
//                            String content = "\n" + updateContent;
//                            handler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_SHOW_UPDATE_DIALOG, content));
//                        } else {
//                            handler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_NOT_UPDATE, null));
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_INPUT_KEY, "秘钥已经过期或未输入秘钥"));
//                }
//            }
//        },mHandler);
        String url = context.getResources().getString(R.string.api_version);
        Document document = Jsoup.connect(url).get();
        newVersion = document.getElementById("version").text();
        String updateContent = document.getElementById("content").text();
        updateContent = updateContent.replaceAll("L","\n");
        if (newVersion != null && updateContent != null) {
            try {
                String localVersion = getLocalVersion(context);
                Log.d("当前版本", localVersion);
                Log.d("最新版本", newVersion);
                if (!newVersion.equals(localVersion)) {
                    //获取更新内容
                    String content = "\n" + updateContent;
                    mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_SHOW_UPDATE_DIALOG, content));
                } else {
                    mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_NOT_UPDATE, null));
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_INPUT_KEY, "秘钥已经过期或未输入秘钥"));
        }
/*        if (!key.isEmpty()) {
            String url = "https://jiur999.github.io/Mypages/";
            Document document = Jsoup.connect(url).get();
            newVersion = document.getElementById("version").text();
            String updateContent = document.getElementById("content").text();
            if (newVersion != null && updateContent != null) {
                try {
                    String localVersion = getLocalVersion(context);
                    Log.d("当前版本", localVersion);
                    Log.d("最新版本", newVersion);
                    if (!newVersion.equals(localVersion)) {
                        //获取更新内容
                        String content = "\n" + updateContent;
                        mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_SHOW_UPDATE_DIALOG, content));
                    } else {
                        mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_NOT_UPDATE, null));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_INPUT_KEY, "秘钥已经过期或未输入秘钥"));
            }
        } else {
            mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_UPDATE_INPUT_KEY, "秘钥已经过期或未输入秘钥"));
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String getLocalVersion(Context context) throws PackageManager.NameNotFoundException {
        String localVersion = context.getPackageManager()
                .getPackageInfo(context.getOpPackageName(), 0)
                .versionName;
        return localVersion;
    }

    public void showUpdateDialog(Context context, String title, String content) {
        //创建LinearLayout对象并设置布局方向
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        Markwon markwon = MarkwonFactory.creatWithImgMarkwon(context);
        //创建TextView对象并设置其属性
        LinkResolver resolver = new LinkResolver() {
            @Override
            public void resolve(@NonNull View view, @NonNull String link) {
                // 处理链接点击，例如使用意图启动浏览器或其他应用
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                view.getContext().startActivity(intent);
            }

        };
        TextView textView = new TextView(context);
        markwon.setMarkdown(textView, content);
        //textView.setText(Html.fromHtml(content,Html.FROM_HTML_MODE_LEGACY));
        textView.setClickable(true);
        textView.setVerticalScrollBarEnabled(true);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        textView.setLayoutParams(layoutParams);
        //将TextView添加到LinearLayout中：
        layout.addView(textView);
        AlertDialog.Builder builder = DialogFactory.normalDialog(context, title, "", layout);
        Dialog alertDialog = builder
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String baseDownUrl = context.getResources().getString(R.string.url_down_apk);
                        //String url = key + "/download/JIUR_" + newVersion;
                        String url = baseDownUrl + newVersion +"/app-release.apk";
                        Log.d("APP下载链接",url);
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        context.startActivity(intent);
//                        new Thread(() -> {
//                            update();
//                        }).start();
                    }
                })
                .create();
        alertDialog.show();
    }

    public void update() {
        if (PermissionUtil.checkIsHasStoragePermission(context)) {
            Looper.prepare();
//            Toast.makeText(context, "下载中", Toast.LENGTH_SHORT).show();
//            Looper.loop();
            //URL url = new URL(key + "/download/JIUR_" + newVersion);
            String url = key + "/download/JIUR_" + newVersion;
            new DownloadUtil(context).Down("JIUR_" + newVersion, Uri.parse(url), ".APK");
        }
    }
}
