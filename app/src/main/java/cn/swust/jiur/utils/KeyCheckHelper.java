package cn.swust.jiur.utils;

import android.content.Context;

import cn.swust.jiur.fragment.MusicFragment;

public class KeyCheckHelper {
    //TODO 封装
    public static boolean checkKey(Context context) {
        String key = (String) SharedPreferenceUtil.readData(context, SharedPreferenceUtil.Type.STRING,
                MusicFragment.KEYFILENAME, "key");
        if (key.isEmpty()) {
//            Dialog keyDialog = DialogFactory.createInputKeyDialog(context);
//            keyDialog.show();
            return false;
        }
        //keyHandler.handleKey(handler,key);
        return true;
    }
}
