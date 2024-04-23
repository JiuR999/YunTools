package cn.swust.jiur;

import static cn.swust.jiur.fragment.HomeFragment.HITOKOTO;
import static cn.swust.jiur.fragment.HomeFragment.M_CACHES;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.databinding.ActivityMainBinding;
import cn.swust.jiur.fragment.HomeFragment;
import cn.swust.jiur.fragment.MoreFragment;
import cn.swust.jiur.impl.ItemRefreshClickListener;
import cn.swust.jiur.myView.VerticalLineHeightSpan;
import cn.swust.jiur.utils.AttributeUtils;
import cn.swust.jiur.utils.SharedPreferenceUtil;

/**
 * @author JIUR
 */
public class MainActivity extends BaseActivity {
    private ActivityMainBinding mainBinding;

    private NavController controller;
    private final String TAG = "MainActivity";
    private List<Fragment> fragments;
    private final int lastIndex = 0;
    private ItemRefreshClickListener itemRefreshClickListener;
    private Menu menu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        initFragment();
        initNavController();
        initBottomNavigation();
        initToolBar();
        //setToolBarBuilder();
    }
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MoreFragment());
    }
    private void initToolBar() {
        setSupportActionBar(mainBinding.toolbar);
        mainBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 初始化菜单
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu,menu);
        this.menu = menu;
        menu.findItem(R.id.item_toolbar_refresh).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void setItemRefreshClickListener(ItemRefreshClickListener itemRefreshClickListener) {
        this.itemRefreshClickListener = itemRefreshClickListener;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.item_toolbar_refresh){
            itemRefreshClickListener.refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    public Menu getMenu() {
        return this.menu;
    }

    private void initBottomNavigation() {
        mainBinding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.navigation_more){
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.nav_main_container,fragments.get(1))
//                            .commit();
                    controller.navigate(R.id.navigation_more);
                } else {
                    controller.navigate(R.id.navigation_home);
                }
                return true;
            }
        });
    }

    private void initNavController() {
//        controller = Navigation.findNavController(this,R.id.nav_main_container);
        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_main_container);
        assert fragment != null;
        controller = fragment.getNavController();
        //FixFragmentNavigator fixFragmentNavigator = new FixFragmentNavigator(this, getSupportFragmentManager(), fragment.getId());
        //controller.getNavigatorProvider().addNavigator(fixFragmentNavigator);
        //controller.setGraph(R.navigation.main_navigation);
        //NavigationUI.setupWithNavController(mainBinding.bottomNavigation,controller);
    }
    public void reLoad(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * 设置标题栏以及
     * @param visible
     */
    public void setToolBarAndBottomBar(int visible){
        if(visible == View.GONE){
            mainBinding.toolbar.setVisibility(View.GONE);
            mainBinding.bottomNavigation.setVisibility(View.VISIBLE);
        } else {
            mainBinding.toolbar.setVisibility(View.VISIBLE);
            mainBinding.bottomNavigation.setVisibility(View.GONE);
        }
    }

    public void setToolBarBuilder(String title, String subTitle){
        // 创建一个SpannableStringBuilder
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // 添加第一部分文字，设置颜色和大小
        String part1 = title + "\n";
        builder.append(part1);

        //builder.setSpan(new TypefaceSpan(ResourcesCompat.getFont(this,R.font.mxj)),0,part1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //builder.setSpan(new ForegroundColorSpan(colorPrimary), 0, part1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 添加第二部分文字，设置颜色和大小
        String part2 = subTitle;

        builder.append(part2);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#776767")), builder.length() - part2.length(), builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new TextAppearanceSpan(this,R.style.text_h3_style), builder.length() - part2.length(), builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainBinding.appbar.setTitle(builder);
    }
    public void setToolBarTitle(String title){
        int colorPrimary = AttributeUtils.getAttrColor(this, androidx.appcompat.R.attr.colorPrimary);
        mainBinding.appbar.setTitle(title);
        mainBinding.appbar.setExpandedTitleColor(colorPrimary);
    }
    public void setToolBarTitle(SpannableStringBuilder title){
        int colorPrimary = AttributeUtils.getAttrColor(this, androidx.appcompat.R.attr.colorPrimary);
        mainBinding.appbar.setTitle(title);
        mainBinding.appbar.setExpandedTitleColor(colorPrimary);
    }
    public void setToolBarVisible(int visible){
        mainBinding.appbar.setVisibility(visible);
    }

    public void setBottomBarVisible(int visible) {
        mainBinding.bottomNavigation.setVisibility(visible);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"暂停"+ TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源 清除缓存
        SharedPreferenceUtil.save(MainActivity.this, SharedPreferenceUtil.Type.STRING,
                M_CACHES,HITOKOTO,"");
    }
}