package cn.swust.jiur.factory;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import cn.swust.jiur.R;

public class AnimationFactory {
    private final Context context;

    public AnimationFactory(Context context) {
        this.context = context;
    }

    public Animation slideIn(){
        return AnimationUtils.loadAnimation(context,R.anim.slide_in);
    }
    public Animation roatateAnim(){
              return AnimationUtils.loadAnimation(context, R.anim.rotate);
       }
    public Animation fadeInAnim(){
        return AnimationUtils.loadAnimation(context, R.anim.fade_in);
    }
    public Animation fadeOutAnim(){
        return AnimationUtils.loadAnimation(context, R.anim.fade_out);
    }
}
