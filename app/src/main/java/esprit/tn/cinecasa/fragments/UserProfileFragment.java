package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.android.volley.toolbox.StringRequest;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.RegisterActivity;
import esprit.tn.cinecasa.adapters.RatedMovieAdapter;
import esprit.tn.cinecasa.adapters.RatedTVShowAdapter;
import esprit.tn.cinecasa.datastorage.SQLiteHandler;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.entities.TVShow;
import esprit.tn.cinecasa.technique.AppConfig;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.utils.ImageProcessClass;
import esprit.tn.cinecasa.utils.SessionManager;

/**
 * Created by ahmed on 03-Dec-17.
 */

public class UserProfileFragment extends Fragment {
    View view;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager session;
    RecyclerView recyclerView;
    List<String> dataSource;
    List<Movie> dataSourceMovies;
    List<TVShow> dataSourceTVShow;
    RatedMovieAdapter ratedMovieAdapter;
    RatedTVShowAdapter ratedTVShowAdapter;
    CircleImageView profilePicture;
    private static String TAG = UserProfileFragment.class.getSimpleName();
    private String urlJsongetRated = "http://idol-design.com/Cinecasa/Scripts/SelectRatedByUserUid.php?uid_user=" + Context.CURRENT_USER.getUid() + "&type=";
    private String urlgetJsonPart1 = "https://api.themoviedb.org/3/";
    private String urlgetJsonPart2 = "?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private LinearLayout editInfos;
    private EditText name, mail, password, newPassword;
    private ImageView done;
    private boolean nameChanged, mailChanged, passwordChanged, newPasswordChanged;
    String ServerUploadPath = "http://idol-design.com/Cinecasa/Scripts/img_upload_to_server.php";
    Bitmap bitmap;
    private String updateProfileRequest = "http://idol-design.com/Cinecasa/Connection/update.php?id=" + Context.CONNECTED_USER.getId();
    private String pwd;
    private TextView nothing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // SqLite database handler
        db = new SQLiteHandler(getContext());
        System.out.println(urlJsongetRated);
        // session manager
        session = new SessionManager(getContext());
        View photoHeader = view.findViewById(R.id.photoHeader);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        profilePicture = (CircleImageView) view.findViewById(R.id.civProfilePic);
        TextView txtname = (TextView) view.findViewById(R.id.tvName);
        TextView txttitle = (TextView) view.findViewById(R.id.tvTitle);
        Button btnmovie = (Button) view.findViewById(R.id.btnMessage);
        Button btntv = (Button) view.findViewById(R.id.btnConnect);
        txtname.setText(Context.CURRENT_USER.getName());
        txttitle.setText(Context.CURRENT_USER.getEmail());
        nothing = (TextView) view.findViewById(R.id.nothing);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        editInfos = (LinearLayout) view.findViewById(R.id.edit_infos);

        ImageView overflow = (ImageView) view.findViewById(R.id.logout);
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        ImageView edit = (ImageView) view.findViewById(R.id.settings);
        done = (ImageView) view.findViewById(R.id.edit_finish);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done.setVisibility(View.GONE);
                name.setText(Context.CURRENT_USER.getName());
                mail.setText(Context.CURRENT_USER.getEmail());
                editInfos.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateProfileRequest = "http://idol-design.com/Cinecasa/Connection/update.php?id=" + Context.CURRENT_USER.getId();
                pwd = Context.CONNECTED_USER.getPassword();

                if (nameChanged)
                    updateProfileRequest += "&name=" + name.getText();


                if (mailChanged)
                    updateProfileRequest += "&email=" + mail.getText();


                if ((passwordChanged && !newPasswordChanged) || (!passwordChanged && newPasswordChanged))
                    Toast.makeText(getContext(),
                            "Please fill the old and new password",
                            Toast.LENGTH_LONG).show();
                else if (password.getText().length() >= 6 && newPassword.getText().length() >= 6) {
                    updateProfileRequest += "&new_password=" + newPassword.getText() + "&old_password=" + password.getText() + "&salt=" + Context.CONNECTED_USER.getSalt();
                    pwd = newPassword.getText().toString();
                } else {
                    Toast.makeText(getContext(),
                            "Password characters must be more than 5",
                            Toast.LENGTH_LONG).show();
                }

