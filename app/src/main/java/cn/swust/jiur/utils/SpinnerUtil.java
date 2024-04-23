package cn.swust.jiur.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import cn.swust.jiur.fragment.HomeFragment;

public class SpinnerUtil {
    public static void initSpinner(Context context, Spinner spinner,String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //TODO 记录之前选择的选项
        int selection = (int) SharedPreferenceUtil.readData(context, SharedPreferenceUtil.Type.INT, HomeFragment.M_CACHES,
        HomeFragment.SELECTED_MODE);
        spinner.setSelection(selection);
        //TODO 适配机型
        spinner.setDropDownVerticalOffset(-60);
    }
}
