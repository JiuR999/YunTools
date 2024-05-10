package cn.swust.jiur.factory;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import cn.swust.jiur.R;
import cn.swust.jiur.fragment.MusicFragment;
import cn.swust.jiur.impl.InputDialogClickListener;
import cn.swust.jiur.utils.MyUploadWebChromeClient;
import cn.swust.jiur.utils.SharedPreferenceUtil;

/**
 * @author ASUS
 */
public class DialogFactory {
    public static final int WEIGHT_SUM = 7;
    public static final int INPUT_CORRECT_KEY = 1;
    /**
     * AUTO 默认
     * SELF 自定义宽高
     */
    public static final int AUTO = 1, SELF = 2;
    public static final int INPUT_INCORRECT_KEY = 0;

    public static AlertDialog.Builder normalDialog(Context context, String title, String msg, View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(title);
        if (view != null) {
            builder.setView(view);
        }
        if (!msg.equals("")) {
            builder.setMessage(msg);
        }
        return builder;
    }

    public static Dialog inputDialog(Context context, InputDialogClickListener inputDialogClickListener, String hint) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_input, null);
        EditText editText = view.findViewById(R.id.edit_input_text);

        TextInputLayout layout = view.findViewById(R.id.input_textfield);
        layout.setHint(hint);
        Dialog dialog = createCustomDialog(context,
                "填写秘钥", view);
        DialogFactory.setDialogWindow(context, dialog, DialogFactory.AUTO,
                DialogFactory.AUTO, DialogFactory.AUTO, false);
        Button button = view.findViewById(R.id.btn_input_submit);
        button.setOnClickListener(v -> {

            String key = editText.getText().toString();
            int code = inputDialogClickListener.submit(key);
            if (code == INPUT_CORRECT_KEY) {
                dialog.dismiss();
            } else {
                Toast.makeText(context, "秘钥格式有误，请检查后重新输入！", Toast.LENGTH_SHORT).show();
            }
        });
        return dialog;
    }

    public static Dialog createInputKeyDialog(Context context) {

        Dialog keyDialog = DialogFactory.inputDialog(context, new InputDialogClickListener() {
            @Override
            public int submit(String text) {
                if ((text.startsWith("https://") || text.startsWith("http://"))) {
                    SharedPreferenceUtil.clearData(context, MusicFragment.KEYFILENAME);
                    SharedPreferenceUtil.save(context, SharedPreferenceUtil.Type.STRING
                            , MusicFragment.KEYFILENAME, "key", text);
                    return INPUT_CORRECT_KEY;
                }
                return INPUT_INCORRECT_KEY;
            }
        }, "请输入秘钥");
        return keyDialog;
    }

    /**
     * 加载对话框
     *
     * @param context
     * @return
     */
    public static Dialog loadDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.load_diaglog);
        ImageView imageView = dialog.findViewById(R.id.img_load);
        AnimationFactory animationFactory = new AnimationFactory(context);
        imageView.startAnimation(animationFactory.roatateAnim());
        setDialogWindow(context, dialog, AUTO, AUTO, AUTO, true);
        return dialog;
    }

    /**
     * 自定义对话框
     *
     * @param context
     * @param view    视图布局
     * @return
     */
    public static Dialog createCustomDialog(Context context, String title, View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        if (!title.equals("")) {
            builder.setTitle(title);
        }
        if (view != null) {
            builder.setView(view);
        }
        //setDialogWindow(context, dialog, SELF, 6, 4,translate);
        return builder.create();
    }

    public static Dialog findDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(View.inflate(context, R.layout.find_different, null));
        WebView webView = dialog.findViewById(R.id.web_find_different);
        webView.setWebChromeClient(new MyUploadWebChromeClient((Activity) context));
        webView.loadUrl("file:///android_asset/www/ZhouJr.html");
        setDialogWindow(context, dialog, SELF, 6, 4, true);
        return dialog;
    }

    /**
     * 为对话框添加动画
     *
     * @param dialog
     */
    public static void dialogInAnim(Dialog dialog) {
        // 获取对话框的根布局
        View dialogView = dialog.getWindow().getDecorView();
        // 设置初始缩放比例为0
        dialogView.setScaleX(0);
        dialogView.setScaleY(0);
        // 创建水平方向的平移动画
        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(dialogView, "translationX", -500f, 0f);
        translationXAnimator.setDuration(600);
        // 创建垂直方向的平移动画
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(dialogView, "translationY", -500f, 0f);
        translationYAnimator.setDuration(600);
        // 创建缩放动画
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(dialogView, "scaleX", 0f, 1f);
        scaleXAnimator.setDuration(600);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(dialogView, "scaleY", 0f, 1f);
        scaleYAnimator.setDuration(600);
        // 创建动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        // 添加动画到集合中，并设置播放顺序
        animatorSet.playTogether(translationXAnimator, translationYAnimator, scaleXAnimator, scaleYAnimator);
        // 启动动画
        animatorSet.start();
    }

    public static void setDialogWindow(Context context, Dialog dialog, int model, int width, int height, boolean translate) {
        Window window = dialog.getWindow();
        if (translate) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        if (model == AUTO) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        } else {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int widthPixels = dm.widthPixels * width / WEIGHT_SUM;
            int heightPixels = dm.heightPixels * height / WEIGHT_SUM;
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.width = widthPixels;
            layoutParams.height = heightPixels;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}
