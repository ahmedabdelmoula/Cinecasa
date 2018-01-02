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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.hanks.htextview.base.HTextView;
import com.hanks.htextview.scale.ScaleTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import esprit.tn.cinecasa.CenterFabActivity;
import esprit.tn.cinecasa.DetailsActivity;
import esprit.tn.cinecasa.PanelActivity;
import esprit.tn.cinecasa.utils.AppController;
import esprit.tn.cinecasa.utils.Context;
import esprit.tn.cinecasa.utils.CustomThumbCard;
import esprit.tn.cinecasa.entities.Movie;
import esprit.tn.cinecasa.R;
import esprit.tn.cinecasa.adapters.RecyclerAdapter;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;


/**
 * Created by Yessine on 11/18/2017.
 */

public class Home2Fragment extends Fragment implements AdapterView.OnItemClickListener {

    private String urlJsonObj = "https://api.themoviedb.org/3/movie/top_rated?api_key=7c408d3e3e9aec97d01604333744b592&language=en-US";
    private ProgressDialog pDialog;
    private static String TAG = Home2Fragment.class.getSimpleName();
    RecyclerView recyclerView;
    RecyclerViewHeader recyclerViewHeader;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
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

        View view = inflater.inflate(R.layout.demo_fragment_gplay_card, container, false);

        big_img = (RoundedImageView) view.findViewById(R.id.big_image_movies);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerViewHeader = (RecyclerViewHeader) view.findViewById(R.id.header);

//        layoutManager = new LinearLayoutManager(getContext());
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewHeader.attachTo(recyclerView);


        makeJsonObjectRequest();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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


                    ArrayList<Movie> dataSourcee = new ArrayList<>();

                    for (int i = 0; i < results.length() - 1; i++) {

                        JSONObject movie = (JSONObject) results.get(i);


                        String vote_count = movie.getString("vote_count");
                        int id = movie.getInt("id");
                        boolean video = movie.getBoolean("video");
                        Double vote_average = movie.getDouble("vote_average");
                        String title = movie.getString("title");
                        Double popularity = movie.getDouble("popularity");
                        String poster_path = ("https://image.tmdb.org/t/p/w150" + movie.getString("poster_path"));
                        String original_language = movie.getString("original_language");
                        String original_title = movie.getString("original_title");
                        String genre_ids = movie.getString("genre_ids");
                        String backdrop_path = "https://image.tmdb.org/t/p/w500" + movie.getString("backdrop_path");
                        boolean adult = movie.getBoolean("adult");
                        String overview = movie.getString("overview");
                        String release_date = movie.getString("release_date");

                        final Movie movie1 = new Movie(vote_count, id, video, vote_average, title, popularity, poster_path, original_language, original_title, genre_ids, adult, null, overview, release_date);
                        movie1.setBackdrop_path(backdrop_path);

                        if (i == 0) {
                            Glide
                                    .with(getContext())
                                    .load(backdrop_path)
                                    .asBitmap()
                                    .skipMemoryCache(true)
                                    .placeholder(R.drawable.ph)
                                    .skipMemoryCache( true )
                                    .into(big_img);
                            big_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Context.ITEM_MOVIE = movie1;
                                    esprit.tn.cinecasa.utils.Context.selected = 0;
                                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                                    startActivityNoAnimation(intent);
                                }
                            });
                        } else {
                            dataSourcee.add(movie1);
                        }
                    }

                    adapter = new RecyclerAdapter(dataSourcee);
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
