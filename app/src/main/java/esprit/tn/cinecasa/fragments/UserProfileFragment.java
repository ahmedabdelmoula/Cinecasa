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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.RegisterActivity;
import esprit.tn.cinecasa.adapters.RatedMovieAdapter;
import esprit.tn.cinecasa.adapters.RatedTVShowAdapter;
import esprit.tn.cinecasa.datastorage.SQLiteHandler;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.entities.TVShow;
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

    String ServerUploadPath = "http://idol-design.com/Cinecasa/Scripts/img_upload_to_server.php";
    Bitmap bitmap;
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        ImageView overflow = (ImageView) view.findViewById(R.id.logout);
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        btntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRated("movie");
            }
        });
        btnmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRated("tv");
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
            }
        });

        return view;
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
                    JSONArray results = (JSONArray) response.get("rated");
                    dataSource = new ArrayList<>();
                    for (int i = 0; i < results.length(); i++) {

                        JSONObject rated = (JSONObject) results.get(i);
                        String idrated = rated.getString("id_rated");

                        dataSource.add(idrated);

                    }

                    getRated1(type, dataSource);

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
            ratedMovieAdapter = new RatedMovieAdapter(getActivity(), dataSourceMovies);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(ratedMovieAdapter);

        } else {
            ratedTVShowAdapter = new RatedTVShowAdapter(getActivity(), dataSourceTVShow);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setAdapter(ratedTVShowAdapter);
        }
    }

    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                pDialog = ProgressDialog.show(getActivity(),"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                pDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(getActivity(),string1,Toast.LENGTH_LONG).show();



            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("aaa", "aaaa");

                HashMapParams.put("aaaa", ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);


                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

}