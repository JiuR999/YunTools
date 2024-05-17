package cn.swust.jiur;

import static cn.swust.jiur.fragment.HomeFragment.HITOKOTO;
import static cn.swust.jiur.fragment.HomeFragment.M_CACHES;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

import cn.swust.jiur.databinding.ActivityMainBinding;
import cn.swust.jiur.fragment.HomeFragment;
import cn.swust.jiur.fragment.MoreFragment;
import cn.swust.jiur.impl.ItemRefreshClickListener;
import cn.swust.jiur.utils.AttributeUtils;
import cn.swust.jiur.utils.SharedPreferenceUtil;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * @author JIUR
 */
public class MainActivity extends BaseActivity {
    private ActivityMainBinding mainBinding;
    private NavController controller;
    private final String TAG = "MainActivity";
    private ItemRefreshClickListener itemRefreshClickListener;
    private Menu menu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        initNavController();
        initToolBar();
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

    /**.
     * 初始化菜单

     * @param menu The options menu in which you place your items.
     *
     * @return .
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
        float radius = 20f;
        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        //ViewGroup rootView = decorView.findViewById(R.id.blur);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        //Drawable windowBackground = decorView.getBackground();
        /*mainBinding.blurView.setupWith(rootView,
                (new RenderScriptBlur(this)))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);*/
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

    public void setToolBarBuilder(String title, String subTitle){
        // 创建一个SpannableStringBuilder
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // 添加第一部分文字，设置颜色和大小
        String part1 = title + "\n";
        builder.append(part1);

        //builder.setSpan(new TypefaceSpan(ResourcesCompat.getFont(this,R.font.mxj)),0,part1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //builder.setSpan(new ForegroundColorSpan(colorPrimary), 0, part1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 添加第二部分文字，设置颜色和大小

        builder.append(subTitle);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#776767")), builder.length() - subTitle.length(), builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new TextAppearanceSpan(this,R.style.text_h3_style), builder.length() - subTitle.length(), builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mainBinding.appbar.setTitle(builder);
    }
    public void setToolBarTitle(String title){
        int colorPrimary = AttributeUtils.getAttrColor(this, android.R.attr.colorPrimary);
        mainBinding.appbar.setTitle(title);
        mainBinding.appbar.setExpandedTitleColor(colorPrimary);
    }
    public void setToolBarTitle(SpannableStringBuilder title){
        int colorPrimary = AttributeUtils.getAttrColor(this, android.R.attr.colorPrimary);
        mainBinding.appbar.setTitle(title);
        mainBinding.appbar.setExpandedTitleColor(colorPrimary);
    }
    public void setToolBarVisible(int visible){
        mainBinding.appbar.setVisibility(visible);
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