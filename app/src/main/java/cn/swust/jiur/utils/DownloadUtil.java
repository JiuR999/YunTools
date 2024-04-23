package cn.swust.jiur.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
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
        Log.d("Title",title);
        String videoPath = videoView.getTag().toString();
        Down(title, Uri.parse(videoPath),".mp4");
    }
    public void downloadMusic(String title,Uri uri) {
        Down(title, uri,".mp3");
    }
    public void Down(String title, Uri uri,String last) {
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setDescription("下载中……")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(title);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //String path = Environment.DIRECTORY_DOWNLOADS+ "/JIUR";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/JIUR";
        //mkDir(path);
        if(last.equals(".jpg")){
            path = path+"/Images";
        } else {
            path = path+"/Music";
        }
        FileUtil.mkDir(path);

        request.setDestinationUri(Uri.fromFile(new File(path)));
        //request.setDestinationInExternalFilesDir(mContext,path,title+last);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + last);
        this.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
            Toast.makeText(mContext, "下载进行中!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "下载失败!", Toast.LENGTH_SHORT).show();
        }
    }

}
