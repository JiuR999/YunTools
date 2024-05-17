package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import cn.swust.jiur.R;
import cn.swust.jiur.databinding.FragmentPictrueBinding;
import cn.swust.jiur.factory.MessageFactory;
import cn.swust.jiur.utils.OkHttpUtil;

public class PictrueFragment extends BaseFragment<FragmentPictrueBinding>{
    public static final int JOKE = 1;
    public static final int NEWS = 2;
    private int type;
    private Handler handler;
    private final String TAG = "PictrueFragment" ;
    @Override
    public void initData() {
        //type = getIntent().getStringExtra(TYPE);
        type = NEWS;
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Glide.with(getContext())
                        .load(msg.obj.toString())
                        .placeholder(R.drawable.load_32)
                        .into(getBinding().imgJoke);
            }
        };
        if(type == JOKE){
            new Thread(()->{
                String img = OkHttpUtil
                        .getData(getString(R.string.api_neihan))
                        .optString("data");
                Log.d(TAG,img);
                handler.sendMessage( MessageFactory.newMessage(JOKE,img));
            }).start();
        } else if (type == NEWS) {
            Glide.with(getContext())
                    .load("https://zj.v.api.aa1.cn/api/60s/")
                    .placeholder(R.drawable.load_32)
                    .into(getBinding().imgJoke);
        }
    }

    @Override
    public FragmentPictrueBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentPictrueBinding.inflate(inflater,container,false);
    }
}
