package esprit.tn.cinecasa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;


import com.arlib.floatingsearchview.FloatingSearchView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.databinding.ActivityCenterFabBinding;
import esprit.tn.cinecasa.fragments.BaseExampleFragment;
import esprit.tn.cinecasa.fragments.FavoriteFragment;
import esprit.tn.cinecasa.fragments.HomeFragment;
import esprit.tn.cinecasa.fragments.HomeeFragment;
import esprit.tn.cinecasa.fragments.SlidingSearchResultsExampleFragment;
import esprit.tn.cinecasa.fragments.UserProfileFragment;
import esprit.tn.cinecasa.utils.SessionManager;
import esprit.tn.cinecasa.utils.SplashScreen;

public class CenterFabActivity extends AppCompatActivity implements BaseExampleFragment.BaseExampleFragmentCallbacks {
    private static final String TAG = CenterFabActivity.class.getSimpleName();
    private ActivityCenterFabBinding bind;
    private VpAdapter adapter;
    private FragmentManager fragManager;
    private Fragment fragHome, fragFav;
    private Bundle b;
    private static boolean CURRENT_TYPE = true;
    private SessionManager session;
    private Activity activity;
    private AlertDialog alert;

    //collections
    private List<Fragment> fragments;// used for ViewPager adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!isNetworkAvailable()) {
            setContentView(R.layout.no_connection);
            getSupportActionBar().hide();

