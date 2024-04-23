package cn.swust.jiur.utils;

import android.app.Dialog;
import android.content.Context;

import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.fragment.MusicFragment;
import cn.swust.jiur.impl.KeyHandler;

public class KeyCheckHelper {
    public static void checkKey(Context context, KeyHandler handler){
        String key = (String) SharedPreferenceUtil.readData(context, SharedPreferenceUtil.Type.STRING,
                MusicFragment.KEYFILENAME, "key");
        if(key.isEmpty()){
            Dialog keyDialog = DialogFactory.createInputKeyDialog(context);
            keyDialog.show();
        } else {
            handler.handleKey(key);
        }
    }
}
