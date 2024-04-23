package cn.swust.jiur.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.swust.jiur.R;
import cn.swust.jiur.adapter.MusicListAdapter;

import cn.swust.jiur.databinding.FragmentMusicBinding;
import cn.swust.jiur.entity.Music;
import cn.swust.jiur.factory.AnimationFactory;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.impl.AdapterClickListener;
import cn.swust.jiur.service.MusicService;
import cn.swust.jiur.utils.DownloadUtil;
import cn.swust.jiur.utils.JsonUtil;
import cn.swust.jiur.utils.OkHttpUtil;
import cn.swust.jiur.utils.PermissionUtil;
import cn.swust.jiur.utils.SharedPreferenceUtil;

public class MusicFragment extends BaseFragment<FragmentMusicBinding> implements View.OnClickListener{
    public static final String KEYFILENAME = "key";
    public static final String KEY_NAME = "key";
    public static final String COVER = "cover";
    public static final String DOWN_URL = "downUrl";
    public static final String MUSIC_NAME = "name";
    private int MAX_LOAD_NUM = 3;
    private final int MSG_ERROR = 0,MSG_GETLIST_SUCCESS = 3, MSG_LOAD_MUSIC_COVER = 4;
    private final int PLAY_CODE = 1, STOP_CODE = 2, SEEK_CODE = 3, NEWMUSIC_CODE = 4, CURRENTDURATION_CODE = 5, TOTALDURATION = 6;
    private final String TAG = "MusicActivity";
    private FragmentMusicBinding musicBinding;
    private Parcel sendData,reply;
    private Handler handler;
    private Dialog loadDialog;
    private String downUrl,musicName,mkey;
    private MediaPlayer mediaPlayer;
    private JSONArray jsonArray;
    private MusicListAdapter musicListAdapter;
    private List<Music> musicList = new ArrayList<>();
    private int musicSum, curPosition;
    private boolean isPlaying = false,flag = false;
    private IBinder mBinder;
    private int total_duration = 0,current_time = 0;
    private MusicService ms;
    private LoadThread loadThread;
    private final SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private final ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ms = ((MusicService.MyBinder)iBinder).getMusicService();
            mBinder = iBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ms = null;
        }
    };
    private void bindServiceConnection() {
        Intent intent = new Intent(getContext(), MusicService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void initData() {
        musicBinding = getBinding();
        loadDialog = DialogFactory.loadDialog(getContext());
        loadDialog.setCancelable(false);
        mediaPlayer = new MediaPlayer();
        initHandler();
        registerClickEvent();
        hasKey();
        setSeekBar();
        bindServiceConnection();
    }

    @Override
    public FragmentMusicBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentMusicBinding.inflate(inflater,container,false);
    }

    private boolean hasKey() {
        mkey = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,KEYFILENAME,
                KEY_NAME);
        if(mkey.isEmpty()){
            DialogFactory.createInputKeyDialog(getContext()).show();

            return false;
        }
//        musicBinding.relayKey.getRoot().setVisibility(View.INVISIBLE);
//        musicBinding.relaySearch.setVisibility(View.VISIBLE);
        return true;
    }

    private void initHandler() {
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                int code = msg.what;
                loadDialog.dismiss();
                if(code == 0){

                    Dialog dialog = DialogFactory.createInputKeyDialog(getContext());
                    dialog.show();
                    Toast.makeText(getContext(), "服务器异常,请检查服务器或者重新输入秘钥！", Toast.LENGTH_SHORT).show();
                }else if(code == 1){
                    musicBinding.relaySearch.setVisibility(View.GONE);
                    musicBinding.recycleviewMusic.setVisibility(View.GONE);
                    musicBinding.editLink.setVisibility(View.GONE);
                    show(View.VISIBLE);
                    String[] strings = (String[]) msg.obj;
                    Glide.with(getContext())
                            .load(strings[0])
                            .placeholder(R.drawable.twotone_help_24)
                            .into(musicBinding.cover);
                    //设置信息
                    resetMusicPlayer(strings);
                    destroyParcel();
                } else if (msg.what == CURRENTDURATION_CODE) {
                    if(current_time < total_duration){
                        try {
                            initParcel();
                            mBinder.transact(CURRENTDURATION_CODE,sendData,reply,0);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        current_time = reply.readInt();
                        musicBinding.seekbar.setProgress(current_time);
                        musicBinding.musicNowTime.setText(time.format(current_time));
                        destroyParcel();
                        handler.sendEmptyMessageDelayed(CURRENTDURATION_CODE,1000);
                    }
                } else if (msg.what == MSG_GETLIST_SUCCESS) {
//                    new Thread(()->{
//                        for (int i = MAX_LOAD_NUM; i < musicList.size(); i++) {
//                            getMusicDownUrl(i);
//                            try {
//                                if((i+1)%MAX_LOAD_NUM==0){
//                                    Thread.sleep((long) ((new Random(1).nextFloat()+1)*800));
//                                    handler.sendMessage(MessageFactory.newMessage(MSG_LOAD_MUSIC_COVER,null));
//                                }
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    }).start();
                    musicBinding.recycleviewMusic.setVisibility(View.VISIBLE);
                    musicListAdapter = new MusicListAdapter(getContext(),musicList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()                            ,LinearLayoutManager.VERTICAL,false);
                    musicListAdapter.setMusicClickListener(new AdapterClickListener() {
                        @Override
                        public void perform(int position) {
                            if(loadThread!=null && loadThread.isAlive()){
                                loadThread.interrupt();
                            }
                            loadThread = new LoadThread(position);
                            loadThread.start();
                            loadDialog.show();
                            curPosition = position;
                        }
                        @Override
                        public void downMusic(int position) {
                            Music music = musicList.get(position);
                            if(music.getDownUrl()!=null){
                                Toast.makeText(getContext(),musicList.get(position)
                                        .getCover(), Toast.LENGTH_SHORT).show();
                                new DownloadUtil(getContext())
                                        .downloadMusic(music.getName(), Uri.parse(music.getDownUrl()));
                            }
                        }
                    });
                    musicBinding.recycleviewMusic.setAdapter(musicListAdapter);
                    musicBinding.recycleviewMusic.setLayoutManager(linearLayoutManager);
                } else if (msg.what == MSG_LOAD_MUSIC_COVER) {
                    musicListAdapter.setMusicList(musicList);
                }
            }
        };
    }

    private void resetMusicPlayer(String[] strings) {
        musicBinding.tvSongName.setText(strings[2]);
        musicBinding.seekbar.setProgress(0);
        isPlaying = false;
        musicBinding.seekbar.setMax(MusicService.getInstance().getDuration());
        total_duration = reply.readInt();
        musicBinding.musicTotalTime.setText(time.format(new Date(total_duration)));
        musicBinding.musicNowTime.setText(time.format(new Date(current_time)));
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.img_music_search){
            show(View.GONE);
            loadDialog.show();
            mkey = (String) SharedPreferenceUtil.readData(getContext(), SharedPreferenceUtil.Type.STRING,KEYFILENAME,
                    KEY_NAME);
            String songName = musicBinding.editLink.getText().toString();
            startGetMusicList(songName);
        } else if (id == R.id.music_play) {
            try {
                initParcel();
                mBinder.transact(PLAY_CODE,sendData,null,1);
                handler.sendEmptyMessage(CURRENTDURATION_CODE);
                if(isPlaying){
                    musicBinding.cover.clearAnimation();
                    musicBinding.musicPlay.setImageDrawable(AppCompatResources
                            .getDrawable(getContext(),R.drawable.music_play));
                }else {
                    musicBinding.cover.startAnimation(new AnimationFactory(getContext()).roatateAnim());
                    musicBinding.musicPlay.setImageDrawable(AppCompatResources
                            .getDrawable(getContext(),R.drawable.music_pause));
                }
                isPlaying = !isPlaying;
                destroyParcel();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else if (id == R.id.img_save) {
            if(PermissionUtil.checkIsHasStoragePermission(getContext())){
                DownloadUtil downloadUtil = new DownloadUtil(getContext());
                downloadUtil.downloadMusic(musicName, Uri.parse(downUrl));
            }
        } else if (id == R.id.play_list_music) {
            musicBinding.editLink.setVisibility(View.VISIBLE);
            musicBinding.relaySearch.setVisibility(View.VISIBLE);
            show(View.GONE);
            musicBinding.recycleviewMusic.setVisibility(View.VISIBLE);
        } else if (id == R.id.previous_music) {
            loadDialog.show();
            changeMusic(0);
        } else if (id == R.id.next_music) {
            loadDialog.show();
            changeMusic(1);
        }
//        } else if (id == R.id.btn_enter) {
//            String key = musicBinding.relayKey.editKey.getText().toString();
//            if((key.startsWith("https://") || key.startsWith("http://"))){
//                musicBinding.relaySearch.setVisibility(View.VISIBLE);
//                musicBinding.relayKey.getRoot().setVisibility(View.GONE);
//                mkey = key;
//                SharedPreferenceUtil.clearData(getContext(),KEYFILENAME);
//                SharedPreferenceUtil.save(getContext(), SharedPreferenceUtil.Type.STRING
//                        ,KEYFILENAME,KEY_NAME,mkey);
//            }else {
//                Toast.makeText(getContext(), "秘钥格式有误，请检查后重新输入！", Toast.LENGTH_SHORT).show();
//            }
//        }
    }
    /**
     *
     * @param type 0---上一首 1---下一首
     */
    private void changeMusic(int type){
        if(type == 0){
            if(curPosition -1>=0){
                curPosition--;
            }
        }else {
            if(curPosition +1<musicSum){
                curPosition++;
            }
        }
        musicBinding.musicPlay.setImageDrawable(AppCompatResources.getDrawable(getContext(),R.drawable.music_play));
        musicBinding.cover.clearAnimation();
        if(loadThread.isAlive()){
            loadThread.interrupt();
        }
        loadThread = new LoadThread(curPosition);
        loadThread.start();
    }
    private void show(int visible) {
        isPlaying = false;
        musicBinding.cover.setVisibility(visible);
        musicBinding.musicPlay.setImageDrawable(AppCompatResources.getDrawable(getContext(),
                R.drawable.music_play));
        musicBinding.linearSeekbar.setVisibility(visible);
        musicBinding.linearTool.setVisibility(visible);
        musicBinding.tvSongName.setVisibility(visible);
    }
    private  void startGetMusicList(String song_name) {
        new Thread(()->{
            String url = mkey+"/music?name="+song_name;
            //搜索歌曲
            JSONObject jsonObject = OkHttpUtil.getData(url);
            if(jsonObject!=null){
                Log.d("MSG",jsonObject.toString());
                if("200".equals(jsonObject.optString("code"))){
                    jsonArray = jsonObject.optJSONArray("data");
                    assert jsonArray != null;
                    musicList = JsonUtil.jsonArrayToList(jsonArray);
                    musicSum = jsonArray.length();
                    curPosition = 0;
                    new InitRecycleViewThread().start();
                }
            }else {
                handler.sendMessage(MessageFactory.newMessage(0,null));
            }

        }).start();
    }
    private void initMediaPlayer(JSONObject jsonObject) throws JSONException {
        if(isPlaying){
            isPlaying = !isPlaying;
            try {
                mBinder.transact(STOP_CODE,sendData,reply,1);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        current_time = 0;
        String img = jsonObject.optString(COVER);
        downUrl = jsonObject.optString(DOWN_URL);
        try {
            initParcel();
            sendData.writeString(downUrl);
            mBinder.transact(NEWMUSIC_CODE,sendData,reply,0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        musicName = jsonObject.optString("name");
        String[] strings = {img,downUrl,musicName};
        handler.sendMessage(MessageFactory.newMessage(1,strings));
    }
    private void setSeekBar() {
        musicBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    musicBinding.cover.setPivotX(musicBinding.cover.getWidth()/2);
                    musicBinding.cover.setPivotY(musicBinding.cover.getHeight()/2);//支点在图片中心
                    //progress表示毫秒数，非常大，所以转化为比较容易观察的数据
                    musicBinding.cover.setRotation(progress/30);
                    musicBinding.musicNowTime.setText(time.format(progress));
                    //与服务通信
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    data.writeInt(progress);
                    try{
                        mBinder.transact(SEEK_CODE, data, reply, 0);
                    }catch (RemoteException e){
                        Log.e("STOP:", "onClick: " + e.toString() );
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    /**
     * 检查存储权限
     */
    @SuppressLint("Range")
    private  void checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 如果已经授予了权限，则执行保存文件的操作
            DownloadUtil downloadUtil = new DownloadUtil(getContext());
            Log.d(TAG,downUrl);
            downloadUtil.downloadMusic(musicName,Uri.parse(downUrl));
        } else {
            // 如果未被授予权限，则向用户请求权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 如果权限已经授予，则执行保存文件的操作
            new DownloadUtil(getContext())
                    .downloadMusic(musicName,Uri.parse(downUrl));;
        } else {
            // 如果权限未被授予，则向用户显示一个错误消息
            Toast.makeText(getContext(), "读取外部存储的权限被拒绝", Toast.LENGTH_SHORT).show();
        }
    }
    private void registerClickEvent() {
        musicBinding.imgMusicSearch.setOnClickListener(this);
        musicBinding.musicPlay.setOnClickListener(this);
        musicBinding.imgSave.setOnClickListener(this);
        musicBinding.previousMusic.setOnClickListener(this);
        musicBinding.nextMusic.setOnClickListener(this);

        musicBinding.playListMusic.setOnClickListener(this);
    }
    public class LoadThread extends Thread{
        private int position;
        public LoadThread(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            getMusicDownUrl(this.position);
        }
    }

    private void getMusicDownUrl(int position) {
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = (JSONObject) jsonArray.get(position);
            String songUrl = jsonObject1.optString("url");
            JSONObject jsonObject2 = null;
            jsonObject2 = OkHttpUtil.getData(mkey+"/music/"+ songUrl.substring(29));
            if(jsonObject2!=null){
                JSONObject data = jsonObject2.getJSONObject("data");
                initMediaPlayer(data);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public class InitRecycleViewThread extends Thread{
        @Override
        public void run() {
            musicList = JsonUtil.jsonArrayToList(jsonArray);
            for (int i = 0; i < MAX_LOAD_NUM; i++) {
                String url = musicList.get(i).getUrl();
                try {
                    JSONObject jsonObject2 = OkHttpUtil.getData(mkey+"/music/"+ url.substring(29));
                    if(jsonObject2!=null){
                        JSONObject data = jsonObject2.getJSONObject("data");
                        musicList.get(i).setCover(data.optString("cover"));
                        musicList.get(i).setDownUrl(data.optString("downUrl"));
                        musicList.get(i).setWordUrl(data.optString("wordUrl"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            handler.sendMessage(MessageFactory.newMessage(MSG_GETLIST_SUCCESS,null));
        }
    }
    private void destroyParcel() {
        sendData.recycle();
        reply.recycle();
    }

    private void initParcel() {
        sendData = Parcel.obtain();
        reply = Parcel.obtain();
    }

}
