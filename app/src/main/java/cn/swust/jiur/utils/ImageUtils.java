package cn.swust.jiur.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.swust.jiur.R;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.factory.MessageFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author JIUR
 */
public class ImageUtils {
    private static final int REQUEST_CODE = 200;
    private static final String TAG = "ImageUtils";
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                // 保存图片到相册
                SaveUtils.saveBitmapToAlbum(context, (Bitmap) msg.obj);
                //saveImageToGallery(context,(Bitmap) msg.obj);
            }
        }
    };

    public ImageUtils(Context context) {
        this.context = context;
    }

    public String imgToBase64(Bitmap bitmap) throws IOException {
        int size = bitmap.getHeight() * bitmap.getWidth() * 4;
        byte[] bytes;
        ByteArrayOutputStream bout = new ByteArrayOutputStream(size);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bout);
        bytes = bout.toByteArray();
        bout.close();
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        Log.d("base64", base64);
        return base64;
    }

    public Bitmap base64ToImg(String bs64) throws IOException {
        byte[] bytes = Base64.decode(bs64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(context.getApplicationContext().getResources(),
                bitmap);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }
    public void downloadImage(String imageUrl) {
        final String[] REQUIRED_PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
        };
        // 检查是否有写入外部存储的权限
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，则请求权限
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_CODE);

        }
        // 创建OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // 创建请求
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        // 发起异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to download image: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    // 从响应中获取图片的字节流
                    byte[] imageBytes = response.body().bytes();
                    // 将字节流转换为Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    // 保存图片到相册
                    //saveImageToGallery(bitmap);
                    handler.sendMessage(MessageFactory.newMessage(1,bitmap));
                } else {
                    Log.e(TAG, "Failed to download image. Response code: " + response.code());
                }
            }
        });
    }

    private void saveImageToGallery(Context context,Bitmap bitmap) {
        String filesDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //小米手机
        String imgPath = filesDir + "/JIUR";
//        ActivityCompat.requestPermissions((Activity) context,new String[]{
//                Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
//        },200);
        if(!FileUtil.mkDir(imgPath)){
            Toast.makeText(context, "创建文件夹失败", Toast.LENGTH_SHORT).show();
        }
        FileUtil.mkDir(imgPath+"/Images");
        // 创建图片文件
        File imageFile = new File(imgPath+"/Images", "images_"+ System.currentTimeMillis() + ".jpg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()){
                    DialogFactory.normalDialog(context,"授予存储权限",
                            "由于Android新版本特性\n您需要前往授予本软件管理所有文件权限\n" +
                                    "然后重新保存图片!",null)
                            .setPositiveButton("授予权限", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    context.startActivity(intent);
                                }
                            }).setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                // 将Bitmap保存到文件
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    // 通知相册刷新
                    MediaScannerConnection.scanFile(context,
                            new String[]{imgPath+"/*"}
                    ,new String[]{"image/*"},((path, uri) -> {
                        Log.d("刷新",path+"==>"+uri);
                            }));
                    Toast.makeText(context, "图片保存至: " + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Image saved to gallery: " + imageFile.getAbsolutePath());
                } catch (IOException ex) {
                    DialogFactory.normalDialog(context,"保存失败","Failed to save image: " + ex.getMessage(),null).show();
                    Log.e(TAG, "Failed to save image: " + ex.getMessage());
                }
            } else {
                String insertImage = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "壁纸头像", "保存的壁纸头像");
                if (!TextUtils.isEmpty(insertImage)) {
                    Toast.makeText(context, "图片保存成功!" + insertImage, Toast.LENGTH_SHORT).show();
                    Log.e("打印保存路径", insertImage + "-");
                }
            }
    }

}
