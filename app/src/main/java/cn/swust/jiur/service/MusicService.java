package cn.swust.jiur.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class MusicService extends Service {
    //用来跟Activity进行绑定
    public final IBinder binder = new MyBinder();
    public static MediaPlayer mp;
    public static MediaPlayer getInstance() {
        if (mp == null) {
            synchronized (MusicService.class) {
                mp = new MediaPlayer();
            }
        }
        return mp;
    }

    /**
     * 对Service控制的不同数字码
     * PLAY_CODE 开始播放
     * STOP_CODE 停止播放
     * SEEK_CODE 拖动进度条
     * NEWMUSIC_CODE 新的音乐
     * CURRENTDURATION_CODE
     * TOTALDURATION
     */
    private final int PLAY_CODE = 1;
    private final int STOP_CODE = 2;
    private final int SEEK_CODE = 3;
    private final int NEWMUSIC_CODE = 4;
    private final int CURRENTDURATION_CODE = 5;
    private final int TOTALDURATION = 6;

    //重写onTransact方法，对不同的CODE做出不同的反应
    public class MyBinder extends Binder {
        public MusicService getMusicService(){
            return MusicService.this;
        }
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException, RemoteException {
            switch (code) {
                //service solve
                case PLAY_CODE:
                    play_pause();
                    break;
                case STOP_CODE:
                    stop();
                    break;
                case SEEK_CODE:
                    mp.seekTo(data.readInt());
                    break;
                case NEWMUSIC_CODE:
                    newMusic(Uri.parse(data.readString()));
                    reply.writeInt(mp.getDuration());
                case TOTALDURATION:
                    reply.writeInt(mp.getDuration());
                    break;
                case CURRENTDURATION_CODE:
                    reply.writeInt(mp.getCurrentPosition());
                    break;
                default:
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }

    private void stop() {
        if (mp != null) {
            mp.stop();
            try {
                mp.prepare();
                mp.seekTo(0);
            } catch (Exception e) {
                Log.d("stop", "stop: " + e.toString());
            }
        }
    }

    private void play_pause() {
        if (mp.isPlaying()) {
            mp.pause();
        } else {
            mp.start();
        }
    }

    public void newMusic(Uri uri) {
        try {
            mp.reset();
            mp.setDataSource(this, uri);
            mp.prepare();
        } catch (Exception e) {
            Log.d("New Music", "new music: " + e.toString());
        }
    }

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        try {
            String uri = intent.getStringExtra("music_uri");
            mp = new MediaPlayer();
            if(uri!=null){
                mp.setDataSource(MusicService.class.newInstance(), Uri.parse(uri));
                mp.prepare();
            }
        } catch (IOException e) {
            Log.e("prepare error", "getService: " + e.toString());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return binder;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp!= null){
            //release之前一定要reset，不然会报下面的错
            //W/MediaPlayer(7564): mediaplayer went away with unhandled events
            mp.reset();
            mp.release();
        }
    }
}