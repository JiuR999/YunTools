package cn.swust.jiur.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.MyApplication;
import cn.swust.jiur.R;
import cn.swust.jiur.adapter.ThemeAdapter;
import cn.swust.jiur.databinding.FragmentMoreBinding;
import cn.swust.jiur.entity.Theme;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.impl.AdapterClickListener;
import cn.swust.jiur.utils.SharedPreferenceUtil;

public class MoreFragment extends BaseFragment<FragmentMoreBinding> implements View.OnClickListener {

    private Context context;
    private NavController controller;
    @Override
    public void initData() {
        FragmentMoreBinding binding = getBinding();
        controller = Navigation.findNavController(binding.getRoot());
        binding.about.setOnClickListener(this);
        binding.layoutJoke.setOnClickListener(this);
        ((MainActivity)getActivity()).setToolBarVisible(View.GONE);
        ((MainActivity)getActivity()).setBottomBarVisible(View.VISIBLE);
    }

    @Override
    public FragmentMoreBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentMoreBinding.inflate(inflater,container,false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.about){
            controller.navigate(R.id.navigation_about);
            ((MainActivity)getActivity()).setBottomBarVisible(View.GONE);
            //startActivity(new Intent(getActivity(), AboutActivity.class));
//            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment,new AboutFragment(context))
//                    .addToBackStack(null)
//                    .commit();
//            ft.add(R.id.fragment, new HomeFragment(context));
//            ft.show(new HomeFragment(context));
 //           ft.commitAllowingStateLoss();
        } else if(id == R.id.layout_joke){
            List<Theme> themes = initThemeData();
            View view1 = LayoutInflater.from(getActivity())
                            .inflate(R.layout.theme_select,null,false);
            ThemeAdapter adapter = new ThemeAdapter(getActivity(),themes);
            adapter.setClickListener(new AdapterClickListener() {
                @Override
                public void perform(int position) {
                    SharedPreferenceUtil.save(getActivity(),SharedPreferenceUtil.Type.INT
                    ,"theme", ThemeAdapter.key,position);
                    int theme = themes.get(position).getTheme();
                    SharedPreferenceUtil.save(getActivity(), SharedPreferenceUtil.Type.INT
                    ,"theme","theme",theme);
                    MyApplication.getInstance().setsCurrentTheme(theme);
                    ((MainActivity)getActivity()).reLoad();
                }

                @Override
                public void downMusic(int position) {

                }
            });
            RecyclerView recyclerView = view1.findViewById(R.id.recycleview_theme);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

            Dialog dialog = DialogFactory.createCustomDialog(getActivity(),"选择主题" ,view1,false);
            dialog.show();

        }
    }

    private List<Theme> initThemeData() {
        List<Theme> themes = new ArrayList<>();
        Theme theme1 = new Theme(R.style.Theme_Blue,"靛蓝色",R.color.md_theme_light_primary);
        themes.add(theme1);
        Theme theme2 = new Theme(R.style.Theme_Green,"果果绿",R.color.theme_green_primary);
        themes.add(theme2);
        Theme theme3 = new Theme(R.style.Theme_Red,"酒红色",R.color.theme_red_primary);
        themes.add(theme3);
        Theme theme4 = new Theme(R.style.Theme_Lavender,"薰衣草色",R.color.theme_lavender);
        themes.add(theme4);
        Theme theme5 = new Theme(R.style.Theme_Green2,"浅葱绿",R.color.theme_green2_primary);
        themes.add(theme5);
        return themes;
    }
}