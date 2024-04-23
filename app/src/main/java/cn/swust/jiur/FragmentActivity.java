package cn.swust.jiur;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.swust.jiur.databinding.ActivityFragmentBinding;

public class FragmentActivity extends BaseActivity {
    public final static String TITLE = "title";
    public final static String SUB_TITLE = "subTitle";

    public final static int FRAGMENT_MUSIC = R.id.navigation_music;
    public final static int FRAGMENT_PROFILE = R.id.navigation_profile;
    public final static int FRAGMENT_WALL_PAPER = R.id.navigation_wall_paper_category;
    public final static int FRAGMENT_ARTICLE = R.id.navigation_article;
    public static int FRAGMENT_ANALY = R.id.navigation_analy_vedio;
    public final static int FRAGMENT_PROFILE_CATAGORY = R.id.navigation_picture_category;
    public static final String FRAGMEN_ID = "fragmenId";

    ActivityFragmentBinding binding;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentBinding.inflate(getLayoutInflater());
        initNaviController();
        initView();
    }

    private void initNaviController() {
        setContentView(binding.getRoot());
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //获取App bar配置：AppBarConfiguration
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity);
        //将NavController和AppBarConfiguration进行绑定
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //将需要交互的App barUI与NavController和AppBarConfiguration进行绑定
        //NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void initView() {
        Intent intent = getIntent();
        int id = intent.getIntExtra(FRAGMEN_ID,R.id.navigation_music);
        String title = intent.getStringExtra(TITLE);
        String subTitle = intent.getStringExtra(SUB_TITLE);
        modefiTitle(title, subTitle);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        navController.navigate(id);
    }

    public void modefiTitle(String title, String subTitle) {
        binding.toolbar.setTitle(title);
        binding.toolbar.setSubtitle(subTitle);
    }
}