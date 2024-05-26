package cn.swust.jiur.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.activity.WebActivity;
import cn.swust.jiur.adapter.FunctionAdapter;
import cn.swust.jiur.constant.MessageConstant;
import cn.swust.jiur.databinding.FragmentHomeBinding;
import cn.swust.jiur.entity.FunctionGroup;
import cn.swust.jiur.factory.AnimationFactory;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.factory.MarkwonFactory;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.impl.FunctionItemClickListener;
import cn.swust.jiur.utils.AttributeUtils;
import cn.swust.jiur.utils.FileUtil;
import cn.swust.jiur.utils.OkHttpUtil;
import cn.swust.jiur.utils.SharedPreferenceUtil;
import cn.swust.jiur.utils.SpinnerUtil;
import cn.swust.jiur.utils.UpdateUtil;
import cn.swust.jiur.utils.WebViewUtil;
import io.noties.markwon.Markwon;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements View.OnClickListener {
    public final static String PACGAGENAME_COC = "com.tencent.tmgp.supercell.clashofclans";
    public final static String PACGAGE_CLASS= "com.supercell.titan.tencent.GameAppTencent";
    public static final String REQUEST_OK = "200";
    public static final String HAS_UPDATE = "hasUpdate";
    public static final String S_UPDATE_FILE_NAME = "update";
    public static final String HTTP_COCFZ_COM = "http://cocfz.com";
    public static final String M_CACHES = "mCaches";
    public static final String START_CODE = "startCode";
    public static final String LAST_GET_CODE_TIME = "last_get_code_time";
    public static final String HITOKOTO = "hitokoto";
    public static final String SELECTED_MODE = "selection";
    private Context context;
    private Handler mHandler;
    private WebView webViewCoc;
    private WebSettings webSettings;
    public String ver_img = "", mode = "i";
    private FragmentHomeBinding homeBinding;
    private final String TAG = "HomeFragment";
    private String cacheStartCode;
    private TalkThread tThread;
    private AnimationFactory animationFactory;
    private Dialog sportDialog;
    private UpdateUtil updateUtil;
    private NavController navController;
    private String cacheHikotoko;
    private MainActivity mainActivity;
    private Dialog keyDialog;

    @Override
    public void initData() {
        homeBinding = getBinding();
        //navController = Navigation.findNavController(getActivity(),R.id.nav_main_container);
        NavHostFragment fragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_main_container);
        navController = fragment.getNavController();
        mainActivity = (MainActivity) getActivity();
        initRecycleView();
        context = getContext();
        homeBinding = getBinding();
        updateUtil = new UpdateUtil(context);
        animationFactory = new AnimationFactory(context);
        initSpinner();
//        checkHasKey();
//        updateUtil.isUpdate();
        registerClickEvent();
        initHandler();
        //getSCode();
        initStartCode();
        //initHitokotoText();
        //检查更新
        ifTipUpdate();
        homeBinding.hitokotoText.setMovementMethod(new ScrollingMovementMethod());
        homeBinding.hitokotoText.setVerticalScrollBarEnabled(true);
        //homeBinding.imgCodeLoad.setVisibility(View.VISIBLE);
    }

    private void getSCode() {
        homeBinding.imgStartCode.setVisibility(View.GONE);
        homeBinding.imgCodeLoad.setVisibility(View.VISIBLE);
        homeBinding.imgCodeLoad.startAnimation(animationFactory
                .roatateAnim());
        new Thread(()->{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://cocfz.com/")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    Document document = Jsoup.parse(response.body().string());
                    //Elements img = document.select("/html/body/div[1]/img[2]");
                    Elements img = document.getElementsByTag("img");
                    String src1 = img.get(0).attr("src");
                    String src2 = img.get(0).attr("src");
                    String src = src1.length() > src2.length() ? src1 : src2;
                    /*String orc = "https://api.pearktrue.cn/api/captchaocr/";
                    String substring = src.substring(src.indexOf(",")+1);
                    FormBody body = new FormBody.Builder()
                            .add("base64", substring)
                            .build();
                    OkHttpUtil.post(orc, body);*/
                    mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_HOMEFRAGMENT_START_CODE,src));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    private void ifTipUpdate() {
        //是否开启每次进入提醒更新
/*        String lastTipTime = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING
                ,M_CACHES,"lastTipUpdateTime");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!"".equals(lastTipTime)){
                LocalDateTime parse = LocalDateTime.parse(lastTipTime);
                parse = parse.plusHours(3);
                long milli = parse.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if(milli > System.currentTimeMillis()){
                    return;
                }
            }
        }*/
        new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    Thread.sleep(200);
                    updateUtil.isUpdate(mHandler);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void initHitokotoText() {
        if(cacheHikotoko == null){
            cacheHikotoko = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                    M_CACHES, HITOKOTO);
        }
        if (!"".equals(cacheHikotoko)) {
            mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_HOMEFRAGMENT_YIYAN, cacheHikotoko));
        } else {
            startHikThread();
        }
    }

    /**
     * 有缓存且不超时直接显示 否则加载
     */
    private void initStartCode() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String time = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                    M_CACHES, LAST_GET_CODE_TIME);
            if ("".equals(time)) {
                //getStartCode();
                getSCode();
            } else {
                long timestamp = LocalDateTime.parse(time)
                        .plusMinutes(45)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long timestampNow = System.currentTimeMillis();
                if (timestamp < timestampNow) {
                    //getStartCode();
                    getSCode();
                    return;
                }
                cacheStartCode = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                        M_CACHES, START_CODE);
                Log.d(TAG, LocalTime.now().toString() + "读取缓存" + cacheStartCode);
                mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_HOMEFRAGMENT_START_CODE, cacheStartCode));
            }
        }
    }

    private void registerClickEvent() {
        homeBinding.imgStartCode.setOnClickListener(this);
        homeBinding.imgFresh.setOnClickListener(this);
        homeBinding.linearTool.setOnClickListener(this);
        homeBinding.linearCoc.setOnClickListener(this);
        homeBinding.linearSport.setOnClickListener(this);
    }


    private void initHandler() {
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MessageConstant.MSG_HOMEFRAGMENT_YIYAN) {
                    if(msg.obj.toString()!= null && !"".equals(msg.obj.toString())){
                        homeBinding.hitokotoText.setText(msg.obj.toString());
                        if (cacheHikotoko == null || "".equals(cacheHikotoko)) {
                            SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING
                                    , M_CACHES, HITOKOTO, msg.obj.toString());
                        }
                        Log.d("一言", msg.obj.toString());
                    }

                } else if (msg.what == MessageConstant.MSG_HOMEFRAGMENT_START_CODE) {
                    ver_img = msg.obj.toString();
                    homeBinding.imgCodeLoad.clearAnimation();
                    homeBinding.imgCodeLoad.setVisibility(View.GONE);
                    getBinding().imgStartCode.startAnimation(animationFactory.slideIn());
                    homeBinding.imgStartCode.setVisibility(View.VISIBLE);
                    //TODO 缓存
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && cacheStartCode == null) {
                        SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING
                                , M_CACHES, START_CODE, ver_img);
                        SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING
                                , M_CACHES, LAST_GET_CODE_TIME, LocalDateTime.now().toString());
                        Log.d(TAG, LocalTime.now().toString() + "存入缓存" + ver_img);
                    }
                    Glide.with(getContext())
                            .load(ver_img)
                            .into(getBinding().imgStartCode);

