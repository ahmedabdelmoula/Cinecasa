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
import esprit.tn.cinecasa.adapters.CardTVShowAdapter;
import esprit.tn.cinecasa.entities.TVShow;
import esprit.tn.cinecasa.utils.AppController;


/**
 * Created by ahmed on 26-Nov-17.
 */

public class GenresTVShowsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private static String TAG = PopularMoviesFragment.class.getSimpleName();
    // Progress dialog
    private ProgressDialog pDialog;
    // temporary string to show the parsed response
    CardTVShowAdapter adapter;
    List<TVShow> tvshowList;
    Fragment fragment;
    int i=0;
    LinkedHashMap<String,Integer> hashMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_genrestvshows, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        fragment=this;
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_Action);
        String urlJson="https://api.themoviedb.org/3/discover/tv?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_genres=28";

        hashMap= new LinkedHashMap<>();
        hashMap.put("12",R.id.recycler_view_Adventure);
        hashMap.put("16",R.id.recycler_view_Animation);
        hashMap.put("35",R.id.recycler_view_Comedy);
        hashMap.put("27",R.id.recycler_view_Horror);
        hashMap.put("10749",R.id.recycler_view_Romance);
        hashMap.put("878",R.id.recycler_view_Science_Fiction);
        makeJsonObjectRequest(urlJson);

        return view;
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
                    List<TVShow> dataSource = new ArrayList<>();

                    for (int i = 0; i < 10; i++) {

                        JSONObject tvshow = (JSONObject) results.get(i);

                        String vote_count= tvshow.getString("vote_count");
                        int id= tvshow.getInt("id");
                        Double vote_average= tvshow.getDouble("vote_average");
                        String name= tvshow.getString("name");
                        Double popularity= tvshow.getDouble("popularity");
                        String poster_path=("https://image.tmdb.org/t/p/w300"+tvshow.getString("poster_path"));
                        String original_language= tvshow.getString("original_language");
                        String original_name= tvshow.getString("original_name");
                        String genre_ids= tvshow.getString("genre_ids");
                        String origin_country= tvshow.getString("origin_country");
                        String backdrop_path=("https://image.tmdb.org/t/p/w300"+tvshow.getString("backdrop_path"));
                        String overview= tvshow.getString("overview");
                        String first_air_date= tvshow.getString("first_air_date");
                        TVShow tvshow1 = new TVShow( genre_ids,  original_name,  name,  popularity,  origin_country,  vote_count,  first_air_date,  backdrop_path,  original_language,  id,  vote_average,  overview,  poster_path);

                        dataSource.add(tvshow1);

                    }
                    tvshowList=dataSource;
                    adapter  = new CardTVShowAdapter(getActivity(),tvshowList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(adapter);
                    if (i<6)
                    {
                        recyclerView = (RecyclerView) view.findViewById((new ArrayList<>(hashMap.values())).get(i));
                        String urlJson="https://api.themoviedb.org/3/discover/tv?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_genres="+(new ArrayList<>(hashMap.keySet())).get(i);
                        makeJsonObjectRequest(urlJson);
                        i++;}

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
