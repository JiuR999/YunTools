package cn.swust.jiur.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtil {
    /**
     * 检查存储权限
     */
    @SuppressLint("Range")
    public static boolean checkIsHasStoragePermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 如果已经授予了权限，则执行保存文件的操作
            return true;
        } else {
            // 如果未被授予权限，则向用户请求权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.MANAGE_EXTERNAL_STORAGE},200);
            return true;
        }
    }
}