                updateProfile();

            }
        });

        name = (EditText) view.findViewById(R.id.edit_name);
        password = (EditText) view.findViewById(R.id.edit_password);
        mail = (EditText) view.findViewById(R.id.edit_mail);
        newPassword = (EditText) view.findViewById(R.id.edit_new_password);

        name.setText(Context.CURRENT_USER.getName());
        mail.setText(Context.CURRENT_USER.getEmail());

        done.setVisibility(View.GONE);
        editInfos.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        getRated("movie");
        btntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done.setVisibility(View.GONE);
                editInfos.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                getRated("movie");
            }
        });
        btnmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done.setVisibility(View.GONE);
                editInfos.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                getRated("tv");
            }
        });

//        profilePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//
//                intent.setType("image/*");
//
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
//            }
//        });

        TextView firstLetter = (TextView) view.findViewById(R.id.first_letter);

        firstLetter.setText(Context.CURRENT_USER.getName().substring(0,1).toUpperCase());

        initListeners();

        return view;
    }

    private void initListeners() {

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!Context.CURRENT_USER.getName().equals(name.getText().toString())) {
                    nameChanged = true;
                    Toast.makeText(getContext(),
                            Context.CURRENT_USER.getName() + " != " + name.getText().toString(),
                            Toast.LENGTH_LONG).show();
                    done.setVisibility(View.VISIBLE);
                } else if (!passwordChanged && !mailChanged && !newPasswordChanged) {
                    done.setVisibility(View.GONE);
                    nameChanged = false;
                } else {
                    nameChanged = false;
                }
            }
        });
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!Context.CURRENT_USER.getEmail().equals(mail.getText().toString())) {
                    mailChanged = true;
                    done.setVisibility(View.VISIBLE);
                } else if (!passwordChanged && !newPasswordChanged && !nameChanged) {
                    done.setVisibility(View.GONE);
                    mailChanged = false;
                } else {
                    mailChanged = false;
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (password.getText().length() > 0) {
                    passwordChanged = true;
                    done.setVisibility(View.VISIBLE);
                } else if (!newPasswordChanged && !mailChanged && !nameChanged) {
                    done.setVisibility(View.GONE);
                    passwordChanged = false;
                } else {
                    passwordChanged = false;
                }
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (newPassword.getText().length() > 0) {
                    newPasswordChanged = true;
                    done.setVisibility(View.VISIBLE);
                } else if (!passwordChanged && !mailChanged && !nameChanged) {
                    done.setVisibility(View.GONE);
                    newPasswordChanged = false;
                } else {
                    newPasswordChanged = false;
                }
            }
        });

    }


    @Override
    public void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == getActivity().RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                profilePicture.setImageBitmap(bitmap);

                ImageUploadToServerFunction();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private void logoutUser() {
        session.setLogin(false, Context.CURRENT_USER.getUid());

        db.deleteUsers();

        // Launching the register activity
        Intent i = new Intent(getContext(), RegisterActivity.class);
        startActivity(i);
        getActivity().finish();
        if (Context.FB_LOGIN)
        {
            LoginManager.getInstance().logOut();
        }
    }


    private void getRated(final String type) {
        String x = urlJsongetRated + type;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, x, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (!response.getBoolean("error")) {
                        nothing.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        JSONObject result = (JSONObject) response.get("result");

                        if (result.get("id_rated") instanceof JSONArray)
                        {
                        dataSource = new ArrayList<>();
                        JSONArray ids_rated = (JSONArray) result.get("id_rated");
                        JSONArray values = (JSONArray) result.get("value");
                        for (int i = 0; i < ids_rated.length(); i++) {
                            String idrated = (String) ids_rated.get(i);
                            dataSource.add(idrated);
                        }
                        }
                        else
                        {
                            dataSource = new ArrayList<>();
                            String id_rated = (String) result.get("id_rated");
                            String value = (String) result.get("value");
                                dataSource.add(id_rated);

                        }

                        getRated1(type, dataSource);
                    }
                    else
                        recyclerView.setVisibility(View.GONE);
                        nothing.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq, "hello");
    }

    private void getRated1(final String type, List<String> listid) {
        dataSourceMovies = new ArrayList<>();
        dataSourceTVShow = new ArrayList<>();
        for (String s : listid) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlgetJsonPart1 + type + "/" + s + urlgetJsonPart2, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (type.equals("movie")) {
                            String vote_count = response.getString("vote_count");
                            int id = response.getInt("id");
                            boolean video = response.getBoolean("video");
                            Double vote_average = response.getDouble("vote_average");
                            String title = response.getString("title");
                            Double popularity = response.getDouble("popularity");
                            String poster_path = ("https://image.tmdb.org/t/p/w300" + response.getString("poster_path"));
                            String original_language = response.getString("original_language");
                            String original_title = response.getString("original_title");
                            String genre_ids = response.getString("genres");
                            String backdrop_path = ("https://image.tmdb.org/t/p/w300" + response.getString("backdrop_path"));
                            boolean adult = response.getBoolean("adult");
                            String overview = response.getString("overview");
                            String release_date = response.getString("release_date");
                            dataSourceMovies.add(new Movie(vote_count, id, video, vote_average, title, popularity, poster_path, original_language, original_title, genre_ids, adult, backdrop_path, overview, release_date));
                            if (dataSourceMovies.size() == dataSource.size())
                                affiche(type);
                        } else {
                            String vote_count = response.getString("vote_count");
                            int id = response.getInt("id");
                            Double vote_average = response.getDouble("vote_average");
                            String name = response.getString("name");
                            Double popularity = response.getDouble("popularity");
                            String poster_path = ("https://image.tmdb.org/t/p/w300" + response.getString("poster_path"));
                            String original_language = response.getString("original_language");
                            String original_name = response.getString("original_name");
                            String genre_ids = response.getString("genres");
                            String origin_country = response.getString("origin_country");
                            String backdrop_path = ("https://image.tmdb.org/t/p/w300" + response.getString("backdrop_path"));
                            String overview = response.getString("overview");
                            String first_air_date = response.getString("first_air_date");
                            dataSourceTVShow.add(new TVShow(genre_ids, original_name, name, popularity, origin_country, vote_count, first_air_date, backdrop_path, original_language, id, vote_average, overview, poster_path));
                            if (dataSourceTVShow.size() == dataSource.size())
                                affiche(type);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                }
            });


            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }

    }


    public void affiche(String type) {
        if (type.equals("movie")) {
            nothing.setVisibility(View.GONE);
            ratedMovieAdapter = new RatedMovieAdapter(getActivity(), dataSourceMovies);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(ratedMovieAdapter);

        } else {
            nothing.setVisibility(View.GONE);
            ratedTVShowAdapter = new RatedTVShowAdapter(getActivity(), dataSourceTVShow);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setAdapter(ratedTVShowAdapter);
        }
    }

    public void ImageUploadToServerFunction() {

        ByteArrayOutputStream byteArrayOutputStreamObject;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                pDialog = ProgressDialog.show(getActivity(), "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                pDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(getActivity(), string1, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<>();

                HashMapParams.put("aaa", "aaaa");

                HashMapParams.put("aaaa", ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);


                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }


    private void updateProfile() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                updateProfileRequest,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        pDialog.dismiss();
                        done.setVisibility(View.GONE);
                        checkLogin(mail.getText().toString(), pwd);

                        password.getText().clear();
                        newPassword.getText().clear();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        db.deleteUser(Context.CONNECTED_USER.getId());

        pDialog = new ProgressDialog(getActivity());
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_LOGIN + "?email=" + email + "&password=" + password, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                pDialog.dismiss();

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
                        Context.CONNECTED_USER = db.getUserDetails(uid);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }

                    pDialog.dismiss();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();
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
        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}