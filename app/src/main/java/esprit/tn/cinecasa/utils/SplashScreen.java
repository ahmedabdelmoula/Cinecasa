package esprit.tn.cinecasa.utils;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.RegisterActivity;
import esprit.tn.cinecasa.datastorage.SQLiteHandler;
import esprit.tn.cinecasa.technique.AppConfig;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {

    // Splash screen timer
//    private static int SPLASH_TIME_OUT = 10000;
    private static int SPLASH_TIME_OUT = 3000;
    private AlertDialog alert;
    private boolean firstTime = true;
    private SessionManager session;
    private SQLiteHandler db;
    private boolean timer = false;
    private boolean ready = false;
    private boolean accepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Context.SS = this;

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        ImageView imgPoster = (ImageView) findViewById(R.id.imgLogo);
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        Animation animZoomOut1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        animZoomOut.setAnimationListener(this);
        animZoomOut1.setAnimationListener(this);
        imgPoster.startAnimation(animZoomOut);

        if (session.isLoggedIn()) {
            checkLogin();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (ready) {
                        if (accepted) {
                            Intent intent = new Intent(getApplicationContext(),
                                    CenterFabActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else
                        timer = true;

                }
            }, SPLASH_TIME_OUT);
        } else {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                    startActivity(i);
                    finish();

                }
            }, SPLASH_TIME_OUT);
        }

    }

    private void checkLogin() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        final String e, m;
        e = session.getPrefMail();
        m = session.getPrefMdp();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_LOGIN + "?email=" + e + "&password=" + m, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    response = response.substring(10);
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session


                        // Now store the user in SQLite
                        int id = jObj.getInt("id");
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        session.setLogin(true, uid, m, e);

                        // Inserting row in users table
                        db.addUser(id, name, email, m, uid, created_at, user.getString("salt"));
                        Context.CURRENT_USER = db.getUserDetails(uid);
                        db.addUser(id, name, email, m, uid, created_at, user.getString("salt"));
                        Context.CURRENT_USER = db.getUserDetails(uid);
                        Context.CONNECTED_USER = db.getUserDetails(uid);
                        // Launch main activity

                        if (timer) {
                            Intent intent = new Intent(getApplicationContext(),
                                    CenterFabActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            ready = true;
                            accepted = true;
                        }

                    } else {
                        if (Context.FB_LOGIN)
                        {
                            LoginManager.getInstance().logOut();
                        }
                        // Error in login. Get the error message
                        session.setLogin(false, session.getUID(), "none", "none");
                        if (timer) {
                            Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            ready = true;
                            accepted = false;
                        }
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", e);
                params.put("password", m);

                return params;
            }

        };

        // Adding request to request queue
        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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