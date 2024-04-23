package cn.swust.jiur.utils;

import android.content.Context;
import android.util.TypedValue;

public class AttributeUtils {

    public static int getAttrColor(Context context,int attr){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr,typedValue,true);
        return typedValue.data;
    }
}