            ImageButton imgBtn = (ImageButton) findViewById(R.id.refresh_page);
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(CenterFabActivity.this, CenterFabActivity.class);
                    startActivity(i);
                }
            });
        } else {
            bind = DataBindingUtil.setContentView(this, R.layout.activity_center_fab);
            getSupportActionBar().hide();

            initImageLoader();
            bind.vp.setOffscreenPageLimit(4);
            fragManager = getSupportFragmentManager();

            b = new Bundle();
            b.putString("choice", "movies");
            fragHome = new HomeFragment();
            fragHome.setArguments(b);

            initData();
            initView();
            initEvent();

            esprit.tn.cinecasa.utils.Context.MAIN_ACTIVITY = this;
            session = new SessionManager(getApplicationContext());
            activity = this;
            if (!session.isFirstTime()) {
                TapTargetView.showFor(this,
                        TapTarget.forView(findViewById(R.id.fab), "Welcome in Cinecasa", "From here you can swipe between movies and tv Shows")
                                // All options below are optional
                                .outerCircleAlpha(0.7f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                .descriptionTextSize(20)
                                .tintTarget(false)  // Specify the size (in sp) of the description text
                                .targetRadius(60),                  // Specify the target radius (in dp)
                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                view.setVisibility(View.GONE);
                                TapTargetView.showFor(activity,
                                        TapTarget.forView(findViewById(R.id.i_home), "HOME", "From here you can discover different categories of movies or tv shows")
                                                // All options below are optional
                                                .outerCircleAlpha(0.7f)            // Specify the alpha amount for the outer circle
                                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                                .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                                .descriptionTextSize(20)
                                                .tintTarget(false)  // Specify the size (in sp) of the description text
                                                .targetRadius(60),                  // Specify the target radius (in dp)
                                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                            @Override
                                            public void onTargetClick(TapTargetView view) {
                                                view.setVisibility(View.GONE);
                                                TapTargetView.showFor(activity,
                                                        TapTarget.forView(findViewById(R.id.i_search), "SEARCH", "From here you can search for movies, tv shows or actors")
                                                                // All options below are optional
                                                                .outerCircleAlpha(0.7f)            // Specify the alpha amount for the outer circle
                                                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                                                .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                                                .descriptionTextSize(20)
                                                                .tintTarget(false)  // Specify the size (in sp) of the description text
                                                                .targetRadius(60),                  // Specify the target radius (in dp)
                                                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                                            @Override
                                                            public void onTargetClick(TapTargetView view) {
                                                                view.setVisibility(View.GONE);
                                                                TapTargetView.showFor(activity,
                                                                        TapTarget.forView(findViewById(R.id.i_favor), "FAVORITE", "From here you can swipe up between your different favorite actors")
                                                                                // All options below are optional
                                                                                .outerCircleAlpha(0.7f)            // Specify the alpha amount for the outer circle
                                                                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                                                                .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                                                                .descriptionTextSize(20)
                                                                                .tintTarget(false)  // Specify the size (in sp) of the description text
                                                                                .targetRadius(60),                  // Specify the target radius (in dp)
                                                                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                                                            @Override
                                                                            public void onTargetClick(TapTargetView view) {
                                                                                view.setVisibility(View.GONE);
                                                                                TapTargetView.showFor(activity,
                                                                                        TapTarget.forView(findViewById(R.id.i_visibility), "PROFILE", "From here you can update your profile")
                                                                                                // All options below are optional
                                                                                                .outerCircleAlpha(0.7f)            // Specify the alpha amount for the outer circle
                                                                                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                                                                                .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                                                                                .descriptionTextSize(20)
                                                                                                .tintTarget(false)  // Specify the size (in sp) of the description text
                                                                                                .targetRadius(60),                  // Specify the target radius (in dp)
                                                                                        new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                                                                            @Override
                                                                                            public void onTargetClick(TapTargetView view) {
                                                                                                view.setVisibility(View.GONE);
                                                                                                session.setIntro(true);
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            }
        }
    }

    public void loadMovies() {
        fragHome = new HomeFragment();
        fragments.remove(0);
        fragments.add(0, fragHome);
        adapter.notifyDataSetChanged();
    }

    public void loadTVShows() {
        fragHome = new HomeeFragment();
        fragments.remove(0);
        fragments.add(0, fragHome);
        adapter.notifyDataSetChanged();
    }

    public void reloadFav() {
//        FavoriteFragment favoriteFragment = (FavoriteFragment) adapter.getmFragmentManager().getFragments().get(2);
        esprit.tn.cinecasa.utils.Context.FAV_FRAG.deleteFromFav();
    }

    public void fullReloadFav() {
        fragFav = new FavoriteFragment();
        for (int k = 0; k < fragments.size(); k++) {
            if (fragments.get(k) instanceof FavoriteFragment) {
                fragments.remove(k);
                fragments.add(k, fragFav);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (esprit.tn.cinecasa.utils.Context.SELECTED_TYPE && esprit.tn.cinecasa.utils.Context.SELECTED_TYPE != CURRENT_TYPE) {
            loadMovies();
            CURRENT_TYPE = !CURRENT_TYPE;
        } else if (!esprit.tn.cinecasa.utils.Context.SELECTED_TYPE && esprit.tn.cinecasa.utils.Context.SELECTED_TYPE != CURRENT_TYPE) {
            loadTVShows();
            CURRENT_TYPE = !CURRENT_TYPE;
        }

        if (esprit.tn.cinecasa.utils.Context.FAV_CHANGED) {
            fullReloadFav();
            esprit.tn.cinecasa.utils.Context.FAV_CHANGED = false;
        }
    }

    /**
     * create fragments
     */
    private void initData() {
        fragments = new ArrayList<>(4);

        // add to fragments for adapter
        fragments.add(new HomeFragment());
        fragments.add(new SlidingSearchResultsExampleFragment());
        fragments.add(new FavoriteFragment());
        fragments.add(new UserProfileFragment());
    }


    /**
     * change BottomNavigationViewEx style
     */
    private void initView() {
        bind.bnve.enableItemShiftingMode(false);
        bind.bnve.enableShiftingMode(false);
        bind.bnve.enableAnimation(false);
        bind.bnve.setTextVisibility(false);

        // set adapter
        adapter = new VpAdapter(fragManager, fragments);
        bind.vp.setAdapter(adapter);
    }

    /**
     * set listeners
     */
    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item
        bind.bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.i_home:
                        position = 0;
                        break;
                    case R.id.i_search:
                        position = 1;
                        break;
                    case R.id.i_favor:
                        position = 2;
                        break;
                    case R.id.i_visibility:
                        position = 3;
                        break;
                    case R.id.i_empty: {
                        return false;
                    }
                }
                if (previousPosition != position) {
                    bind.vp.setCurrentItem(position, false);
                    previousPosition = position;
                }

                return true;
            }
        });

        // set listener to change the current checked item of bottom nav when scroll view pager
        bind.vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position >= 2)// 2 is center
                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
                bind.bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // center item click listener
        bind.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivityNoAnimation(new Intent(CenterFabActivity.this, MainActivity.class));
                Intent i = new Intent(CenterFabActivity.this, PanelActivity.class);
                startActivityNoAnimation(i);
                //Toast.makeText(CenterFabActivity.this, "Center", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {

    }

    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentStatePagerAdapter {

        public final FragmentManager mFragmentManager;
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
            mFragmentManager = fm;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        public FragmentManager getmFragmentManager() {
            return mFragmentManager;
        }
    }

    private void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    @SuppressWarnings("deprecation")
    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(480, 800)
                // default = device screen dimensions
                .threadPoolSize(3)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13) // default
                .discCacheSize(50 * 1024 * 1024) // 缓冲大小
                .discCacheFileCount(100) // 缓冲文件数目
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();

        // 2.单例ImageLoader类的初始化
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
//
//    protected void createNetErrorDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
//                .setTitle("Unable to connect")
//                .setCancelable(false)
//                .setPositiveButton("Settings",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//                                startActivity(i);
//                            }
//                        }
//                )
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                SplashScreen.this.finish();
//                            }
//                        }
//                );
//        alert = builder.create();
//        alert.show();
//    }
}
