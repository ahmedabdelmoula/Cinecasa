package esprit.tn.cinecasa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import esprit.tn.cinecasa.fragments.LoopViewPagerFragment;


public class MainActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();

       /* BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);

        bnve.enableItemShiftingMode(false);
        bnve.enableShiftingMode(false);
        bnve.setTextVisibility(false);
        bnve.setIconSize(24, 24);
        bnve.setItemBackground(1,0); */

        Fragment fragment = Fragment.instantiate(MainActivity.this, LoopViewPagerFragment.class.getName());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        //fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(LoopViewPagerFragment.class.getName());
        fragmentTransaction.commit();

    }
}
