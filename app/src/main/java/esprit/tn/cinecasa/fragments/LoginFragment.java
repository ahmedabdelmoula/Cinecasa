package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.BuildConfig;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.entities.User;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.RegisterActivity;
import esprit.tn.cinecasa.datastorage.SQLiteHandler;
import esprit.tn.cinecasa.technique.AppConfig;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.SessionManager;

/**
 * Created by ahmed on 09-Nov-17.
 */

public class LoginFragment extends Fragment {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister, forgotPassword;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private View view;
    private AppCompatActivity activity;
    private Fragment fragment;
    private ImageView bottomImg;
    private ImageButton facebook, lickedIn, twiter;
    private CheckBox rememberMe;
    private CallbackManager callbackManager;
    private LoginButton facebookLogin;
    private Bundle bundlefb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        fragment = this;
        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) view.findViewById(R.id.facebook_login);
        facebookLogin.setFragment(this);
        facebookLogin.setReadPermissions(Arrays.asList("email"));


//        facebook = (ImageButton) view.findViewById(R.id.facebook_icon);
//        Glide
//                .with(getContext())
//                .load(R.drawable.facebook)
//                .asBitmap()
//                .into(facebook);
facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {
        String accessToken = loginResult.getAccessToken().getToken();
        Log.i("accessToken", accessToken);
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.i("LoginActivity", response.toString());
                // Get facebook data from login
                Bundle bFacebookData = getFacebookData(object);
                bundlefb=bFacebookData;
                Context.FB_LOGIN=true;
                checkLoginfacebook(bFacebookData.getString("email"));
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        Toast.makeText(getContext(), "cancel", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(getContext(), "Please error", Toast.LENGTH_LONG).show();
    }
});
        lickedIn = (ImageButton) view.findViewById(R.id.linked_in_icon);
        Glide
                .with(getContext())
                .load(R.drawable.linked_in)
                .asBitmap()
                .into(lickedIn);

        twiter = (ImageButton) view.findViewById(R.id.twitter_icon);
        Glide
                .with(getContext())
                .load(R.drawable.twitter)
                .asBitmap()
                .into(twiter);

        bottomImg = (ImageView) view.findViewById(R.id.bottom_img);
        Glide
                .with(getContext())
                .load(R.drawable.bottom_bg)
                .asBitmap()
                .into(bottomImg);
        inputEmail = (EditText) view.findViewById(R.id.email);
        inputPassword = (EditText) view.findViewById(R.id.password);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        forgotPassword = (Button) view.findViewById(R.id.btn_reset_password);
        rememberMe = (CheckBox) view.findViewById(R.id.remember_me);
        btnLinkToRegister = (Button) view.findViewById(R.id.btn_signup);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"Lato-Light.ttf");
        inputEmail.setTypeface(tf);
        inputPassword.setTypeface(tf);
        btnLogin.setTypeface(tf);
        btnLinkToRegister.setTypeface(tf);
        rememberMe.setTypeface(tf);
        forgotPassword.setTypeface(tf);


        // Progress dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(view.getContext());

        // Session manager
        session = new SessionManager(view.getContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            String UID = session.getUID();
            Context.CURRENT_USER = db.getUserDetails(UID);
//            Context.CURRENT_USER = db.getUserDetails(UID);
            Intent intent = new Intent(getActivity(), CenterFabActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
                }
            }

        });
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Context.registerSelected  = true;
                Intent i = new Intent(getContext(), RegisterActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_LOGIN + "?email=" + email + "&password=" + password, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

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
                        session.setLogin(true, uid);
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(id, name, email, password, uid, created_at, user.getString("salt"));
                      Context.CURRENT_USER = db.getUserDetails(uid);
                        db.addUser(id, name, email, password, uid, created_at, user.getString("salt"));
                        Context.CURRENT_USER = db.getUserDetails(uid);
                        Context.CONNECTED_USER = db.getUserDetails(uid);
                        // Launch main activity
                        Intent intent = new Intent(fragment.getActivity(),
                                CenterFabActivity.class);
                        startActivity(intent);
                        fragment.getActivity().finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(fragment.getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(fragment.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(fragment.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }
        catch(JSONException e) {
            Log.d(TAG,"Error parsing JSON");
        }
        return null;
    }

    public void registerUser(final String name, final String email,
                             final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        int id = jObj.getInt("id");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(id, name, email, password, uid, created_at, user.getString("salt"));
                        Toast.makeText(getContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login fragment
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().add(R.id.container, new LoginFragment(), "LoginFragment").commit();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void checkLoginfacebook(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_FB_LOGIN + "?email=" + email, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

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
                        session.setLogin(true, uid);
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String salt = user.getString("salt");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(id, name, email, salt, uid, created_at,salt);
                        Context.CURRENT_USER = db.getUserDetails(uid);

                        Context.CONNECTED_USER = db.getUserDetails(uid);
                        // Launch main activity
                        Intent intent = new Intent(fragment.getActivity(),
                                CenterFabActivity.class);
                        startActivity(intent);
                        fragment.getActivity().finish();
                    } else {
                        System.out.println("++++++++++++++----------------");
                        LayoutInflater li = LayoutInflater.from(getContext());
                        View promptsView = li.inflate(R.layout.prompt_password, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getContext());

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText userInput = (EditText) promptsView
                                .findViewById(R.id.passwordp);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Sign In",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                System.out.println("****************************");
                                                registerUser(bundlefb.getString("first_name")+bundlefb.getString("last_name"),bundlefb.getString("email"), userInput.getText().toString().trim());

                                            }

                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(fragment.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}