package cn.swust.jiur.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.databinding.FragmentAnalytikBinding;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.utils.DownloadUtil;
import cn.swust.jiur.utils.NetworkUtil;
import cn.swust.jiur.utils.OkHttpUtil;
import cn.swust.jiur.utils.PermissionUtil;

public class AnalyVedioFragment extends BaseFragment<FragmentAnalytikBinding> implements View.OnClickListener {
    public static final String TAG = "AnalyVedioFragment";
    public static final String ANALY_BILIBILI = "Bilibili解析";
    public static final String ANALY_TIKTOK = "抖音解析";
    private final int REQUEST_CODE = 200;
    private Dialog loading;
    private FragmentAnalytikBinding binding;
    private EditText editTextUrl;
    private ImageView imgLoad, imgSearch, imgBack;
    private Button btnSave;
    private Handler handler;
    private VideoView videoViewTik;
    private ImageView imgFengm;
    private String loadAddress;
    private ProgressBar progressBarLoad;
    private LinearLayout linearLayoutTool;
    private boolean isLoading = false;
    /**
     * 获取当前日期时间
     */
    private DateFormat format;
    private String date, platform, videoTitle;
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void initData() {
        initView();
    }

    private void initView() {
        binding = getBinding();

        platform = AnalyVedioFragmentArgs.fromBundle(requireArguments()).getPlatform();
        if(platform.equals(ANALY_BILIBILI)){
            binding.textInput.setHint("请输入视频BV号");
        }
        Log.d(TAG,platform);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(platform);
        builder.setSpan(new TypefaceSpan(ResourcesCompat.getFont(getContext(),R.font.mxj)),0,platform.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((MainActivity)getActivity()).setToolBarTitle(builder);
        threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1
                , TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
        format = new SimpleDateFormat("MM_dd_mm_ss");
        linearLayoutTool = binding.linearTool;
        imgFengm = binding.cover;
        btnSave = binding.imgSave;
        btnSave.setOnClickListener(this);
        View diaglogView = View.inflate(getActivity(), R.layout.load_diaglog, null);
        imgLoad = diaglogView.findViewById(R.id.img_load);
        progressBarLoad = binding.progressDown;
        loading = DialogFactory.loadDialog(getContext());
        loading.setCancelable(false);

        imgSearch = binding.imgMusicSearch;
        imgSearch.setOnClickListener(this);
        editTextUrl = binding.editLink;
        videoViewTik = binding.videoTik;
        MediaController m = new MediaController(getContext());
        videoViewTik.setMediaController(m);
        initHandler();
    }

    private void initHandler() {
        handler = new Handler(Looper.myLooper()) {
                                @Override
                                public void handleMessage(@NonNull Message msg) {
                                    super.handleMessage(msg);
                                    switch (msg.what) {
                                        case 1:
                                            loadAddress = msg.obj.toString();
                                            imgLoad.clearAnimation();
                                            if ("".equals(loadAddress)) {
                                                loading.dismiss();
                                                Toast.makeText(getActivity(), "请输入正确的视频链接！", Toast.LENGTH_SHORT).show();
                                            } else {
                                                loading.dismiss();
                                                videoViewTik.setVideoURI(Uri.parse(loadAddress));
                                                videoViewTik.setTag(Uri.parse(loadAddress));
                                                videoViewTik.start();
                                                linearLayoutTool.setVisibility(View.VISIBLE);
                                                imgFengm.setVisibility(View.GONE);
                                                //downDialog(getActivity(),address);
                                            }
                                            break;
                                        case 2:
                                            if (msg.arg1 != progressBarLoad.getMax()) {
                                                progressBarLoad.setProgress(msg.arg1);
                                            } else {
                                                reSetProgress();
                            Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
                        }
                    default:
                        break;
                }
            }
        };
    }

    private void reSetProgress() {
        isLoading = false;
        imgLoad.setVisibility(View.VISIBLE);
        progressBarLoad.setVisibility(View.GONE);
        progressBarLoad.setMax(progressBarLoad.getMax());
        progressBarLoad.setProgress(0);
        loading.dismiss();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_music_search) {
            //TODO 策略模式重构
            if ("".equals(editTextUrl.getText().toString())) {
                Toast.makeText(getContext(), "请输入视频链接！", Toast.LENGTH_SHORT).show();
            } else {
                if (NetworkUtil.isOpenNetwork(getContext())) {
                    if(platform.equals(ANALY_TIKTOK)){
                        analyTik();
                    } else if (platform.equals(ANALY_BILIBILI)) {
                        analyBilibili();
                    }

                } else {
                    Toast.makeText(getContext(), "网络未连接或网络不可用！", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.img_save) {
            //检查当前应用程序是否已经被授予写入外部存储权限
            date = format.format(System.currentTimeMillis());
            if (PermissionUtil.checkIsHasStoragePermission(getContext())) {
                downVideo();
            }
        }

    }

    private void analyTik() {
        String link = Match(editTextUrl.getText().toString(), "https://v.douyin.com/[a-zA-Z0-9]+/");
//                        "https://api.cooluc.com/?url=https://v.douyin.com/ie9qRNJf/"
        //https://www.mxnzp.com/api/douyin/video?url=aHR0cHM6Ly92LmRvdXlpbi5jb20vaWRFR1dyNFIv&app_id=ehpyynixekjhomti&app_secret=Is9aCnAOl4MMviEyi3AvKK7VGcgGAe3l
        Log.d("Base64", Base64.encodeToString(link.getBytes(), Base64.DEFAULT));
        String url = getResources().getString(R.string.api_analy_tik) + link;
        imgLoad.setVisibility(View.VISIBLE);
        imgLoad.startAnimation(initLoadAnim(getContext()));
        //显示加载动画
        loading.show();
        analysisVideo(url);
    }

    private void analyBilibili() {
        String link = editTextUrl.getText().toString();

        String url = getResources().getString(R.string.api_analy_bilib) + link;
        imgLoad.setVisibility(View.VISIBLE);
        imgLoad.startAnimation(initLoadAnim(getContext()));
        //显示加载动画
        loading.show();
        analysisVideo(url);
    }
    private void downVideo() {
        // 如果已经授予了权限，则执行保存文件的操作
        imgLoad.setVisibility(View.GONE);
        DownloadUtil downloadUtil = new DownloadUtil(getContext());
        downloadUtil.downloadVideo(videoViewTik, date);
        isLoading = true;
        threadPoolExecutor.execute(() -> {
            while (isLoading) {
                DownloadManager downloadManager = downloadUtil.getDownloadManager();
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadUtil.getDownloadID());
                Cursor cursor = downloadManager.query(query);
                cursor.moveToFirst();
                @SuppressLint("Range")
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(
                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                ));
                @SuppressLint("Range")
                int bytes_downLoad_total = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                progressBarLoad.setMax(bytes_downLoad_total);
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    isLoading = false;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Message msg = new Message();
                msg.what = 2;
                msg.arg1 = bytes_downloaded;
                handler.sendMessage(msg);
                cursor.close();
            }
        });
    }

    /**
     * 解析视频
     *
     * @param url 视频链接
     */
    private void analysisVideo(String url) {
        new Thread(() -> {
            String value = null;
            try {
                JSONObject data = OkHttpUtil.getData(url);
                if(data != null){
                    if(platform.equals(ANALY_TIKTOK)){
                        value = data.getJSONObject("data").optString("url");
                        Log.d("抖音视频链接", value);
                    } else if (platform.equals(ANALY_BILIBILI)) {
                        value = data.getJSONObject("data")
                                .optJSONArray("videos")
                                .getJSONObject(0)
                                .optString("videourl");
                        Log.d("b站视频链接", value);
                    }

                }
            } catch (JSONException e) {
                Log.d(TAG, Arrays.toString(e.getStackTrace()));
            }
            handler.sendMessage(MessageFactory.newMessage(1, value));
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 如果权限已经授予，则执行保存文件的操作
            new DownloadUtil(getContext())
                    .downloadVideo(videoViewTik, date);

        } else {
            // 如果权限未被授予，则向用户显示一个错误消息
            Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param link  要匹配的字符串
     * @param regex 正则表达式
     * @return 匹配的字符串
     */
    private String Match(String link, String regex) {
        Log.d("匹配start", regex);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @NonNull
    private Animation initLoadAnim(Context context) {
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);
        return rotateAnimation;
    }

    /**
     * 初始化弹窗
     */
    public Dialog createDialog(Context context, View view) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void downDialog(Context context, String address) {
        Dialog dialog = new Dialog(context);
        //将内容复制到剪切板
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", address);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "已将内容复制到剪切板！", Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        window.setLayout(width - 180, height * 2 / 5);

    }

    @Override
    public FragmentAnalytikBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentAnalytikBinding.inflate(inflater, container, false);
    }
}
