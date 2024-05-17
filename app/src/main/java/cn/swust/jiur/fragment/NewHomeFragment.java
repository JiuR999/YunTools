package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import cn.swust.jiur.MainActivity;
import cn.swust.jiur.R;
import cn.swust.jiur.databinding.FragmentNewHomeBinding;

public class NewHomeFragment extends BaseFragment<FragmentNewHomeBinding>{
    private ArrayList<Fragment> fragments;

    @Override
    public void initData() {
        initFragment();
        ((MainActivity)getActivity()).setToolBarVisible(View.GONE);
        getBinding().homeViewpager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments == null ? 0 : fragments.size();
            }
        });
        getBinding().homeViewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position == 1){
                    getBinding().bottomNavigation2.setSelectedItemId(R.id.navigation_more);
                }else {
                    getBinding().bottomNavigation2.setSelectedItemId(R.id.navigation_home);
                }
            }
        });
        getBinding().bottomNavigation2.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.navigation_more){
                    getBinding().homeViewpager2.setCurrentItem(1);
                } else {
                    getBinding().homeViewpager2.setCurrentItem(0);
                }
                return true;
            }
        });

    }
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MoreFragment());
    }
    @Override
    public FragmentNewHomeBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentNewHomeBinding.inflate(inflater,container,false);
    }
}
