package cn.swust.jiur.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.webkit.ValueCallback;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.swust.jiur.BaseActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.databinding.ActivityFileBinding;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.utils.FileUtil;
import cn.swust.jiur.utils.OkHttpUtil;
import cn.swust.jiur.utils.UriToFileUtil;

public class FileActivity extends BaseActivity {
    private ActivityFileBinding fileBinding;
    private ValueCallback<Uri[]> checkedFile;
    private ActivityResultLauncher<String[]> explorer1,explorer2;
    private final int MSG_FILE_NAME1 = 1,MSG_FILE_NAME2 = 2;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileBinding = ActivityFileBinding.inflate(getLayoutInflater());
        setContentView(fileBinding.getRoot());
        initHandler();
        explorer1 = registerResult(MSG_FILE_NAME1);
        explorer2 = registerResult(MSG_FILE_NAME2);
        fileBinding.btnSelectFile1.setOnClickListener(view->{
            selectFile(explorer1);
        });
        fileBinding.submit.setOnClickListener(view->{
        OkHttpUtil.uploadFile(FileActivity.this,getString(R.string.my_host)+"/upload");
            Toast.makeText(this, "文件已保存至"+getString(R.string.exter_path)+"different.xlsx", Toast.LENGTH_SHORT).show();
        });
    }

    private void initHandler() {
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case MSG_FILE_NAME1:
                        fileBinding.tvFilename1.setText(msg.obj.toString());
                        break;
                    case MSG_FILE_NAME2:
                        fileBinding.tvFilename2.setText(msg.obj.toString());
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void selectFile(ActivityResultLauncher<String[]> explorer){
        String[] accept = {"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        explorer.launch(accept);
    }
    private ActivityResultLauncher<String[]> registerResult(int msg) {
        ActivityResultLauncher<String[]> explorer1 = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                result -> {
                        if(result == null) {
                            checkedFile.onReceiveValue(null); //result没选择时为null
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                try {
                                    InputStream in = getContentResolver().openInputStream(result);
                                    String root_path = Environment.getExternalStorageDirectory().getPath();
                                    File file = new File(root_path+"/JiuR");
                                    if (!file.exists()){
                                        //Boolean b = PermissionUtil.checkIsHasStoragePermission(FileActivity.this);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            if(!Environment.isExternalStorageManager()){
                                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                                startActivity(intent);
                                            }else {
                                                if(!file.mkdir()){
                                                    Toast.makeText(this, "创建文件夹失败", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                    assert in != null;
                                    String fileName = UriToFileUtil.getFileNameFromUri(FileActivity.this,result);
                                    handler.sendMessage(MessageFactory.newMessage(msg,fileName));
                                    FileUtil.writeFile(in,getString(R.string.exter_path)+fileName);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                });
        return explorer1;
    }
}