//                    GlideUrl url = new GlideUrl(ver_img, new LazyHeaders.Builder()
//                            .addHeader("Referer", HTTP_COCFZ_COM)
//                            .addHeader("Origin", HTTP_COCFZ_COM).build());
//                    Glide.with(getContext())
//                            .load(url)
//                            .placeholder(R.drawable.twotone_terrain_24)
//                            .into(getBinding().imgStartCode);
                } else if (msg.what == MessageConstant.MSG_HOMEFRAGMENT_SPORT_CODE) {
                    finishModify();
                    if (REQUEST_OK.equals(msg.obj.toString())) {
                        Toast.makeText(context, "步数已经成功上传 请返回查看！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "请检查账号密码！", Toast.LENGTH_SHORT).show();
                    }
                } else if (msg.what == MessageConstant.MSG_UPDATE_SHOW_UPDATE_DIALOG) {
                    //TODO 记录更新内容
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING
                                ,M_CACHES,"lastTipUpdateTime",LocalDateTime.now().toString());
                    }
                    updateUtil.showUpdateDialog(getActivity(), "有新的版本啦!", (String) msg.obj);
                } else if (msg.what == MessageConstant.MSG_UPDATE_INPUT_KEY) {
                    Toast.makeText(getActivity(), (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
                    keyDialog = DialogFactory.createInputKeyDialog(getContext());
                    keyDialog.show();
                }
            }
        };
    }

    private void initSpinner() {
        String[] modes = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
        String[] items = {"动画", "漫画", "游戏", "文学", "原创", "网络", "其他", "影视", "诗词", "网易云", "哲学"};
        SpinnerUtil.initSpinner(getContext(), homeBinding.spinnerMode, items);
        homeBinding.spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mode = modes[i];
                TextView view1 = (TextView) view;
                if (view1 != null) {
                    int colorPrimary = AttributeUtils.getAttrColor(getContext(),android.R.attr.colorPrimary);
                    view1.setTextColor(colorPrimary);
                    view1.setGravity(Gravity.CENTER);
                }
                int lastSelectedMode = (int) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.INT,M_CACHES,SELECTED_MODE);
                if(lastSelectedMode != i){
                    SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.INT, M_CACHES,
                            SELECTED_MODE, i);
                    cacheHikotoko = "";

                }
                //cacheHikotoko = "";
                initHitokotoText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getStartCode() {
        //TODO 40分钟刷新一次
        homeBinding.imgStartCode.setVisibility(View.GONE);
        homeBinding.imgStartCode.setVisibility(View.VISIBLE);
        homeBinding.imgCodeLoad.startAnimation(animationFactory
                .roatateAnim());
        //webViewCoc = homeBinding.startCodeWeb;
        webSettings = webViewCoc.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webViewCoc.loadUrl(getResources().getString(R.string.url_ver_img));
        webViewCoc.addJavascriptInterface(new JavaScriptInterface(), "AndroidInterface");
        webViewCoc.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                webViewCoc.evaluateJavascript("javascript:var v = document.getElementById('verImg').src;AndroidInterface.getValue(v);"
                        , null);

                super.onPageFinished(view, url);
            }
        });
    }

    private void modifyStep(Dialog sportDialog) {
        String account = ((EditText) sportDialog.findViewById(R.id.edit_account))
                .getText()
                .toString();
        String pwd = ((EditText) sportDialog.findViewById(R.id.edit_password))
                .getText()
                .toString();
        String step = ((EditText) sportDialog.findViewById(R.id.edit_step))
                .getText()
                .toString();
        startUpLoad(account, pwd, step);

    }

    private void finishModify() {
        ImageView imageView = sportDialog.findViewById(R.id.img_sport_load);
        ((Button) sportDialog.findViewById(R.id.btn_commit)).setText(getString(R.string.enter));
        imageView.startAnimation(new AnimationFactory(getContext()).fadeOutAnim());
        imageView.setVisibility(View.GONE);
    }

    private void initRecycleView() {
        List<FunctionGroup> groups = generateDatas();
        FunctionAdapter adapter = new FunctionAdapter();
        adapter.setItemClickListener(new FunctionItemClickListener() {
            @Override
            public void click(int fragmentId, String name) {

                // 1 头像  2  壁纸
                if(fragmentId == R.id.navigation_picture_category){
                    Bundle args = new HomeFragmentArgs.Builder()
                            .setCategoryType(name)
                                    .build().toBundle();
                    navController.navigate(fragmentId,args);
                } else if (fragmentId == R.id.navigation_analy_vedio) {
                    Bundle args = new AnalyVedioFragmentArgs.Builder()
                            .setPlatform(name)
                            .build()
                            .toBundle();
                    navController.navigate(fragmentId,args);
                } else {
                    navController.navigate(fragmentId);
                }
                mainActivity.setToolBarVisible(View.VISIBLE);
                mainActivity.setToolBarTitle(name);
                //((MainActivity) getActivity()).setBottomBarVisible(View.GONE);
            }
        });
        adapter.submitList(groups);
        getBinding().recycleviewFunctionMenu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getBinding().recycleviewFunctionMenu.setAdapter(adapter);
    }

    private void startUpLoad(String account, String pwd, String step) {
        ImageView imageView = sportDialog.findViewById(R.id.img_sport_load);
        ((Button) sportDialog.findViewById(R.id.btn_commit)).setText("");
        //imageView.startAnimation(new AnimationFactory(getContext()).fadeInAnim());
        imageView.startAnimation(new AnimationFactory(getContext()).roatateAnim());
        imageView.setVisibility(View.VISIBLE);
        //String url = "https://api.pearktrue.cn/api/xiaomi/api.php";
        String url = getString(R.string.url_update_sport);
        FormBody body = new FormBody.Builder()
                .add("user", account)
                .add("password", pwd)
                .add("step", step)
                .build();

        new Thread(() -> {
            try {
                org.json.JSONObject jsonObject = OkHttpUtil.post(url, body);
                String code = jsonObject.optString("code");
                mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_HOMEFRAGMENT_SPORT_CODE, code));
                Log.d("Step", code);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    private void startHikThread() {
        tThread = new TalkThread();
        tThread.setHandler(mHandler);
        tThread.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_start_code) {
            getSCode();
//            if (webViewCoc != null) {
//                webViewCoc.evaluateJavascript("javascript:var v = document.getElementById('verImg').src;AndroidInterface.getValue(v);"
//                        , null);
//            } else {
//                getStartCode();
//            }
        } else if (id == R.id.img_fresh) {
            startHikThread();
        } else if (id == R.id.linear_coc) {

            View view = View.inflate(getContext(),R.layout.dialog_input,null);
            EditText editUrl = view.findViewById(R.id.edit_input_text);
            view.findViewById(R.id.btn_input_submit).setOnClickListener(v2->{
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                // 设置ComponentName参数1:packagename 参数2:MainActivity路径
                ComponentName cn = new ComponentName(PACGAGENAME_COC, PACGAGE_CLASS);
                intent.setComponent(cn);
                Pattern pattern = Pattern.compile("action.*tencent");
                Matcher matcher = pattern.matcher(editUrl.getText().toString());
                while (matcher.find()) {
                    Log.d("匹配", matcher.group(0));
                    intent.setData(Uri.parse("clashofclans://" + matcher.group(0)));
                }
                startActivity(intent);
            });
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("复制阵型进游戏")
                    .setView(view).create().show();
        } else if (id == R.id.linear_sport) {
            //TODO 微信运动
            View view = LayoutInflater.from(getContext()).inflate(R.layout.sport_form,null);
            sportDialog = DialogFactory.createCustomDialog(getContext(),"步数修改",view);
            sportDialog.show();
            sportDialog.findViewById(R.id.img_sport_help).setOnClickListener(vHelp -> {
                View helpView = LayoutInflater.from(getContext()).inflate(R.layout.sport_help, null);
                TextView tv = helpView.findViewById(R.id.tv_sport_help);
                Markwon markwon = MarkwonFactory.creatWithImgMarkwon(getContext());
                String help = FileUtil.readFromAssets(getContext(),"help.txt");
                markwon.setMarkdown(tv,help);
                Dialog helpDialog = DialogFactory.createCustomDialog(getContext(), "使用帮助", helpView);
                //DialogFactory.setDialogWindow(getContext(), helpDialog, DialogFactory.SELF, 6, , false);
                helpDialog.show();
            });
            sportDialog.findViewById(R.id.btn_commit).setOnClickListener(vSport->{
                modifyStep(sportDialog);
            });
        } else if (id == R.id.linear_tool) {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", context.getResources().getString(R.string.url_woobx));
            intent.putExtra("tip", context.getResources().getString(R.string.woobx_tip));
            startActivity(intent);
            //设置过渡动画
            //getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        }

    }

    public class TalkThread extends Thread {
        Handler handler;

        public void setHandler(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            String url = "https://v1.hitokoto.cn/?c=" + mode + "&encode=json";
            org.json.JSONObject jsonObject = OkHttpUtil.getData(url);
            if(jsonObject != null){
                this.handler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_HOMEFRAGMENT_YIYAN,jsonObject.optString("hitokoto") ));
            }
            super.run();
        }
    }


    class JavaScriptInterface {
        @JavascriptInterface
        public void getValue(String value) {
            mHandler.sendMessage(MessageFactory.newMessage(MessageConstant.MSG_HOMEFRAGMENT_START_CODE, value));
            // 处理获取到的值
            Log.d("启动码", "getValue: " + value);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Home", "暂停");

        if (webViewCoc != null) {
            webViewCoc.removeAllViews();
        }
        WebViewUtil.clearCache(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivity.setBottomBarVisible(View.VISIBLE);
        Log.d("Home", "恢复");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Home", "退出");
        if(webViewCoc != null){
            webViewCoc.removeAllViews();
        }
        WebViewUtil.clearCache(context);
    }

    private List<FunctionGroup> generateDatas() {
        String datas = FileUtil.readFromAssets(getContext(), "groups.txt");
        JSONObject jsonObject = JSON.parseObject(datas);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        return JSON.parseArray(jsonArray.toString(), FunctionGroup.class);
    }
    @Override
    public FragmentHomeBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }
}
