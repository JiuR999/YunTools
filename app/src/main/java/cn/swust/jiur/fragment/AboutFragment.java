package cn.swust.jiur.fragment;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.swust.jiur.R;
import cn.swust.jiur.adapter.ExpandAdapter;
import cn.swust.jiur.constant.MessageConstant;
import cn.swust.jiur.databinding.FragmentAboutBinding;
import cn.swust.jiur.entity.DataBean;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.impl.InputDialogClickListener;
import cn.swust.jiur.utils.FileUtil;
import cn.swust.jiur.utils.SharedPreferenceUtil;
import cn.swust.jiur.utils.UpdateUtil;

public class AboutFragment extends BaseFragment<FragmentAboutBinding> implements View.OnClickListener{
    private List<DataBean> dataBeanList;
    private DataBean dataBean;
    private ExpandAdapter expandAdapter;
    private FragmentAboutBinding aboutBinding;
    private UpdateUtil updateUtil;
    private Handler handler;
    private boolean isAutoCheck = true;
    private Dialog keyDialog;

    @Override
    public void initData() {
        aboutBinding = getBinding();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                aboutBinding.textViewVersion.setText("Version " + UpdateUtil.getLocalVersion(getContext()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        updateUtil = new UpdateUtil(getContext());
        initRecycleView();
        initHandler();
        new Thread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                updateUtil.isUpdate(handler);
            }
        }).start();
        registerClickEvent();
    }

    private void initRecycleView() {
        dataBeanList = new ArrayList<>();
        int ids = 1;
        String txt = FileUtil.readFromAssets(getContext(),"update.txt");
        String txt1 = FileUtil.readFromAssets(getContext(),"reference.txt");
        dataBean = new DataBean(String.valueOf(ids++), "更新日志", txt);
        dataBean.setChildBean(dataBean);
        dataBean.setType(0);
        DataBean tipBean = new DataBean(String.valueOf(ids++), "借物说明", txt1);
        tipBean.setChildBean(tipBean);
        tipBean.setType(0);
        dataBeanList.add(dataBean);
        dataBeanList.add(tipBean);
        aboutBinding.recycleviewMore.setLayoutManager(new LinearLayoutManager(getContext()));
        expandAdapter = new ExpandAdapter(getContext(), dataBeanList);
        aboutBinding.recycleviewMore.setAdapter(expandAdapter);
        //滚动监听
        expandAdapter.setOnScrollListener(new ExpandAdapter.OnScrollListener() {
            @Override
            public void scrollTo(int pos) {
                aboutBinding.recycleviewMore.scrollToPosition(pos);
            }
        });
    }

    private void registerClickEvent() {
        aboutBinding.layoutCheckUpdate.setOnClickListener(this);
    }

    private void initHandler() {
        this.handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MessageConstant.MSG_UPDATE_SHOW_UPDATE_DIALOG) {
                    aboutBinding.notice.setVisibility(View.VISIBLE);
                    String updateContent = (String) msg.obj;
                    String content = "\n" + updateContent;
                    updateUtil.showUpdateDialog(getContext(), "有新的版本啦!", content);
                } else if (msg.what == MessageConstant.MSG_UPDATE_NOT_UPDATE) {
                    aboutBinding.notice.setVisibility(View.GONE);

                    SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.INT, HomeFragment.S_UPDATE_FILE_NAME, HomeFragment.HAS_UPDATE,0);
                    if(!isAutoCheck){
                        Toast.makeText(getContext(), "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } else if (msg.what == MessageConstant.MSG_UPDATE_INPUT_KEY) {
                    Toast.makeText(getContext(), (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
                    keyDialog = DialogFactory.createInputKeyDialog(getContext());
                    keyDialog.show();
                }
            }
        };
    }

    @Override
    public FragmentAboutBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentAboutBinding.inflate(inflater,container,false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_check_update) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isAutoCheck = false;
                new Thread(() -> {
                    updateUtil.isUpdate(handler);
                }).start();
            }
        }
    }
}
