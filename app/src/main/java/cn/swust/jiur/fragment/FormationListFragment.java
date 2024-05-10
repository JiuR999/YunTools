package cn.swust.jiur.fragment;

import static android.app.Activity.RESULT_OK;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.adapter.FormRecycleAdapter;
import cn.swust.jiur.database.dao.FormDao;
import cn.swust.jiur.databinding.FragmentFormationlistBinding;
import cn.swust.jiur.databinding.UploadFormationBinding;
import cn.swust.jiur.entity.Formation;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.utils.KeyCheckHelper;
import cn.swust.jiur.utils.OkHttpUtil;
import cn.swust.jiur.utils.SharedPreferenceUtil;
import cn.swust.jiur.utils.UpdateUtil;
import cn.swust.jiur.utils.UriToFileUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormationListFragment extends BaseFragment<FragmentFormationlistBinding>{
    public static final String HALL_LEVEL_SELECTION = "hall_level_selection";
    private static final int UPLOAD_FORM = 1;
    private FragmentFormationlistBinding binding;
    private List<Formation> formationList;
    private ActivityResultLauncher<String> launcher;
    private final String TAG = "FMationActivity";
    private FormRecycleAdapter recycleAdapter;
    private Handler handler;
    private FormDao formDao;
    private String homeTallImg;
    private ImageView imageView;
    private Formation nformation;
    private UploadFormationBinding uploadFormationBinding;
    private String key;
    private String level = "7";

    @Override
    public void initData() {
        binding = getBinding();
        //EventBus.getDefault().register(this);
        initView();
        initSpinner();
        initHandler();
        initRefresh();
        setPicSelectLauncher();
    }

    private void initRefresh() {
        binding.formRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getFormList(level);
                    refreshLayout.finishRefresh(1000);
            }
        });
    }

    private void setPicSelectLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        Log.d("图片", result.toString());
                        String path = UriToFileUtil.getRealPathFromUri(getContext()
                                , result);
                        if (path != null) {
                            InputStream inputStream = null;
                            try {
                                inputStream = getContext().getContentResolver().openInputStream(result);
                                String imgPath = "/data/data/"+getContext().getPackageName()+"/img_tmp.png";
                                FileOutputStream out = new FileOutputStream(imgPath);
                                Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
//                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                //byte[] byteArray = ;
                                int length = bitmap1.getByteCount();
                                File file = new File(imgPath);
                                OkHttpClient client = new OkHttpClient();
                                MultipartBody.Builder builder = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("image", "test.png", RequestBody
                                                .create(MediaType.parse("image/*"), file));
                                Request request = new Request.Builder()
                                        .url("https://tucdn.wpon.cn/api/upload")
                                        .addHeader("Content-Type","multipart/form-data")
                                        .addHeader("Accept","*/*")
                                        .addHeader("Accept-Encoding","gzip, deflate, br")
                                        .addHeader("Connection","keep-alive")
                                        .addHeader("token","36acd6ac4544da1b0f14abbedc03b446")
                                        .post(builder.build())
                                        .build();
                                new Thread(()->{
                                    Response response = null;
                                    try {
                                        response = client.newCall(request).execute();
                                        if (response.isSuccessful()) {
                                            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.body().string());
                                            if(jsonObject.getString("code").equals("200")){
                                                String url = jsonObject.getJSONObject("data").getString("url");
                                                if(url != null && url.startsWith("http")){
                                                    nformation.setImg(url);
                                                    getActivity().runOnUiThread(()->{
                                                        Glide.with(getContext())
                                                                .load(url)
                                                                .placeholder(R.drawable.load_32)
                                                                .into(imageView);
                                                        uploadFormationBinding.submit.setVisibility(View.VISIBLE);
                                                        Log.d(TAG + "照片Url",url);
                                                    });
                                                }
                                            } else {
                                                Toast.makeText(getContext(),jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                            }
                                            //Log.d("阵型上传",response.body().string());
                                        } else {
                                            Toast.makeText(getContext(), "阵型图片上传失败，请重试!", Toast.LENGTH_SHORT).show();
                                            Log.d("阵型图片上传失败",response.body().string());
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                }).start();
                                inputStream.close();
                                //boolean delete = file.delete();
                                //Log.d(TAG,delete?"删除成功":"删除失败");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            Log.d("照片路径", path);
                        }
                    }
                });

    }
    private void initSpinner() {
        String[] levels = getResources().getStringArray(R.array.home_level);
        Spinner spinner = binding.spinnerHomeLevel;
        //SpinnerUtil.initSpinner(this,spinner,getResources().getStringArray(R.array.home_level));
        //TODO 记录上次选择的值
        int selection = (int) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.INT,HomeFragment.M_CACHES
                , HALL_LEVEL_SELECTION);
        selection = selection == 0 ? 7 : selection;
        spinner.setSelection(selection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = levels[position];
                //String value = String.valueOf(Integer.valueOf(level));
                SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.INT,HomeFragment.M_CACHES
                        ,HALL_LEVEL_SELECTION,position);
                getFormList(level);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getFormList(String level) {
        new Thread(() -> {
            formationList.clear();
            JSONObject jsonObject = OkHttpUtil.getData(getString(R.string.coc) + level);
            try {
                jsonObject = jsonObject.getJSONObject("data");
                homeTallImg = jsonObject.optString("img");
                JSONArray jsonArray = jsonObject
                        .getJSONArray("forms");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String link = jsonArray.getJSONObject(i).optString("link");
                    String img = jsonArray.getJSONObject(i).optString("img");
                    Formation formation = new Formation();
                    formation.setImg(img);
                    formation.setLink(link);
                    formationList.add(formation);
                }
                handler.sendMessage(MessageFactory.newMessage(RESULT_OK, null));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void initHandler() {
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                int cmd = msg.what;
                if(cmd == RESULT_OK){
                    Glide.with(getContext())
                            .load(homeTallImg)
                            .placeholder(R.drawable.load_32)
                            .into(binding.imgHomeTall);
                    if (recycleAdapter != null) {
                        recycleAdapter.setFormationList(formationList);
                    } else {
                        initRecycleView();
                    }
                } else if (msg.what == UPLOAD_FORM){
                    Toast.makeText(getContext(), (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        recycleAdapter = new FormRecycleAdapter(formationList, getContext());
        binding.recycleviewFlist.setAdapter(recycleAdapter);
        binding.recycleviewFlist.setLayoutManager(layoutManager);
    }

    private void initView() {
        formationList = new ArrayList<>();
        nformation = new Formation();
        binding.fabAdd.setOnClickListener(view -> {
            key = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,
                    MusicFragment.KEYFILENAME, "key");
            if(key.isEmpty()){
                DialogFactory.createInputKeyDialog(getContext()).show();
            }else {
                addFormation();
            }
        });
    }

    /**.
     * 增加阵型.
     */
    private void addFormation() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = View.inflate(getContext(), R.layout.upload_formation, null);
        uploadFormationBinding = UploadFormationBinding.bind(view);
        bottomSheetDialog.setContentView(uploadFormationBinding.getRoot());
        bottomSheetDialog.show();
        imageView = uploadFormationBinding.imgSelectPicture;
        uploadFormationBinding.townHallSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nformation.setLevel(position+7);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //imageView = bottomSheetDialog.findViewById(R.id.img_select_picture);

        //打开相册
        imageView.setOnClickListener(v->{
            launcher.launch("image/*");
        });

        uploadFormationBinding.submit.setOnClickListener(v->{
            if(uploadFormationBinding.hallDownInfo.getText().equals("") ||
            uploadFormationBinding.hallDownLink.getText().equals("")){
                Toast.makeText(getContext(), "请输入阵型描述或阵型链接", Toast.LENGTH_SHORT).show();
            } else {
                String hallTownInfo = uploadFormationBinding.hallDownInfo.getText().toString();
                String hallTownLink = uploadFormationBinding.hallDownLink.getText().toString();
                Log.d(TAG + "上传阵型","阵型描述:" + hallTownInfo + "/n" +
                        "阵型链接:" + hallTownLink);
                nformation.setContent(hallTownInfo);
                nformation.setLink(hallTownLink);
                FormBody formBody = new FormBody.Builder()
                        .add("level", String.valueOf(nformation.getLevel()))
                        .add("link", nformation.getLink())
                        .add("img", nformation.getImg())
                        .add("content", nformation.getContent())
                        .build();
                //发起增加请求
                new Thread(()->{
                    try {
                        JSONObject post = OkHttpUtil.post(key + "/forms", formBody);
                        String msg = null;
                        if(post != null && post.optString("code").equals("200")){
                            binding.formRefresh.autoRefresh(500);
                            msg = "上传成功!";
                            bottomSheetDialog.dismiss();
                            Log.d(TAG+"增加阵型",post.toString());
                        } else {
                            msg = "上传失败，请重试!";
                            Toast.makeText(getContext(), "上传失败 请重试!", Toast.LENGTH_SHORT).show();
                        }
                        handler.sendMessage(MessageFactory.newMessage(UPLOAD_FORM,msg));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
//                KeyCheckHelper.checkKey(getContext(),key -> {
//
//                });

            }
        });
    }

    @Override
    public FragmentFormationlistBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentFormationlistBinding.inflate(inflater,container,false);
    }
}
