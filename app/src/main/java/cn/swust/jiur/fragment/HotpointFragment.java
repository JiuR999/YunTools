package cn.swust.jiur.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import cn.swust.jiur.databinding.FragmentHotpointBinding;

public class HotpointFragment extends BaseFragment<FragmentHotpointBinding>{
    FragmentHotpointBinding binding;
    private String[] titles = {"百度","微博","知乎","哔哩哔哩","贴吧"};
    @Override
    public void initData() {
        binding = getBinding();

        binding.viewPager2.setAdapter(new FragmentStateAdapter(getActivity()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return HotpointItemFragment.newInstance(getContext(),titles[position]);
            }

            @Override
            public int getItemCount() {
                return titles.length;
            }
        });
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(titles[position]);
                    }
                });

        tabLayoutMediator.attach();
    }

    @Override
    public FragmentHotpointBinding initBinding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentHotpointBinding.inflate(inflater,container,false);
    }
}
