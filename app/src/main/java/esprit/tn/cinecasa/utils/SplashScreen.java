package esprit.tn.cinecasa.utils;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.RegisterActivity;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {

    // Splash screen timer
//    private static int SPLASH_TIME_OUT = 10000;
    private static int SPLASH_TIME_OUT = 5000;
    private AlertDialog alert;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Context.SS = this;

        ImageView imgPoster = (ImageView) findViewById(R.id.imgLogo);
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        Animation animZoomOut1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move);
        animZoomOut.setAnimationListener(this);
        animZoomOut1.setAnimationListener(this);
        imgPoster.startAnimation(animZoomOut);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                    Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                    startActivity(i);
                    finish();

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}