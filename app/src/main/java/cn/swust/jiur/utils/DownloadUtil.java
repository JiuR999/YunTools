package cn.swust.jiur.utils;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

/**
 * @author JIUR
 */
public class DownloadUtil {
    private final Context mContext;
    private long downloadID;
    private DownloadManager downloadManager;

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public long getDownloadID() {
        return downloadID;
    }

    public DownloadUtil(Context context) {
        mContext = context;
    }

    public void downloadVideo(VideoView videoView, String title) {

        String videoPath = videoView.getTag().toString();
        Log.d("videoPath",videoPath);
        Down(title, Uri.parse(videoPath),".mp4");
    }
    public void downloadMusic(String title,Uri uri) {
        Down(title, uri,".mp3");
    }
    public void Down(String title, Uri uri,String last) {
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setDescription("下载中------")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(title);
        //String path = Environment.DIRECTORY_DOWNLOADS+ "/JIUR";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/JIUR";
        FileUtil.mkDir(path);
        path = "JIUR";
        if(last.equals(".jpg")){
            path += "/Images";
        } else if(last.equals(".mp4")){
            path += "/Video";
        } else {
            path += "/Music";
        }
        //FileUtil.mkDir(path);

        //request.setDestinationUri(fileUri);
        //request.setDestinationInExternalPublicDir()
//        request.setDestinationInExternalFilesDir(mContext,Environment.DIRECTORY_DOWNLOADS,
//                "Music/" + title+last);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path + "/" + title + last);
        this.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
            //values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            //mContext.getContentResolver().update(fileUri, values, null, null);
            Toast.makeText(mContext, "下载进行中\n请下拉通知栏查看下载进度", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "下载失败!", Toast.LENGTH_SHORT).show();
        }
    }

}
