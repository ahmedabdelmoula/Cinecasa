package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.RecyclerImagesAdapter;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Context;

/**
 * Created by Yessine on 11/21/2017.
 */

public class ActorDetailsImages extends Fragment {

    private View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private String urlJsonObj;
    private ProgressDialog pDialog;
    private int id;
    private static String TAG = ActorDetailsImages.class.getSimpleName();
    List<Movie> dataSource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_actor_images, container, false);


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyler_images);

//        layoutManager = new LinearLayoutManager(getContext());
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerImagesAdapter();

        id = getArguments().getInt("id");
        urlJsonObj = "https://api.themoviedb.org/3/person/" + id + "/images?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";

        makeJsonObjectRequest();

        return view;
    }

    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response

                    JSONArray results = response.getJSONArray("profiles");

                    dataSource = new ArrayList<>();
                    for (int i = 0; i < results.length() - 1; i++) {

                        JSONObject movie = (JSONObject) results.get(i);

                        String poster_path = ("https://image.tmdb.org/t/p/w150" + movie.getString("file_path"));

                        Movie m = new Movie();
                        m.setPoster_path(poster_path);
                        dataSource.add(m);
                    }

                    RecyclerImagesAdapter.setData(dataSource);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void startActivityNoAnimation(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
