package cn.swust.jiur.utils;

import android.content.Context;

public class WindowUtils {
    /**.
     *将px转为dp.
     *
     * @param context 上下文。
     * @param px 待转换px值。
     * @return 转换结果.
     */
    public static float pxToDp(Context context, float px) {
        float densityDPI = context.getResources().getDisplayMetrics().densityDpi;
        return px / (densityDPI / 160);
    }

    /**.

     * @param context 上下文。
     * @param dp 待转换dp值。
     * @return 转换结果.
     */
    public static float dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }
}
