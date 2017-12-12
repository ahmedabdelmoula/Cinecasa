package esprit.tn.cinecasa.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.RecyclerAdapter;
import esprit.tn.cinecasa.adapters.TVShowsRecyclerAdapter;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.entities.TVShow;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.utils.CustomThumbCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by ahmed on 18-Nov-17.
 */

public class TVShowsFragment extends Fragment {

    private String urlJsonObj = "https://api.themoviedb.org/3/movie/top_rated?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private ProgressDialog pDialog;
    private static String TAG = TVShowsFragment.class.getSimpleName();
    CardView cardView0;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    List<TVShow> dataSource;
    RoundedImageView big_img;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().getString("url") != null)
            urlJsonObj = getArguments().getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tv_shows, container, false);

        TextView type = (TextView) view.findViewById(R.id.tv_type);
        if (urlJsonObj.contains("popular"))
            type.setText("Popular");
        else if (urlJsonObj.contains("top_rated"))
            type.setText("Top Rated");
        else if (urlJsonObj.contains("airing_today"))
            type.setText("Airing Today");
        else if (urlJsonObj.contains("on_the_air"))
            type.setText("On The Air");


//        cardView0 = (CardView) view.findViewById(R.id.bigTVCard);
        big_img = (RoundedImageView) view.findViewById(R.id.big_image_tv_shows);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        recyclerView =
                (RecyclerView) view.findViewById(R.id.tv_shows_recycler_view);

//        layoutManager = new LinearLayoutManager(getContext());
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TVShowsRecyclerAdapter();

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

                    JSONArray results = response.getJSONArray("results");

                    dataSource = new ArrayList<>();
                    // nsob fi movies lkol fi west arraylist
                    for (int i = 0; i < results.length()-1; i++) {

                        JSONObject tvshow = (JSONObject) results.get(i);

                        String vote_count = tvshow.getString("vote_count");
                        int id = tvshow.getInt("id");
                        Double vote_average = tvshow.getDouble("vote_average");
                        String name = tvshow.getString("name");
                        Double popularity = tvshow.getDouble("popularity");
                        String poster_path = "https://image.tmdb.org/t/p/w300" + tvshow.getString("poster_path");
                        String original_language = tvshow.getString("original_language");
                        String original_name = tvshow.getString("original_name");
                        String genre_ids = tvshow.getString("genre_ids");
                        String origin_country = tvshow.getString("origin_country");
                        String backdrop_path = "https://image.tmdb.org/t/p/w500" + tvshow.getString("backdrop_path");
                        String overview = tvshow.getString("overview");
                        String first_air_date = tvshow.getString("first_air_date");

                        final TVShow tvshow1 = new TVShow(genre_ids,
                                original_name,
                                name,
                                popularity,
                                origin_country,
                                vote_count,
                                first_air_date,
                                backdrop_path,
                                original_language,
                                id,
                                vote_average,
                                overview,
                                poster_path);
                        tvshow1.setOverview(overview);
                        tvshow1.setPoster_path(poster_path);
                        tvshow1.setBackdrop_path(backdrop_path);

                        if (i == 1) {
                            Glide
                                    .with(getContext())
                                    .load(backdrop_path)
                                    .asBitmap()
                                    .skipMemoryCache( true )
                                    .into(big_img);
                            big_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Context.ITEM_TV_SHOW = tvshow1;
                                    esprit.tn.cinecasa.utils.Context.selected = 1;
                                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                                    startActivityNoAnimation(intent);
                                }
                            });

                        } else {
                            dataSource.add(tvshow1);
                        }


                    }


                    TVShowsRecyclerAdapter.setData(dataSource);
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
