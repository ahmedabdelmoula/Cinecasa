package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.CardMovieAdapter;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.utils.AppController;

/**
 * Created by ahmed on 26-Nov-17.
 */

public class GenresMoviesFragment extends Fragment {
    private View view;
    private String urlJsonObj = "https://api.themoviedb.org/3/movie/top_rated?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private RecyclerView recyclerView;
    private static String TAG = PopularMoviesFragment.class.getSimpleName();
    // Progress dialog
    private ProgressDialog pDialog;
    // temporary string to show the parsed response
    CardMovieAdapter adapter;
    List<Movie> moviesList;
    Fragment fragment;
    int i = 0;
    LinkedHashMap<String, Integer> hashMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_genresmovies, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        fragment = this;
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_Action);
        String urlJson = "https://api.themoviedb.org/3/discover/movie?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_genres=28";

        hashMap = new LinkedHashMap<>();
        hashMap.put("10749", R.id.recycler_view_Romance);
        hashMap.put("16", R.id.recycler_view_Animation);
        hashMap.put("12", R.id.recycler_view_Adventure);
        hashMap.put("35", R.id.recycler_view_Comedy);
        hashMap.put("27", R.id.recycler_view_Horror);
        hashMap.put("878", R.id.recycler_view_Science_Fiction);
        makeJsonObjectRequest(urlJson);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private void makeJsonObjectRequest(String urlJson) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJson, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("results");
                    List<Movie> dataSource = new ArrayList<>();

                    for (int i = 0; i < 10; i++) {

                        JSONObject movie = (JSONObject) results.get(i);

                        String vote_count = movie.getString("vote_count");
                        int id = movie.getInt("id");
                        boolean video = movie.getBoolean("video");
                        Double vote_average = movie.getDouble("vote_average");
                        String title = movie.getString("title");
                        Double popularity = movie.getDouble("popularity");
                        String poster_path = ("https://image.tmdb.org/t/p/w300" + movie.getString("poster_path"));
                        String original_language = movie.getString("original_language");
                        String original_title = movie.getString("original_title");
                        String genre_ids = movie.getString("genre_ids");
                        String backdrop_path = ("https://image.tmdb.org/t/p/w300" + movie.getString("backdrop_path"));
                        boolean adult = movie.getBoolean("adult");
                        String overview = movie.getString("overview");
                        String release_date = movie.getString("release_date");
                        Movie movie1 = new Movie(vote_count, id, video, vote_average, title, popularity, poster_path, original_language, original_title, genre_ids, adult, backdrop_path, overview, release_date);

                        dataSource.add(movie1);

                    }
                    moviesList = dataSource;
                    adapter = new CardMovieAdapter(getActivity(), moviesList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(adapter);
                    if (i < 6) {
                        recyclerView = (RecyclerView) view.findViewById((new ArrayList<>(hashMap.values())).get(i));
                        String urlJson = "https://api.themoviedb.org/3/discover/movie?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_genres=" + (new ArrayList<>(hashMap.keySet())).get(i);
                        makeJsonObjectRequest(urlJson);
                        i++;
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
